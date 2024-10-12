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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MINUTES;

@Service
public class JWTUtil {

    Logger logger = LoggerFactory.getLogger(JWTUtil.class);
    private final JWTokenAccessRepository jwTokenAccessRepository;
    private final JWTokenRefreshRepository jwtTokenRefreshRepository;

    private static final String SECRET_KEY= "maka_987654321_maka_987654321_maka_987654321_maka_987654321";
    private static final long ACCESS_TOKEN_EXPIRATION = 15 * 60 * 1000; // Czas ważności tokenu dostępu (15 minut)
    private static final long REFRESH_TOKEN_EXPIRATION = 5 * 24 * 60 * 60 * 1000; // Czas ważności tokenu odświeżania (30 dni)

    public JWTUtil(JWTokenAccessRepository jwTokenAccessRepository, JWTokenRefreshRepository jwtTokenRefreshRepository) {
        this.jwTokenAccessRepository = jwTokenAccessRepository;
        this.jwtTokenRefreshRepository = jwtTokenRefreshRepository;
    }

    public String issueToken(String subject, String ...scopes) {
        return issueToken(subject, Map.of("scopes", scopes));
    }

    public String issueToken(String subject,Map<String, Object> claims){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    public String issueRefreshToken(String subject){
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public boolean isAccessTokenValid(String jwt) {
        AccessToken accessToken = jwTokenAccessRepository.findByToken(jwt);
        if(accessToken != null &&accessToken.getExpirationTime().after(new Date())){
            return true;
        }

        return false;
    }

    public boolean isRefreshTokenValid(String jwt) {
        RefreshToken refreshToken = jwtTokenRefreshRepository.findByToken(jwt);
        if(refreshToken != null && refreshToken.getExpirationTime().after(new Date())){
            return true;
        }
        return false;
    }

    public Date extractExpiration(String token) {
        return getClaims(token).getExpiration();
    }

    public void deleteAccessToken(String subject){
        AccessToken accessToken = jwTokenAccessRepository.findBySubject(subject);
        jwTokenAccessRepository.delete(accessToken);

    }

    public void deleteRefreshToken(String subject){
        RefreshToken refreshToken = jwtTokenRefreshRepository.findBySubject(subject);
        jwtTokenRefreshRepository.delete(refreshToken);
    }

}