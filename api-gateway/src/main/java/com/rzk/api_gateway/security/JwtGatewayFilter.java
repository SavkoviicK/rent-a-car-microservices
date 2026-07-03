package com.rzk.api_gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtGatewayFilter implements GlobalFilter, Ordered {

    private static final String SECRET =
            "rzk_user_service_super_secret_key_32_chars_min!!!";

    private final SecretKey key =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    private static final Logger log = LoggerFactory.getLogger(JwtGatewayFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        // Saljemo OPTIONS request da proverimo da li je dozvoljeno
        if (method == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        // Public endpoints (bez tokena)
        if (path.startsWith("/auth/")
                || path.equals("/auth")
                || path.startsWith("/actuator/")
                || path.equals("/actuator")
                || path.startsWith("/swagger")
                || path.startsWith("/v3/api-docs")) {
            return chain.filter(exchange);
        }

        // Public read-only (GET) endpoints
        if (method == HttpMethod.GET && isPublicGet(path)) {
            return chain.filter(exchange);
        }

        // Bearer token
        final String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("Missing/invalid Authorization header for path={}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        final String token = authHeader.substring(7).trim();

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String email = claims.getSubject();
            String roles = claims.get("roles", String.class);

            email = (email == null) ? "" : email.trim();
            roles = (roles == null) ? "" : roles.trim();

            log.debug("JWT OK path={} email={} roles={}", path, email, roles);

            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Email", email)
                    .header("X-User-Roles", roles)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            log.debug("JWT INVALID path={} msg={}", path, e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isPublicGet(String path) {
        return path.equals("/vehicles") || path.startsWith("/vehicles/")
                || path.equals("/categories") || path.startsWith("/categories/")
                || path.equals("/locations") || path.startsWith("/locations/");
    }

    @Override
    public int getOrder() {
        return -1;
    }

}