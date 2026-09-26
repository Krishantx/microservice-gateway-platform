package com.krishantx.github.com.API_Gateway.service;

import java.util.Date;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private static final String SECRET = System.getenv().getOrDefault("JWT_SECRET", "dev-secret-change-me-hs256-min-32-bytes");
  private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
  private static final long EXPIRATION_TIME = 30 * 60 * 1000;

  public String validateToken(String token) throws JwtException {
    String username = new String();
    try {
      username = getUsername(token);
    } catch (MalformedJwtException e) {
      throw e;
    }
    Date exp = getExpiration(token);
    Date currDate = new Date(System.currentTimeMillis());
    if (currDate.after(exp)) {
      throw new ExpiredJwtException(null, null, null);
    }
    return username;

  }

  public static String generateToken(String username) {
    return generateToken(username, EXPIRATION_TIME);
  }

  public static String generateToken(String username, long ttlMillis) {
    return Jwts.builder()
        .subject(username)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + ttlMillis))
        .signWith(KEY)
        .compact();
  }

  public String getUsername(String token) {
    return getClaims(token).getSubject();
  }

  public Date getExpiration(String token) {
    return getClaims(token).getExpiration();
  }

  public Claims getClaims(String token) {
    return Jwts.parser()
        .verifyWith(KEY)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}
