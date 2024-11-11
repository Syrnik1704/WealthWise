package com.example.wealthwise_api.Services;

import com.example.wealthwise_api.Entity.AccessToken;
import com.example.wealthwise_api.Entity.RefreshToken;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.Repository.JWTokenRefreshRepository;
import com.example.wealthwise_api.Repository.JWTokenAccessRepository;
import com.example.wealthwise_api.Util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LogoutService implements LogoutHandler {

    private final JWTUtil jwtUtil;
    Logger logger = LoggerFactory.getLogger(LogoutService.class);
    private final UserServiceDetails userServiceDetails;
    private final JWTokenAccessRepository jwtTokenAccessRepository;
    private final JWTokenRefreshRepository jwtTokenRefreshRepository;

    public LogoutService(JWTUtil jwtUtil, UserServiceDetails userServiceDetails,
                         JWTokenAccessRepository jwtTokenAccessRepository,
                         JWTokenRefreshRepository jwtTokenRefreshRepository) {
        this.jwtUtil = jwtUtil;
        this.userServiceDetails = userServiceDetails;
        this.jwtTokenAccessRepository = jwtTokenAccessRepository;
        this.jwtTokenRefreshRepository = jwtTokenRefreshRepository;
    }

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        try {

            final String authHeader = request.getHeader("Authorization");
       
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Zwraca 400 jeśli token nie istnieje
                response.getWriter().write("Authorization header missing or not valid.");
                return;
            }

            String jwt = authHeader.substring(7);
            String email = jwtUtil.getEmail(jwt); // email extracted from JWT
            logger.warn("logout - email " + email);
            if (email != null) {
                // Sprawdź, czy tokeny istnieją w bazie
                AccessToken accessToken = jwtTokenAccessRepository.findBySubject(email);
                RefreshToken refreshToken = jwtTokenRefreshRepository.findBySubject(email);

                // Jeśli tokenów nie ma, oznacza to, że użytkownik został już wylogowany
                if (accessToken == null && refreshToken == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Zwraca 400 jeśli tokeny nie istnieją
                    response.getWriter().write("User is already logged out or token not found.");
                    return;
                }

                // Sprawdzenie, czy access token jest ważny
                if (jwtUtil.isAccessTokenValid(jwt)) {
                    // Usuwanie tokenów z bazy danych
                    if (refreshToken != null) {
                        jwtTokenRefreshRepository.delete(refreshToken);
                        logger.info("Refresh token deleted for user: " + email);
                    }

                    if (accessToken != null) {
                        jwtTokenAccessRepository.delete(accessToken);
                        logger.info("Access token deleted for user: " + email);
                    }

                    // Czyszczenie SecurityContext i zwrot statusu 200
                    SecurityContextHolder.clearContext();
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("User has been logged out successfully.");
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Zwraca 401 dla niepoprawnego tokena
                    response.getWriter().write("Invalid or expired access token.");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Zwraca 401 jeśli nie ma zalogowanego użytkownika
                response.getWriter().write("User is not logged in or token is invalid.");
            }
        } catch (Exception e) {
            logger.error("logout - exception: ", e); // Pełny stack trace dla diagnostyki
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Zwraca 500 dla wewnętrznych błędów serwera
            try {
                response.getWriter().write("An error occurred during logout.");
            } catch (IOException ioException) {
                logger.error("Error writing response: ", ioException);
            }
        } finally {
            // Zawsze czyści kontekst, nawet jeśli wystąpił wyjątek
            SecurityContextHolder.clearContext();
        }
    }
}
