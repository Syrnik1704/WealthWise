package com.example.wealthwise_api.Services;
import com.example.wealthwise_api.DAO.UserDAO;
import com.example.wealthwise_api.DTO.ChangePasswordRequest;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.Util.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ChangePasswordService {

    private final UserDAO userDAO;
    private final JWTUtil jwtUtil;

    private PasswordEncoder passwordEncoder;

    Logger logger = LoggerFactory.getLogger(ChangePasswordService.class);

    public ChangePasswordService(@Qualifier("jpa") UserDAO userDAO, JWTUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<?> changePassword(ChangePasswordRequest request){
        try{

            String email = jwtUtil.getSubject(request.token());
            UserEntity userEntity = userDAO.findUserByEmail(email);
            if(userEntity == null){
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            if(!passwordEncoder.matches(request.password(), userEntity.getPassword())){
                return new ResponseEntity<>("Old password is incorrect", HttpStatus.BAD_REQUEST);
            }
            if(!request.password().equals(request.confirmPassword())){
                userEntity.setPassword(passwordEncoder.encode(request.confirmPassword()));
                userDAO.save(userEntity);
                return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
            }
            return new ResponseEntity<>("New password and confirm password are the same", HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}