package com.example.wealthwise_api.Services;

import com.example.wealthwise_api.DAO.SavingsGoalDAO;
import com.example.wealthwise_api.DAO.UserDAO;
import com.example.wealthwise_api.DTO.AddSavingsGoalRequest;
import com.example.wealthwise_api.DTO.SavingsGoalRequest;
import com.example.wealthwise_api.DTO.SavingsGoalResponse;
import com.example.wealthwise_api.DTO.TokenRequest;
import com.example.wealthwise_api.Entity.SavingsGoals;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.Util.JWTUtil;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SavingsGoalService {

    private final UserDAO userDAO;

    private final JWTUtil jwtUtil;

    private final SavingsGoalDAO savingsGoalDAO;

    public SavingsGoalService(@Qualifier("jpa")UserDAO userDAO, JWTUtil jwtUtil,@Qualifier("savingGoalJPA") SavingsGoalDAO savingsGoalDAO) {
        this.userDAO = userDAO;
        this.jwtUtil = jwtUtil;
        this.savingsGoalDAO = savingsGoalDAO;
    }


    public ResponseEntity<?> createSavingsGoal(SavingsGoalRequest savingsGoalRequest){
        try {
            if(savingsGoalRequest.getGoalName() == null || savingsGoalRequest.getGoalName().isEmpty()){
                return new ResponseEntity<>("Goal name is required", HttpStatus.BAD_REQUEST);
            }
            if(savingsGoalRequest.getGoalAmount() <= 0){
                return new ResponseEntity<>("Goal amount must be greater than 0", HttpStatus.BAD_REQUEST);
            }

            String email = jwtUtil.getSubject(savingsGoalRequest.getToken());
            UserEntity principal = userDAO.findUserByEmail(email);

            if(principal == null){
                return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
            }

            if(savingsGoalDAO.checkIfSavingsGoalExists(savingsGoalRequest.getGoalName(), principal.getIdUser())){
                return new ResponseEntity<>("Savings Goal already exists", HttpStatus.BAD_REQUEST);
            }

            SavingsGoals savingsGoal = new SavingsGoals(savingsGoalRequest.getGoalName(),savingsGoalRequest.getGoalAmount(),0,true,principal);

            savingsGoalDAO.save(savingsGoal);

            return new ResponseEntity<>("Savings Goal created successfully", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> addCashSavingsGoal(AddSavingsGoalRequest addSavingsGoalRequest ){
        try {

            if(addSavingsGoalRequest.getToken()==null || addSavingsGoalRequest.getToken().isEmpty()){
                return new ResponseEntity<>("Token is required", HttpStatus.BAD_REQUEST);
            }

            if(addSavingsGoalRequest.getCurrentAmount() <= 0){
                return new ResponseEntity<>("Current amount must be greater than 0", HttpStatus.BAD_REQUEST);
            }

            String email = jwtUtil.getSubject(addSavingsGoalRequest.getToken());

            UserEntity principal = userDAO.findUserByEmail(email);

            if(principal == null){
                return new ResponseEntity<>("User not found", HttpStatus.UNAUTHORIZED);
            }

            if(!savingsGoalDAO.checkIfSavingsGoalExists(addSavingsGoalRequest.getName(), principal.getIdUser())){
                return new ResponseEntity<>("Savings Goal does not exist", HttpStatus.BAD_REQUEST);
            }

            SavingsGoals savingsGoal = savingsGoalDAO.getSavingsGoalByName(addSavingsGoalRequest.getName(), principal.getIdUser());

            if(!savingsGoal.isStatus()){
                return new ResponseEntity<>("Savings Goal is already completed", HttpStatus.BAD_REQUEST);
            }

            double newAmount = savingsGoal.getCurrentAmount() + addSavingsGoalRequest.getCurrentAmount();

            if (newAmount >= savingsGoal.getTargetAmount()){
                savingsGoal.setStatus(false);
                savingsGoalDAO.delete(savingsGoal);
                return new ResponseEntity<>("Saving has been completed", HttpStatus.BAD_REQUEST);
            }

            savingsGoal.setCurrentAmount(newAmount);
            savingsGoalDAO.save(savingsGoal);
            return new ResponseEntity<>("Change amount of savings goal - successfully ", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getSavingsGoalList(TokenRequest tokenRequest){
        try {
            if(tokenRequest.token()==null || tokenRequest.token().isEmpty()){
                return new ResponseEntity<>("Token is required", HttpStatus.BAD_REQUEST);
            }

            String email = jwtUtil.getSubject(tokenRequest.token());
            UserEntity principal = userDAO.findUserByEmail(email);

            if(principal == null){
                return new ResponseEntity<>("User nor found", HttpStatus.UNAUTHORIZED);
            }

            List<SavingsGoals> savingsGoalsList = savingsGoalDAO.getSavingsGoalsByUserId(principal.getIdUser());
            List<SavingsGoalResponse> savingsGoalResponseList = new ArrayList<>();

            for( SavingsGoals savingsGoal : savingsGoalsList){
                SavingsGoalResponse savingsGoalResponse = new SavingsGoalResponse(savingsGoal.getName(), savingsGoal.getCurrentAmount(),savingsGoal.getTargetAmount(),savingsGoal.isStatus());
                savingsGoalResponseList.add(savingsGoalResponse);
            }

            return new ResponseEntity<>(savingsGoalResponseList, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
