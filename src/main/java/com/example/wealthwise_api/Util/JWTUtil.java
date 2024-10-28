package com.example.wealthwise_api.Util;

import com.example.wealthwise_api.Entity.AccessToken;
import com.example.wealthwise_api.Entity.RefreshToken;
import com.example.wealthwise_api.Repository.JWTokenAccessRepository;
import com.example.wealthwise_api.Repository.JWTokenRefreshRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JWTUtil {

    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);
    private final JWTokenAccessRepository jwTokenAccessRepository;
    private final JWTokenRefreshRepository jwtTokenRefreshRepository;

    private static final String SECRET_KEY = "maka_987654321_maka_987654321_maka_987654321_maka_987654321";
    private static final long ACCESS_TOKEN_EXPIRATION = 15 * 60 * 1000; // Czas ważności tokenu dostępu (15 minut)
    private static final long REFRESH_TOKEN_EXPIRATION = 5 * 24 * 60 * 60 * 1000; // Czas ważności tokenu odświeżania (30 dni)

    public JWTUtil(JWTokenAccessRepository jwTokenAccessRepository, JWTokenRefreshRepository jwtTokenRefreshRepository) {
        this.jwTokenAccessRepository = jwTokenAccessRepository;
        this.jwtTokenRefreshRepository = jwtTokenRefreshRepository;
    }

    // Generowanie tokenu z polami name, isActive, email, role
    public String issueToken(String name, boolean isActive, String email, String role) {
        Map<String, Object> claims = Map.of(
                "name", name,
                "isActive", isActive,
                "email", email,
                "role", role
        );
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Generowanie tokenu odświeżania bez dodatkowych roszczeń
    public String issueRefreshToken(String email, String role) {
        return Jwts.builder()
                .claim("email", email)
                .claim("role",role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Ekstrakcja informacji z tokenu
    public String getName(String token) {
        return getClaims(token).get("name", String.class);
    }

    public boolean getIsActive(String token) {
        return getClaims(token).get("isActive", Boolean.class);
    }

    public String getEmail(String token) {
        return getClaims(token).get("email", String.class);
    }

    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // Walidacja tokenów
    public boolean isAccessTokenValid(String jwt) {
        AccessToken accessToken = jwTokenAccessRepository.findByToken(jwt);
        return accessToken != null && accessToken.getExpirationTime().after(new Date());
    }

    public boolean isRefreshTokenValid(String jwt) {
        RefreshToken refreshToken = jwtTokenRefreshRepository.findByToken(jwt);
        return refreshToken != null && refreshToken.getExpirationTime().after(new Date());
    }

    // Ekstrakcja daty wygaśnięcia tokenu
    public Date extractExpiration(String token) {
        return getClaims(token).getExpiration();
    }

    // Usuwanie tokenów
    public void deleteAccessToken(String email) {
        AccessToken accessToken = jwTokenAccessRepository.findBySubject(email);
        if (accessToken != null) {
            jwTokenAccessRepository.delete(accessToken);
        }
    }

    public void deleteRefreshToken(String email) {
        RefreshToken refreshToken = jwtTokenRefreshRepository.findBySubject(email);
        if (refreshToken != null) {
            jwtTokenRefreshRepository.delete(refreshToken);
        }
    }

    // Metoda pomocnicza do uzyskiwania roszczeń z tokenu
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Metoda pomocnicza do uzyskiwania klucza do podpisywania
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}
