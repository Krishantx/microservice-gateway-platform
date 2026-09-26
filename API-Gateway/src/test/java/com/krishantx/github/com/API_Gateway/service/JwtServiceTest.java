package com.krishantx.github.com.API_Gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

class JwtServiceTest {

  private JwtService jwtService;

  @BeforeEach
  void setUp() {
    jwtService = new JwtService();
  }

  @Test
  void generateToken_defaultTtl_producesTokenWithUsernameAndFutureExpiry() {
    String token = JwtService.generateToken("krishant");
    assertNotNull(token);
    assertEquals("krishant", jwtService.getUsername(token));
    assertTrue(jwtService.getExpiration(token).after(new Date()));
  }

  @Test
  void generateToken_withTtl_setsExpiryAroundNowPlusTtl() {
    long ttlMillis = 60_000;
    String token = JwtService.generateToken("alice", ttlMillis);
    long diff = jwtService.getExpiration(token).getTime() - new Date().getTime();
    assertTrue(diff > 0, "expiry should be in the future");
    assertTrue(diff <= ttlMillis + 1000, "expiry should not exceed ttl");
  }

  @Test
  void generateToken_zeroTtl_createsAlreadyExpiredToken() {
    String token = JwtService.generateToken("dave", 0);
    assertThrows(ExpiredJwtException.class, () -> jwtService.validateToken(token));
  }

  @Test
  void validateToken_validToken_returnsUsername() {
    String token = JwtService.generateToken("bob", 60_000);
    assertEquals("bob", jwtService.validateToken(token));
  }

  @Test
  void validateToken_expiredToken_throwsExpiredJwtException() {
    String token = JwtService.generateToken("bob", -1000);
    assertThrows(ExpiredJwtException.class, () -> jwtService.validateToken(token));
  }

  @Test
  void validateToken_malformedToken_throwsMalformedJwtException() {
    assertThrows(MalformedJwtException.class, () -> jwtService.validateToken("not-a-jwt"));
  }

  @Test
  void getClaims_parsesSubjectAndExpiration() {
    String token = JwtService.generateToken("carol", 60_000);
    Claims claims = jwtService.getClaims(token);
    assertEquals("carol", claims.getSubject());
    assertNotNull(claims.getExpiration());
  }
}