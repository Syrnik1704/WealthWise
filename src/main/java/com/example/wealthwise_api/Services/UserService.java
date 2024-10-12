package com.example.wealthwise_api.Services;


import com.example.wealthwise_api.DAO.UserDAO;
import com.example.wealthwise_api.DTO.TokenRequest;
import com.example.wealthwise_api.DTO.UserDataResponse;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.Repository.JWTokenAccessRepository;
import com.example.wealthwise_api.Repository.JWTokenRefreshRepository;
import com.example.wealthwise_api.Util.JWTUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    public ResponseEntity<?> getDataUser(TokenRequest tokenResponse){
        try {
            String email = jwtUtil.getSubject(tokenResponse.token());

            UserEntity userEntity = userDAO.findUserByEmail(email);

            if(userEntity == null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            UserDataResponse userDataResponse = new UserDataResponse(userEntity.getName(), userEntity.getSurname(), userEntity.getEmail(), userEntity.getBirthDay());

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

            String email = jwtUtil.getSubject(tokenRequest.token());

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
