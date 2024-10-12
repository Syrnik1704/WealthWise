package com.example.wealthwise_api.Services;

import com.example.wealthwise_api.DAO.CategoriesDAO;
import com.example.wealthwise_api.DAO.ExpensesDAO;
import com.example.wealthwise_api.DAO.IncomesDAO;
import com.example.wealthwise_api.DAO.UserDAO;
import com.example.wealthwise_api.DTO.*;
import com.example.wealthwise_api.Entity.Categories;
import com.example.wealthwise_api.Entity.Expenses;
import com.example.wealthwise_api.Entity.Incomes;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.Util.JWTUtil;
import com.example.wealthwise_api.dtoMapper.ExpensesMapper;
import com.example.wealthwise_api.dtoMapper.MonthlySummaryMapper;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ExpensesService {

    private final UserDAO userDAO;
    private final JWTUtil jwtUtil;
    private final ExpensesDAO expensesDAO;
    private final CategoriesDAO categoriesDAO;
    private final IncomesDAO incomesDAO;
    private final ExpensesMapper expensesMapper;
    private final MonthlySummaryMapper monthlySummaryMapper;

    public ExpensesService(@Qualifier("jpa") UserDAO userDAO, JWTUtil jwtUtil,
                           @Qualifier("expensesJPA") ExpensesDAO expensesDAO,
                           @Qualifier("categoriesJPA") CategoriesDAO categoriesDAO,
                            @Qualifier("incomesJPA") IncomesDAO incomesDAO,
                           ExpensesMapper expensesMapper,
                           MonthlySummaryMapper monthlySummaryMapper) {
        this.userDAO = userDAO;
        this.jwtUtil = jwtUtil;
        this.expensesDAO = expensesDAO;
        this.categoriesDAO = categoriesDAO;
        this.incomesDAO = incomesDAO;
        this.expensesMapper = expensesMapper;
        this.monthlySummaryMapper = monthlySummaryMapper;
    }

    public ResponseEntity<?> saveExpenses(ExpensesRequest expensesRequest) {
        try {
            if(expensesRequest.token()==null || expensesRequest.token().equals("")) {
                return new ResponseEntity<>("Lack of token", HttpStatus.BAD_REQUEST);
            }

            if(expensesRequest.value()<=0) {
                return new ResponseEntity<>("Incorrect value", HttpStatus.BAD_REQUEST);
            }

            if(expensesRequest.category()==null || expensesRequest.category().equals("")) {
                return new ResponseEntity<>("Incorrect category", HttpStatus.BAD_REQUEST);
            }

            if(!categoriesDAO.exists(expensesRequest.category())) {
                return new ResponseEntity<>("Category does not exist", HttpStatus.BAD_REQUEST);
            }

            String email = jwtUtil.getSubject(expensesRequest.token());
            UserEntity principal = userDAO.findUserByEmail(email);

            if(principal==null) {
                return new ResponseEntity<>("User does not exist", HttpStatus.BAD_REQUEST);
            }

            Incomes principalIncomes = incomesDAO.findIncomesByUser(principal.getIdUser());

            if(principalIncomes==null) {
                return new ResponseEntity<>("Incomes do not exist", HttpStatus.BAD_REQUEST);
            }

            double sumOfExpenses = expensesDAO.getSumOfExpensesByUserId(principal.getIdUser());

            if(sumOfExpenses+expensesRequest.value()>principalIncomes.getValue()) {
                return new ResponseEntity<>("Expenses cannot be greater than incomes", HttpStatus.BAD_REQUEST);
            }

            Categories categories = categoriesDAO.findByName(expensesRequest.category());

            Expenses expenses = new Expenses(expensesRequest.value(),new Date(),"PLN",principal,categories);

            expensesDAO.save(expenses);

            return new ResponseEntity<>("Expense saved successfully", HttpStatus.OK);
        }catch (Exception e) {
         return new ResponseEntity<>("Error: " +e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getFewLastExpenses(TokenRequest tokenRequest){
        try {
            if(tokenRequest.token()==null || tokenRequest.token().equals("")) {
                return new ResponseEntity<>("Lack of token", HttpStatus.BAD_REQUEST);
            }

            String email = jwtUtil.getSubject(tokenRequest.token());
            UserEntity principal = userDAO.findUserByEmail(email);

            if(principal==null) {
                return new ResponseEntity<>("User does not exist", HttpStatus.BAD_REQUEST);
            }

            if(!expensesDAO.exists(principal.getIdUser())) {
                List<ExpensesResponse> expensesResponseList = new ArrayList<>();
                return new ResponseEntity<>(expensesResponseList, HttpStatus.BAD_REQUEST);
            }

            List<Expenses> expensesList = expensesDAO.getExpensesByUserId(principal.getIdUser());

            if(expensesList==null) {
                return new ResponseEntity<>("Expenses do not exist", HttpStatus.BAD_REQUEST);
            }

            List<ExpensesResponse> expensesResponseList = new ArrayList<>();
            for(Expenses expenses : expensesList) {
                ExpensesResponse expensesResponse= new ExpensesResponse(expenses.getCategory().getIdCategories(),expenses.getAmount());
                expensesResponseList.add(expensesResponse);
            }

            return new ResponseEntity<>(expensesResponseList, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>("Error: " +e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getExpensesForEachCategoryByMonth(TokenRequest tokenRequest){
        try {
            if(tokenRequest.token()==null || tokenRequest.token().equals("")) {
                return new ResponseEntity<>("Lack of token", HttpStatus.BAD_REQUEST);
            }

            String email = jwtUtil.getSubject(tokenRequest.token());
            UserEntity principal = userDAO.findUserByEmail(email);

            if(principal==null) {
                return new ResponseEntity<>("User does not exist", HttpStatus.BAD_REQUEST);
            }


            List<Tuple> expensesResponseList = expensesDAO.getExpensesForEachCategoryByMonth(principal.getIdUser());

            if(expensesResponseList==null) {
                return new ResponseEntity<>("Expenses do not exist", HttpStatus.BAD_REQUEST);
            }

            Incomes principalIncomes = incomesDAO.findIncomesByUser(principal.getIdUser());

            List<NewExpensesResponse> expensesResponseReturnedList = new ArrayList<>();
            for (Tuple tuple: expensesResponseList) {
                NewExpensesResponse expensesRequestReturned = expensesMapper.mapToExpensesResponse(tuple);
                double percentage;
                if(principalIncomes!=null){
                 percentage = (expensesRequestReturned.getValue()/principalIncomes.getValue())*100;}
                else{
                    percentage = 0;
                }
                NewExpensesResponse expensesResponse = new NewExpensesResponse(expensesRequestReturned.getCategory(),percentage);
                expensesResponseReturnedList.add(expensesResponse);
            }

            return new ResponseEntity<>(expensesResponseReturnedList, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>("Error: " +e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getMonthlyIncome(TokenRequest tokenRequest){
        try{
            if(tokenRequest.token()==null || tokenRequest.token().equals("")) {
                return new ResponseEntity<>("Lack of token", HttpStatus.BAD_REQUEST);
            }

            String email = jwtUtil.getSubject(tokenRequest.token());
            UserEntity principal = userDAO.findUserByEmail(email);

            if(principal==null){
                return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
            }

            List<Tuple> tuples = expensesDAO.getMonthlySummary(principal.getIdUser());
            List<MonthlySummaryResponse> result = new ArrayList<>();

            for (Tuple tuple : tuples) {
                MonthlySummaryResponse monthlySummaryResponse = monthlySummaryMapper.mapToMonthlySummaryResponse(tuple);
                result.add(monthlySummaryResponse);
            }

            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Error" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
