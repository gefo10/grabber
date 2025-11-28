package com.grabbler.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  @Value("${jwt.secret}")
  private String SECRET_KEY;

  @Value("${jwt.expiration}")
  private Long JWT_EXPIRATION;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
  }

  public String extractSubject(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  @Deprecated
  public String generateToken(String username) {
    Map<String, Object> claims = Map.of();
    return createToken(claims, username);
  }

  /**
   * Generate JWT token for e-commerce user with relevant claims
   *
   * @param userId - Unique user identifier
   * @param username - Username for display
   * @param email - User email (used as subject)
   * @param roles - User roles (e.g., CUSTOMER, SELLER, ADMIN)
   * @param customerId - Customer profile ID (if different from userId)
   * @return JWT token string
   */
  public String generateToken(
      String username, String email, List<String> roles, String customerId) {

    Map<String, Object> claims = new HashMap<>();

    // Standard useful claims for e-commerce
    claims.put("username", username);
    // claims.put("email", email);
    claims.put("roles", roles);
    claims.put("customerId", customerId);

    // Optional: Add cart or session identifiers
    claims.put("sessionId", UUID.randomUUID().toString());

    return Jwts.builder()
        .claims(claims)
        .subject(email)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
        .signWith(getSigningKey())
        .compact();
  }

  /** Generate token with additional custom claims */
  public String generateTokenWithCustomClaims(
      String username, String email, List<String> roles, Map<String, Object> customClaims) {

    Map<String, Object> claims = new HashMap<>(customClaims);
    claims.put("username", username);
    // claims.put("email", email);
    claims.put("roles", roles);

    return Jwts.builder()
        .claims(claims)
        .subject(email)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
        .signWith(getSigningKey())
        .compact();
  }

  private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .issuer("Grabbler")
        .claims(claims)
        .subject(subject)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
        .signWith(getSigningKey())
        .compact();
  }

  public Boolean validateToken(String token, String email) {
    return (email.equals(extractSubject(token)) && !isTokenExpired(token));
  }
}
