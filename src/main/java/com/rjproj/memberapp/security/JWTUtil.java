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
        Object permissionsClaim = claims.get("permissions");

        // Check if the permissions claim is indeed a List
        if (permissionsClaim instanceof List<?>) {
            // Check if the list contains only strings
            List<?> permissionsList = (List<?>) permissionsClaim;
            if (permissionsList.stream().allMatch(item -> item instanceof String)) {
                @SuppressWarnings("unchecked") // Suppress warning after ensuring type safety
                List<String> permissions = (List<String>) permissionsList;
                return permissions;
            }
        }
        return Collections.emptyList(); // Return an empty list if no valid permissions are found
    }

    public UUID extractSelectedOrganizationId(String token) {
        Claims claims = extractAllClaims(token);
        if (claims.get("selectedOrganizationId") != null) {
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
        claims.put("permissions", permissions);
        claims.put("selectedOrganizationId", selectedOrganizationId);
        claims.put("memberId", memberId);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        token = Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
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