package com.snackbar.iam.application;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Legacy JWT service that delegates to the new JwtService in the infrastructure layer.
 * This class is kept for backward compatibility during refactoring.
 * 
 * @deprecated Will be removed once all dependencies are migrated to use
 *             {@link com.snackbar.iam.infrastructure.security.JwtService} directly.
 */
@Service("legacyJwtService")
@Deprecated
public class JwtService {

    @Autowired
    private com.snackbar.iam.infrastructure.security.JwtService newJwtService;

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return newJwtService.extractUsername(token);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return newJwtService.extractClaim(token, claimsResolver);
    }

    public String generateToken(UserDetails userDetails) {
        return newJwtService.generateToken(userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return newJwtService.generateToken(extraClaims, userDetails);
    }

    public long getExpirationTime() {
        return newJwtService.getExpirationTime();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return newJwtService.isTokenValid(token, userDetails);
    }

    // Keep these private methods for backward compatibility in case any subclass uses them
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
