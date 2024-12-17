package com.example.wealthwise_api.Services;

import com.example.wealthwise_api.DAO.CategoriesDAO;
import com.example.wealthwise_api.DAO.ExpensesDAO;
import com.example.wealthwise_api.DAO.UserDAO;
import com.example.wealthwise_api.DTO.ExpensesDateRangeRequest;
import com.example.wealthwise_api.DTO.ExpensesRequest;
import com.example.wealthwise_api.Entity.Categories;
import com.example.wealthwise_api.Entity.Expenses;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.Exception.ResourceNotFoundException;
import com.example.wealthwise_api.Util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ExpensesService {

    private final UserDAO userDAO;
    private final JWTUtil jwtUtil;
    private final ExpensesDAO expensesDAO;
    private final CategoriesDAO categoriesDAO;


    public ExpensesService(@Qualifier("jpa") UserDAO userDAO, JWTUtil jwtUtil,
                           @Qualifier("expensesJPA") ExpensesDAO expensesDAO,
                           @Qualifier("categoriesJPA") CategoriesDAO categoriesDAO) {
        this.userDAO = userDAO;
        this.jwtUtil = jwtUtil;
        this.expensesDAO = expensesDAO;
        this.categoriesDAO = categoriesDAO;

    }

    private String getUserEmailFromToken(HttpServletRequest request) throws HttpClientErrorException.BadRequest {
        String token = request.getHeader("Authorization").split("Bearer ")[1];

        if(token==null || token.isEmpty()) throw new IllegalArgumentException("Token is empty");

        return jwtUtil.getEmail(token);
    }

    public ResponseEntity<String> addExpenses(HttpServletRequest request, ExpensesRequest expensesRequest){
        try {
            Optional<UserEntity> userEntity = Optional.ofNullable(userDAO.findUserByEmail(getUserEmailFromToken(request)));

            if(userEntity.isEmpty()) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

            Optional<Categories> category = Optional.ofNullable(categoriesDAO.findByName(expensesRequest.getCategoryName()));

            if(category.isEmpty()) return new ResponseEntity<>("Category not found", HttpStatus.NOT_FOUND);

            if(expensesRequest.getAmount()<=0) return new ResponseEntity<>("Expenses amount must be greater than 0", HttpStatus.BAD_REQUEST);

            if(StringUtils.isBlank(expensesRequest.getName())) return new ResponseEntity<>("Expenses name cannot be empty", HttpStatus.BAD_REQUEST);

            if(StringUtils.isBlank(expensesRequest.getDescription())) return new ResponseEntity<>("Expenses description cannot be empty", HttpStatus.BAD_REQUEST);

            expensesDAO.save(new Expenses(expensesRequest.getName(), expensesRequest.getDescription(), expensesRequest.getAmount(), new Date(), userEntity.get(), category.get()));

            return new ResponseEntity<>("Expenses added successfully", HttpStatus.OK);
        }catch (ResourceNotFoundException e) {
            return new ResponseEntity<>("Resource not found", HttpStatus.NOT_FOUND);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Expenses>> getAllExpensesOfUsers(HttpServletRequest request){
        try{
            Optional<UserEntity> userEntity = Optional.ofNullable(userDAO.findUserByEmail(getUserEmailFromToken(request)));

            if(userEntity.isEmpty()) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

            Optional<List<Expenses>> expenses = Optional.ofNullable(expensesDAO.findExpensesByUser(userEntity.get().getIdUser()));

            return new ResponseEntity<>(expenses.get(), HttpStatus.OK);

        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Expenses> getExpenses(HttpServletRequest request, Long idExpenses){
        try{
            Optional<UserEntity> userEntity = Optional.ofNullable(userDAO.findUserByEmail(getUserEmailFromToken(request)));

            if(userEntity.isEmpty()) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

            Optional<Expenses> expenses = Optional.ofNullable(expensesDAO.findExpensesByUserAndIdExpenses(userEntity.get().getIdUser(), idExpenses));

            if(expenses.isEmpty()) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

            return new ResponseEntity<>(expenses.get(), HttpStatus.OK);

        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> deleteExpenses(HttpServletRequest request, List<Long> idExpenses){
        try{
            Optional<UserEntity> userEntity = Optional.ofNullable(userDAO.findUserByEmail(getUserEmailFromToken(request)));

            if(userEntity.isEmpty()) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

            if(idExpenses.isEmpty()) return new ResponseEntity<>("Expenses list is empty", HttpStatus.BAD_REQUEST);

            if(!expensesDAO.checkExpensesExists(idExpenses)) return new ResponseEntity<>("Expenses not found", HttpStatus.NOT_FOUND);

            for(Long id : idExpenses){
                Expenses expenses = expensesDAO.findExpensesByUserAndIdExpenses(userEntity.get().getIdUser(), id);
                expensesDAO.delete(expenses);
            }

            return new ResponseEntity<>("Expenses deleted successfully", HttpStatus.OK);

        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> updateExpenses(HttpServletRequest request, Long expenseId, ExpensesRequest expensesRequest){
        try {
            Optional<UserEntity> userEntity = Optional.ofNullable(userDAO.findUserByEmail(getUserEmailFromToken(request)));

            if(userEntity.isEmpty()) return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

            if(expensesRequest.getAmount()<=0) return new ResponseEntity<>("Expenses amount must be greater than 0", HttpStatus.BAD_REQUEST);

            if(StringUtils.isBlank(expensesRequest.getName())) return new ResponseEntity<>("Expenses name cannot be empty", HttpStatus.BAD_REQUEST);

            if(StringUtils.isBlank(expensesRequest.getDescription())) return new ResponseEntity<>("Expenses description cannot be empty", HttpStatus.BAD_REQUEST);

            Optional<Expenses> expenses = Optional.ofNullable(expensesDAO.findExpensesByUserAndIdExpenses(userEntity.get().getIdUser(), expenseId));

            if(expenses.isEmpty()) return new ResponseEntity<>("Expenses not found", HttpStatus.NOT_FOUND);

            expenses.get().setName(expensesRequest.getName());
            expenses.get().setDescription(expensesRequest.getDescription());
            expenses.get().setAmount(expensesRequest.getAmount());
            expenses.get().setCategory(categoriesDAO.findByName(expensesRequest.getCategoryName()));

            expensesDAO.save(expenses.get());

            return new ResponseEntity<>("Expenses updated successfully", HttpStatus.OK);

        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Expenses>> getExpensesInDateRangeAndSelectedCategories(HttpServletRequest request, ExpensesDateRangeRequest expensesDateRangeRequest){
        try{
            Optional<UserEntity> userEntity = Optional.ofNullable(userDAO.findUserByEmail(getUserEmailFromToken(request)));

            if(userEntity.isEmpty()) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

            Optional<Categories> category = Optional.ofNullable(categoriesDAO.findByName(expensesDateRangeRequest.getCategoryName()));

            if(category.isEmpty()) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

            Optional<List<Expenses>> expenses = Optional.ofNullable(expensesDAO.findExpensesInDateRangeAndSelectedCategories(userEntity.get().getIdUser(), expensesDateRangeRequest.getStartDate(), expensesDateRangeRequest.getEndDate(), category.get().getIdCategories()));

            if(expenses.get().isEmpty()) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

            return new ResponseEntity<>(expenses.get(), HttpStatus.OK);

        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
