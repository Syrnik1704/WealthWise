package com.example.wealthwise_api.Services;


import com.example.wealthwise_api.DAO.IncomesDAO;
import com.example.wealthwise_api.DAO.UserDAO;
import com.example.wealthwise_api.DTO.IncomesRequest;
import com.example.wealthwise_api.DTO.IncomesResponse;
import com.example.wealthwise_api.DTO.TokenRequest;
import com.example.wealthwise_api.Entity.Incomes;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.Util.JWTUtil;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class IncomesService {
    private final UserDAO userDAO;
    private final JWTUtil jwtUtil;
    private final IncomesDAO incomesDAO;


    public IncomesService(@Qualifier("jpa") UserDAO userDAO, JWTUtil jwtUtil, @Qualifier("incomesJPA") IncomesDAO incomesDAO) {
        this.userDAO = userDAO;
        this.jwtUtil = jwtUtil;
        this.incomesDAO = incomesDAO;
    }

    public ResponseEntity<?> addIncome(IncomesRequest incomesRequest) {
        try {
            if(incomesRequest.token()==null || incomesRequest.token().equals("")) {
                return new ResponseEntity<>("Lack of token", HttpStatus.BAD_REQUEST);
            }

            if(incomesRequest.value()<=0) {
                return new ResponseEntity<>("Incorrect value", HttpStatus.BAD_REQUEST);
            }

            String email = jwtUtil.getSubject(incomesRequest.token());
            UserEntity principal = userDAO.findUserByEmail(email);
            if(principal==null) {
               return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
            }

            if(incomesDAO.existsForDedicatedMonth(principal.getIdUser())) {
                return new ResponseEntity<>("Incomes for this month already exists", HttpStatus.BAD_REQUEST);
            }

            Incomes incomes = new Incomes(incomesRequest.value(),new Date(),principal);
            incomesDAO.save(incomes);

            return new ResponseEntity<>("Income saved successfully", HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getMonthlyIncome(TokenRequest tokenRequest){
        try{
            if(tokenRequest.token()==null || tokenRequest.token().equals("")) {
                return new ResponseEntity<>("Lack of token", HttpStatus.BAD_REQUEST);
            }

            String email = jwtUtil.getSubject(tokenRequest.token());
            UserEntity principal = userDAO.findUserByEmail(email);
            if(principal==null) {
                return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
            }

            if(!incomesDAO.existsForDedicatedMonth(principal.getIdUser())) {
                IncomesResponse incomesResponse = new IncomesResponse(0);
                return new ResponseEntity<>(incomesResponse, HttpStatus.OK);
            }

            Incomes incomes = incomesDAO.findIncomesByUser(principal.getIdUser());
            IncomesResponse incomesResponse = new IncomesResponse(incomes.getValue());
            return new ResponseEntity<>(incomesResponse, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity("Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
