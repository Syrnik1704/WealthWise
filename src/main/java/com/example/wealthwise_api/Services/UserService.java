package com.example.wealthwise_api.Services;


import com.example.wealthwise_api.DAO.UserDAO;
import com.example.wealthwise_api.DTO.TokenRequest;
import com.example.wealthwise_api.DTO.UserDataResponse;
import com.example.wealthwise_api.Entity.Role;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.Repository.JWTokenAccessRepository;
import com.example.wealthwise_api.Repository.JWTokenRefreshRepository;
import com.example.wealthwise_api.Util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserDAO userDAO;
    private final JWTUtil jwtUtil;
    private final JWTokenRefreshRepository jwtTokenRefreshRepository;
    private final JWTokenAccessRepository jwtTokenAccessRepository;

    public UserService(@Qualifier("jpa") UserDAO userDAO, JWTUtil jwtUtil, JWTokenRefreshRepository jwtTokenRefreshRepository, JWTokenAccessRepository jwtTokenAccessRepository) {
        this.userDAO = userDAO;
        this.jwtUtil = jwtUtil;
        this.jwtTokenRefreshRepository = jwtTokenRefreshRepository;
        this.jwtTokenAccessRepository = jwtTokenAccessRepository;
    }


    public ResponseEntity<List<UserDataResponse>> getAllUsers(){

        List<UserEntity> userEntityList = userDAO.findAll();

        if(userEntityList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        List<UserDataResponse> userDataResponsesList = userEntityList.stream().filter(
                userEntity -> userEntity.getRole()== Role.USER
                )
                .map(userEntity -> new UserDataResponse(userEntity.getIdUser(),userEntity.getName(),
                        userEntity.getSurname(),userEntity.getEmail(),userEntity.getActive(),
                        userEntity.getRole(), userEntity.getBirthDay())).collect(Collectors.toList());

        return new ResponseEntity<>(userDataResponsesList, HttpStatus.OK);
    }

    public ResponseEntity<?> getDataUser(TokenRequest tokenResponse){
        try {
            String email = jwtUtil.getEmail(tokenResponse.token());


            UserEntity userEntity = userDAO.findUserByEmail(email);

            if(userEntity == null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            UserDataResponse userDataResponse = new UserDataResponse(userEntity.getIdUser(), userEntity.getName(), userEntity.getSurname(), userEntity.getEmail(),userEntity.getActive(),userEntity.getRole(),userEntity.getBirthDay());

            return new ResponseEntity<>(userDataResponse, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> deleteUser(TokenRequest tokenRequest){
        try {

            if(tokenRequest.token()==null || tokenRequest.token().isEmpty()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            String email = jwtUtil.getEmail(tokenRequest.token());

            UserEntity userEntity = userDAO.findUserByEmail(email);

            if(userEntity == null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            jwtTokenRefreshRepository.deleteById(userEntity.getIdUser());
            jwtTokenAccessRepository.deleteById(userEntity.getIdUser());
            userDAO.deleteUser(userEntity);

            return new ResponseEntity<>("Delete successfully",HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
