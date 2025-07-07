package com.backend.tracker.service;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    // 1. Method is jus use secret Key application.properties file that would be
    // saved into .gitIgnore later.
    @Value("${jwt.secret}")
    private String secretKey;

    // 2. Method is to use secret key.
    // private String secretKey = "";

    // public JwtService() {
    // try {
    // KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
    // SecretKey sK = keyGen.generateKey();
    // secretKey = Base64.getEncoder().encodeToString(sK.getEncoded());
    // } catch (NoSuchAlgorithmException e) {
    // logger.error("Error generating key: " + e.getMessage());
    // throw new RuntimeException("Error generating key", e);
    // }
    // }

    public String generateToken(String username) {

        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new java.util.Date(System.currentTimeMillis()))
                .expiration(new java.util.Date(System.currentTimeMillis() + 3600 * 60 * 2)) // 2 hr
                .and()
                .signWith(getKey())
                .compact();

        // ========================> Old Method <========================
        // Jwts.builder()
        // .setClaims(claims)
        // .setSubject(username)
        // .setIssuedAt(new java.util.Date(System.currentTimeMillis()))
        // .setExpiration(new java.util.Date(System.currentTimeMillis() + 60 * 60
        // * 10)) // 10 minutes
        // .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, "secretkey")
        // .compact();

    }

    private SecretKey getKey() {

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {

        return extractClaims(token, Claims::getSubject);

    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                // .setSigningKey(getKey())
                .verifyWith(getKey())
                .build()
                // .parseClaimsJws(token)
                .parseSignedClaims(token)
                // .getBody()
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        // return extractAllClaims(token).getExpiration().before(new java.util.Date());

        // 2nd method to check token expired or not.
        return extractExpiration(token).before(new java.util.Date());
    }

    // 2nd method to check token expired or not.
    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

}
