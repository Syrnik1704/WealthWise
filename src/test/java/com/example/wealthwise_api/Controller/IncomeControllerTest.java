package com.example.wealthwise_api.Controller;

import com.example.wealthwise_api.DTO.IncomesRequest;
import com.example.wealthwise_api.Entity.Incomes;
import com.example.wealthwise_api.Entity.Role;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.Repository.IncomesRepository;
import com.example.wealthwise_api.Repository.UserEntityRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.hamcrest.Matchers.containsString;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class IncomeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final String GET_INCOME_URL = "https://localhost:8443/incomes/getIncome";
    private final String ADD_INCOME_URL = "https://localhost:8443/incomes/addIncome";
    private final String LOGIN_URL = "https://localhost:8443/auth/login";
    private final String USER_EMAIL = "jan.kowalski@example.com";
    private final String USER_PASSWORD = "Password1";
    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final IncomesRepository incomesRepository;
    private String tokenAccess;

    @Autowired
    IncomeControllerTest(UserEntityRepository userEntityRepository, IncomesRepository incomesRepository,PasswordEncoder passwordEncoder) {
        this.userEntityRepository = userEntityRepository;
        this.incomesRepository = incomesRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @BeforeEach
    public void addTestUser() throws Exception {
        String password = passwordEncoder.encode(USER_PASSWORD);
        UserEntity userEntity =  new UserEntity(USER_EMAIL, password, "jan", "kowalski", "12-12-2001", Role.USER);
        userEntityRepository.save(userEntity);


        tokenAccess = loginUserAndGetAccessToken(USER_EMAIL, USER_PASSWORD);

    }

    @AfterEach
    public void deleteTestUser() {
        userEntityRepository.deleteAll();
    }

    private String loginUserAndGetAccessToken(String email, String password) throws Exception {
        MvcResult loginResult = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createLoginRequest(email, password).toString()))
                .andReturn();

        return extractTokenFromResponse(loginResult, "tokenAccess");
    }

    private JsonObject createLoginRequest(String email, String password) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("password", password);
        return jsonObject;
    }

    private String extractTokenFromResponse(MvcResult result, String tokenType) throws UnsupportedEncodingException {
        String response = result.getResponse().getContentAsString();
        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
        return jsonResponse.get(tokenType).getAsString();
    }

    @Test
    public void shouldAddIncomeSuccessfully() throws Exception {
        // Przygotuj dane dla żądania dodawania przychodu
        IncomesRequest incomesRequest = new IncomesRequest(tokenAccess,100);


        // Wykonaj żądanie dodawania przychodu
        mockMvc.perform(post(ADD_INCOME_URL)
                        .header(HttpHeaders.AUTHORIZATION,"Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAddIncomeRequest(incomesRequest).toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Income saved successfully")));
    }

    @Test
    public void shouldReturnBadRequestWhenTokenMissing() throws Exception {
        // Przygotuj dane dla żądania dodawania przychodu
        IncomesRequest incomesRequest = new IncomesRequest("",100);


        // Wykonaj żądanie dodawania przychodu bez tokena
        mockMvc.perform(post(ADD_INCOME_URL)
                        .header(HttpHeaders.AUTHORIZATION,"Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAddIncomeRequest(incomesRequest).toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Lack of token")));
    }

    @Test
    public void shouldReturnBadRequestWhenIncorrectValue() throws Exception {
        // Przygotuj dane dla żądania dodawania przychodu
        IncomesRequest incomesRequest = new IncomesRequest(tokenAccess, -100);

        // Wykonaj żądanie dodawania przychodu z niepoprawną wartością
        mockMvc.perform(post(ADD_INCOME_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAddIncomeRequest(incomesRequest).toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Incorrect value")));
    }


    @Test
    public void shouldReturnBadRequestWhenIncomesExistForDedicatedMonth() throws Exception {
        // Przygotuj dane dla żądania dodawania przychodu
        IncomesRequest incomesRequest = new IncomesRequest(tokenAccess, 100);
        incomesRepository.save(new Incomes(50, new Date(), userEntityRepository.findByEmail(USER_EMAIL))); // Dodaj przychód dla aktualnego miesiąca

        // Wykonaj żądanie dodawania przychodu, gdy już istnieją przychody dla danego miesiąca
        mockMvc.perform(post(ADD_INCOME_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAddIncomeRequest(incomesRequest).toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Incomes for this month already exists")));
    }

    @Test
    public void shouldReturnMonthlyIncomeSuccessfully_whenIncomeWasNotDefinedEarly() throws Exception {

        // Wykonaj żądanie pobierania miesięcznych przychodów
        mockMvc.perform(post(GET_INCOME_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createTokenRequest(tokenAccess).toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("value").value(0)); // Zakładając, że użytkownik nie ma żadnych przychodów
    }

    @Test
    public void shouldReturnBadRequestWhenTokenMissingForGetIncome() throws Exception {


        // Wykonaj żądanie pobierania miesięcznych przychodów bez tokena
        mockMvc.perform(post(GET_INCOME_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createTokenRequest("").toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Lack of token")));
    }

    @Test
    public void shouldReturnMonthlyIncomeSuccessfully() throws Exception {

        incomesRepository.save(new Incomes(500, new Date(), userEntityRepository.findByEmail(USER_EMAIL))); // Dodaj przychód dla aktualnego miesiąca

        // Wykonaj żądanie pobierania miesięcznych przychodów
        mockMvc.perform(post(GET_INCOME_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createTokenRequest(tokenAccess).toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("value").value(500.0)); // Zakładając, że użytkownik ma przychód 500.0
    }

    private JsonObject createAddIncomeRequest(IncomesRequest incomesRequest) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", incomesRequest.value());
        jsonObject.addProperty("token", incomesRequest.token());
        return jsonObject;
    }

    private JsonObject createTokenRequest(String token) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", token);
        return jsonObject;
    }

}