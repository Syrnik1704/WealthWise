package com.example.wealthwise_api.Services;

import com.example.wealthwise_api.DAO.IncomesDAO;
import com.example.wealthwise_api.DAO.UserDAO;
import com.example.wealthwise_api.DTO.IncomesRequest;
import com.example.wealthwise_api.DTO.IncomesResponse;
import com.example.wealthwise_api.Entity.Incomes;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.Repository.IncomesRepository;
import com.example.wealthwise_api.Repository.UserEntityRepository;
import com.example.wealthwise_api.Util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class IncomesService {

    private final IncomesRepository repository;
    private final UserEntityRepository userEntityRepository;
    private final UserDAO userDAO;
    private final JWTUtil jwtUtil;
    private final IncomesDAO incomesDAO;

    @Autowired
    public IncomesService(IncomesRepository repository,
                          UserEntityRepository userEntityRepository,
                          @Qualifier("jpa") UserDAO userDAO,
                          JWTUtil jwtUtil,
                          @Qualifier("incomesJPA") IncomesDAO incomesDAO) {
        this.repository = repository;
        this.userEntityRepository = userEntityRepository;
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

            String email = jwtUtil.getEmail(incomesRequest.token());
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

    public ResponseEntity<?> getMonthlyIncome(HttpServletRequest request){
        String token = request.getHeader("Authorization").split("Bearer ")[1];
        try{
            if(token==null || token.equals("")) {
                return new ResponseEntity<>("Lack of token", HttpStatus.BAD_REQUEST);
            }


            String email = jwtUtil.getEmail(token);

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

    public void deleteIncome(String token, Long incomeId) {
        UserEntity user = findUserByToken(token);
        Incomes income = findIncomeById(incomeId);

        if (income.getUserEntity().getIdUser() != user.getIdUser()) {
            throw new RuntimeException("You are not authorized to delete this income.");
        }

        repository.delete(income);
    }

    private UserEntity findUserByToken(String token) {
        String email = jwtUtil.getEmail(token.replace("Bearer ", ""));
        UserEntity user = userEntityRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User associated with the token was not found.");
        }
        return user;
    }

    private Incomes findIncomeById(Long incomeId) {
        return repository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income with the specified ID was not found."));
    }
}
