package com.taxifleet.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.SignatureException;
import java.util.Date;

public class JWTUtils {

    private static final String SECRET_KEY = "your-secret-key";

    public static String generateToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static String extractRoles(String token) {
        try {
            Claims claims = (Claims) Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return claims.getSubject();
        } catch (Exception e) {
            throw e;

        }
    }
}
