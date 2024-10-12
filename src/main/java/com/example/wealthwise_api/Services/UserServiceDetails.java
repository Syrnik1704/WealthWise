package com.example.wealthwise_api.Services;

import com.example.wealthwise_api.DAO.UserDAO;
import com.example.wealthwise_api.Entity.UserDataRequest;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.Util.JWTUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceDetails implements UserDetailsService {

    private final UserDAO userDAO;

    public UserServiceDetails(@Qualifier("jpa") UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userDAO.findUserByEmail(email);
        if(userEntity==null){
            throw new UsernameNotFoundException("User not found");
        }
        return userEntity;
    }

    public ResponseEntity<?> getUser(Authentication authentication){
        try{
            UserDataRequest activeUser = userDAO.getData(authentication.getName());
            if(activeUser==null){
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(activeUser,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}