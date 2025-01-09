package com.example.wealthwise_api.Services;


import com.example.wealthwise_api.DAO.ExpensesDAO;
import com.example.wealthwise_api.DAO.IncomesDAO;
import com.example.wealthwise_api.DAO.UserDAO;
import com.example.wealthwise_api.DTO.IncomesRequest;
import com.example.wealthwise_api.DTO.IncomesResponse;
import com.example.wealthwise_api.DTO.TokenRequest;
import com.example.wealthwise_api.Entity.Incomes;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.Exception.ResourceNotFoundException;
import com.example.wealthwise_api.Util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class IncomesService {

    private final UserDAO userDAO;
    private final JWTUtil jwtUtil;
    private final IncomesDAO incomesDAO;
    private final ExpensesDAO expensesDAO;


    public IncomesService(@Qualifier("jpa") UserDAO userDAO, JWTUtil jwtUtil, @Qualifier("incomesJPA") IncomesDAO incomesDAO,
                          @Qualifier("expensesJPA") ExpensesDAO expensesDAO) {
        this.userDAO = userDAO;
        this.jwtUtil = jwtUtil;
        this.incomesDAO = incomesDAO;
        this.expensesDAO = expensesDAO;
    }

    private String getUserEmailFromToken(HttpServletRequest request) throws HttpClientErrorException.BadRequest {
        String token = request.getHeader("Authorization").split("Bearer ")[1];

        if(token==null || token.isEmpty()) throw new IllegalArgumentException("Token is empty");

        return jwtUtil.getEmail(token);
    }

    public ResponseEntity<String> addIncome(HttpServletRequest request, IncomesRequest incomesRequest) {
        try{
            Optional<UserEntity> userEntity = Optional.ofNullable(userDAO.findUserByEmail(getUserEmailFromToken(request)));

            if(userEntity.isEmpty()) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

            if(incomesRequest.getValue()<=0) return new ResponseEntity<>("Income value must be greater than 0", HttpStatus.BAD_REQUEST);

            if(StringUtils.isBlank(incomesRequest.getName())) return new ResponseEntity<>("Income name cannot be empty", HttpStatus.BAD_REQUEST);

            if(StringUtils.isBlank(incomesRequest.getDescription())) return new ResponseEntity<>("Income description cannot be empty", HttpStatus.BAD_REQUEST);

            Incomes incomes = new Incomes(incomesRequest.getName(),incomesRequest.getDescription(),incomesRequest.getValue(), new Date(), userEntity.get());

            incomesDAO.save(incomes);

            return new ResponseEntity<>("Income added successfully", HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Incomes>> getAllIncomesOfUser(HttpServletRequest request){
        try{
            Optional<UserEntity> userEntity = Optional.ofNullable(userDAO.findUserByEmail(getUserEmailFromToken(request)));

            if(userEntity.isEmpty()) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

            Optional<List<Incomes>> incomes = Optional.ofNullable(incomesDAO.findIncomesByUser(userEntity.get().getIdUser()));

            return incomes.map(incomesList -> new ResponseEntity<>(incomesList, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Incomes> getIncome(HttpServletRequest request, Long idIncome){
        try{
            Optional<UserEntity> userEntity = Optional.ofNullable(userDAO.findUserByEmail(getUserEmailFromToken(request)));

            if(userEntity.isEmpty()) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

            Optional<Incomes> incomes = Optional.ofNullable(incomesDAO.findIncomesByUserAndIdIncomes(userEntity.get().getIdUser(), idIncome));


            if (incomes == null || incomes.isEmpty() ) throw new ResourceNotFoundException("Income not found");

            return new ResponseEntity<>(incomes.get(), HttpStatus.OK);

        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> deleteIncomes(HttpServletRequest request, List<Long> idIncomes){
        try{
            Optional<UserEntity> userEntity = Optional.ofNullable(userDAO.findUserByEmail(getUserEmailFromToken(request)));

            if(userEntity.isEmpty()) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

            if(idIncomes.isEmpty()) return new ResponseEntity<>("Incomes list is empty", HttpStatus.BAD_REQUEST);

            if(!incomesDAO.checkIncomesExists(idIncomes)) return new ResponseEntity<>("Incomes not found", HttpStatus.NOT_FOUND);

            for(Long idIncome : idIncomes){
                incomesDAO.deleteIncomesById(idIncome);
            }

            return new ResponseEntity<>("Incomes deleted successfully", HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> updateIncome(HttpServletRequest request, Long incomeId, IncomesRequest incomesRequest){
        try{
            Optional<UserEntity> userEntity = Optional.ofNullable(userDAO.findUserByEmail(getUserEmailFromToken(request)));

            if(userEntity.isEmpty()) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

            if(incomesRequest.getValue()<=0) return new ResponseEntity<>("Income value must be greater than 0", HttpStatus.BAD_REQUEST);

            if(StringUtils.isBlank(incomesRequest.getName())) return new ResponseEntity<>("Income name cannot be empty", HttpStatus.BAD_REQUEST);

            if(StringUtils.isBlank(incomesRequest.getDescription())) return new ResponseEntity<>("Income description cannot be empty", HttpStatus.BAD_REQUEST);

            Optional<Incomes> incomes = Optional.ofNullable(incomesDAO.findIncomesByUserAndIdIncomes(userEntity.get().getIdUser(), incomeId));

            if(incomes==null) return new ResponseEntity<>("Income not found", HttpStatus.NOT_FOUND);

            incomes.get().setName(incomesRequest.getName());
            incomes.get().setValue(incomesRequest.getValue());
            incomes.get().setDescription(incomesRequest.getDescription());

            incomesDAO.save(incomes.get());

            return new ResponseEntity<>("Income updated successfully", HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<?> getUserBalance(HttpServletRequest request) {
        try {
            Optional<UserEntity> userEntity = Optional.ofNullable(userDAO.findUserByEmail(getUserEmailFromToken(request)));

            if (userEntity.isEmpty()) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

            List<Double> incomes = incomesDAO.findIncomesByUser(userEntity.get().getIdUser())
                    .stream().map(income -> income.getValue()).toList();
            List<Double> expenses = expensesDAO.findExpensesByUser(userEntity.get().getIdUser())
                    .stream().map(expense -> expense.getAmount()).toList();

            double totalIncome = incomes.stream().mapToDouble(Double::doubleValue).sum();
            double totalExpenses = expenses.stream().mapToDouble(Double::doubleValue).sum();

            double balance = totalIncome - totalExpenses;

            return new ResponseEntity<>(balance, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
