package com.example.wealthwise_api.Services;

import com.example.wealthwise_api.DTO.*;
import com.example.wealthwise_api.Entity.AccessToken;
import com.example.wealthwise_api.Entity.RefreshToken;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.Repository.JWTokenAccessRepository;
import com.example.wealthwise_api.Repository.JWTokenRefreshRepository;
import com.example.wealthwise_api.Repository.UserEntityRepository;
import com.example.wealthwise_api.Util.JWTUtil;
import com.example.wealthwise_api.dtoMapper.UserEntityDTOMapper;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserEntityDTOMapper userEntityDTOMapper;
    private final JWTokenRefreshRepository jwtTokenRefreshRepository;
    private final JWTokenAccessRepository jwtTokenAccessRepository;
    private final UserEntityRepository userEntityRepository;
    private final JWTUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public AuthenticationService(AuthenticationManager authenticationManager,
                                 UserEntityDTOMapper userEntityDTOMapper,
                                 JWTUtil jwtUtil,
                                 JWTokenRefreshRepository jwtTokenRefreshRepository,
                                 JWTokenAccessRepository jwtTokenAccessRepository,
                                    UserEntityRepository userEntityRepository
                                 ) {
        this.authenticationManager = authenticationManager;
        this.userEntityDTOMapper = userEntityDTOMapper;
        this.jwtTokenRefreshRepository = jwtTokenRefreshRepository;
        this.jwtTokenAccessRepository = jwtTokenAccessRepository;
        this.jwtUtil = jwtUtil;
        this.userEntityRepository = userEntityRepository;
    }


    public ResponseEntity<?> login(AuthenticationRequest request){
        try{
            Authentication authentication;

            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

            UserEntity principal = (UserEntity) authentication.getPrincipal();
            UserDTO userDTO = userEntityDTOMapper.apply(principal);
            Boolean isTokenExists = jwtTokenAccessRepository.existsByToken(userDTO.email());
            if(isTokenExists){
                try {
                jwtTokenAccessRepository.deleteAllBySubject(userDTO.email());
                }catch (Exception e){
                    logger.error("Error deleting token access: " + e.getMessage());
            }
            isTokenExists= jwtTokenRefreshRepository.existsByToken(userDTO.email());
            if(isTokenExists){
            }
                try {
                    jwtTokenRefreshRepository.deleteAllBySubject(userDTO.email());
                }catch (Exception e){
                    logger.error("Error deleting token refresh: " + e.getMessage());
                }
            }

            String tokenAccess = jwtUtil.issueToken(userDTO.email(), userDTO.role().toString());
            String tokenRefresh = jwtUtil.issueRefreshToken(userDTO.email());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            saveTokenAccess(tokenAccess);
            saveTokenRefresh(tokenRefresh);
            return new ResponseEntity<>(new AuthenticationResponse(tokenAccess, tokenRefresh), HttpStatus.OK);
        }catch(AuthenticationException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    private void saveTokenAccess(String token){
        AccessToken accessToken = new AccessToken(token, jwtUtil.getSubject(token), jwtUtil.extractExpiration(token));
        jwtTokenAccessRepository.save(accessToken);
    }

    private void saveTokenRefresh(String token){
        RefreshToken refreshToken = new RefreshToken(token, jwtUtil.getSubject(token), jwtUtil.extractExpiration(token));
        jwtTokenRefreshRepository.save(refreshToken);
    }

    public ResponseEntity<?> refreshToken(AuthenticationRequestToken refreshToken){

        try{
            if(jwtUtil.isRefreshTokenValid(refreshToken.refreshToken())){
                String email = jwtUtil.getSubject(refreshToken.refreshToken());
                jwtUtil.deleteRefreshToken(email);
                jwtUtil.deleteAccessToken(email);

                UserEntity principal = userEntityRepository.findByEmail(email);
                String newAccessToken = jwtUtil.issueToken(principal.getEmail(), principal.getRole().toString());
                String newRefreshToken = jwtUtil.issueRefreshToken(principal.getEmail());
                saveTokenAccess(newAccessToken);
                saveTokenRefresh(newRefreshToken);
                return new ResponseEntity<>(new AuthenticationResponse(newAccessToken, newRefreshToken), HttpStatus.OK);
            }else{
                String email = jwtUtil.getSubject(refreshToken.refreshToken());
                jwtUtil.deleteRefreshToken(email);
                jwtUtil.deleteAccessToken(email);
                return new ResponseEntity<>(new AuthenticationFailedResponse("Refresh token is invalid"), HttpStatus.UNAUTHORIZED);
            }
        }catch(Exception e){
            return new ResponseEntity<>(new AuthenticationFailedResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


}