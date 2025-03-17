package com.rjproj.memberapp.security;

import com.rjproj.memberapp.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class JWTUtil {

    @Value("${jwt.secret}")
    private String jwtSecretKey;

    @Value("${jwt.expiration}")
    private long jwtExpirationTime;

    @Value("${management.tracing.zipkin.tracing.endpoint:NOT SET}")
    private String zipkinEndpoint;

    private String token;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
    }

    @PostConstruct
    public void logZipkinEndpoint() {
        System.out.println("Resolved Zipkin Endpoint: " + zipkinEndpoint);
    }

    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public List<String> extractPermissions(String token) {
        Claims claims = extractAllClaims(token);
        if(claims.get("permissions") != null) {
            return (List<String>) claims.get("permissions");
        }
        return null;

    }

    public UUID extractSelectedOrganizationId(String token) {
        Claims claims = extractAllClaims(token);
        if(claims.get("selectedOrganizationId") != null) {
            return UUID.fromString((String) claims.get("selectedOrganizationId"));
        }
        return null;
    }

    public UUID extractMemberId(String token) {
        Claims claims = extractAllClaims(token);
        return UUID.fromString((String) claims.get("memberId"));
    }

    public UUID extractMemberIdInternally() {
        Claims claims = extractAllClaims(this.token);
        return UUID.fromString((String) claims.get("memberId"));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username, Role role, List<String> permissions, UUID selectedOrganizationId, UUID memberId) {
        Map<String, Object> claims = new HashMap<>();
        //claims.put("role", role);
        claims.put("permissions", permissions);
        claims.put("selectedOrganizationId", selectedOrganizationId);
        claims.put("memberId", memberId);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        token = Jwts.builder()
                .claims(claims)
                .subject(subject)
                .header().empty().add("typ","JWT")
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationTime)) // 5 minutes expiration time
                .signWith(getSigningKey())
                .compact();
        return token;
    }

    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    public void deleteToken() {
        this.token = null;
    }

    public String getToken() {
        return token;
    }
}
