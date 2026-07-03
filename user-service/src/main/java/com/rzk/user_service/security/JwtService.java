package com.rzk.user_service.security;

import com.rzk.user_service.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private static final String SECRET =
            "rzk_user_service_super_secret_key_32_chars_min!!!";

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public String generateToken(User user) {
        long now = System.currentTimeMillis();
        long exp = now + (60L * 60L * 1000L); // 1h

        String roles = user.getRoles().stream()
                .map(r -> {
                    String name = r.getName();
                    return name.startsWith("ROLE_") ? name : "ROLE_" + name;
                })
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("roles", roles)
                .issuedAt(new Date(now))
                .expiration(new Date(exp))
                .signWith(key)
                .compact();
    }
}