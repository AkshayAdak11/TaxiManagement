package com.taxifleet.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JWTUtils {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public static String generateToken(String subject, String role) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("roles", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                .signWith(SECRET_KEY)
                .compact()
                .replaceAll("\\s", "");
    }

    public static String extractRoles(String token) {
        Jws<Claims> jwsClaims = Jwts.parser()
                .setSigningKey(SECRET_KEY).build()
                .parseClaimsJws(token);
        Claims claims = jwsClaims.getBody();
        return claims.get("roles", String.class);
    }

    public static void validateToken(String token) {
        Jwts.parser()
                .setSigningKey(SECRET_KEY).build()
                .parseClaimsJws(token);
    }
}