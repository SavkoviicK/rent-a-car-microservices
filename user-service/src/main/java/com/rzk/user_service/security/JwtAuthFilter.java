package com.rzk.user_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String SECRET =
            "rzk_user_service_super_secret_key_32_chars_min!!!";

    private final SecretKey key =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();

        // ne filtriraj ove rute - jvne su, ne pokrece se jwt
        return uri.startsWith("/auth/") || uri.startsWith("/actuator/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // za svaki request brise prethodnu autentifikaciju
        SecurityContextHolder.clearContext();

        String authHeader = request.getHeader("Authorization");

        // nema tokena -> samo pusti dalje (SecurityConfig odlucuje, ili je dozvoljen ili je 401)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7).trim();

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String email = claims.getSubject();
            if (email == null || email.isBlank()) {
                filterChain.doFilter(request, response);
                return;
            }

            List<SimpleGrantedAuthority> authorities = toAuthorities(claims.get("roles"));

            var authentication =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception ignored) {
            // los token -> ostaje neautentikovan (nema 403 ovde)
        }

        filterChain.doFilter(request, response);
    }

    private List<SimpleGrantedAuthority> toAuthorities(Object rolesObj) {
        if (rolesObj == null) return Collections.emptyList();

        if (rolesObj instanceof String rolesStr) {
            if (rolesStr.isBlank()) return Collections.emptyList();
            return Arrays.stream(rolesStr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(this::toRole)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        if (rolesObj instanceof List<?> list) {
            return list.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(this::toRole)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private String toRole(String r) {
        return r.startsWith("ROLE_") ? r : "ROLE_" + r;
    }
}