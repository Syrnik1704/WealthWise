package com.example.wealthwise_api.Controller;


import com.example.wealthwise_api.Entity.*;
import com.example.wealthwise_api.Repository.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SavingsGoalControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final String SAVINGS_GOAL_URL = "https://localhost:8443/savingsGoal/createSavingsGoal";
    private final String ADD_CASH_SAVINGS_GOAL_URL = "https://localhost:8443/savingsGoal/addCashSavingsGoal";
    private final String GET_SAVINGS_GOAL_URL = "https://localhost:8443/savingsGoal/getSavingsGoal";
    private final String LOGIN_URL = "https://localhost:8443/auth/login";

    private final PasswordEncoder passwordEncoder;

    private final UserEntityRepository userEntityRepository;

    private final SavingsGoalsRepository savingsGoalsRepository;
    private final String USER_EMAIL = "jan.kowalski@example.com";
    private final String USER_PASSWORD = "Password1";
    private String tokenAccess;

    @Autowired
    SavingsGoalControllerTest(PasswordEncoder passwordEncoder, UserEntityRepository userEntityRepository, SavingsGoalsRepository savingsGoalsRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userEntityRepository = userEntityRepository;
        this.savingsGoalsRepository = savingsGoalsRepository;
    }

    @BeforeEach
    public void addTestUser() throws Exception {
        String password = passwordEncoder.encode(USER_PASSWORD);
        UserEntity userEntity =  new UserEntity(USER_EMAIL, password, "jan", "kowalski", "12-12-2001", Role.USER);
        userEntityRepository.save(userEntity);
        SavingsGoals savingsGoals = new SavingsGoals("Dom", 100000.0, 0.0, true, userEntity);
        savingsGoalsRepository.save(savingsGoals);
        tokenAccess = loginUserAndGetAccessToken(USER_EMAIL, USER_PASSWORD);

    }

    private String loginUserAndGetAccessToken(String email, String password) throws Exception {
        MvcResult loginResult = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createLoginRequest(email, password).toString()))
                .andReturn();

        return extractTokenFromResponse(loginResult, "tokenAccess");
    }

    @AfterEach
    public void deleteTestUser() {
        userEntityRepository.deleteAll();
    }


    @Test
    public void shouldCreateSavingsGoalSuccessfully() throws Exception {
        // Przygotuj dane dla żądania utworzenia celu oszczędnościowego
        String goalName = "My Savings Goal";
        double goalAmount = 1000.0;

        mockMvc.perform(post(SAVINGS_GOAL_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createSavingGoalRequest(tokenAccess, goalName, goalAmount).toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Savings Goal created successfully")));

        // Sprawdź, czy cel oszczędnościowy został zapisany w bazie danych
        SavingsGoals savedSavingsGoal = savingsGoalsRepository.findByGoalName(goalName, userEntityRepository.findByEmail(USER_EMAIL).getIdUser());
        assertNotNull(savedSavingsGoal);
        assertEquals(goalName, savedSavingsGoal.getName());
        assertEquals(goalAmount, savedSavingsGoal.getTargetAmount());
    }

    @Test
    public void shouldReturnBadRequestWhenGoalNameMissing() throws Exception {
        // Przygotuj dane dla żądania utworzenia celu oszczędnościowego bez nazwy celu
        double goalAmount = 1000.0;

        mockMvc.perform(post(SAVINGS_GOAL_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createSavingGoalRequest(tokenAccess, "", goalAmount).toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Goal name is required")));
    }

    @Test
    public void shouldReturnBadRequestWhenGoalAmountInvalid() throws Exception {
        // Przygotuj dane dla żądania utworzenia celu oszczędnościowego z nieprawidłową kwotą celu
        String goalName = "My Savings Goal";

        mockMvc.perform(post(SAVINGS_GOAL_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createSavingGoalRequest(tokenAccess, goalName, -100.0).toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Goal amount must be greater than 0")));
    }

    @Test
    public void shouldReturnBadRequestWhenSavingsGoalExists() throws Exception {


        mockMvc.perform(post(SAVINGS_GOAL_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createSavingGoalRequest(tokenAccess, "Dom", 100000.0).toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Savings Goal already exists")));
    }

    @Test
    public void shouldReturnBadRequestWhenCurrentAmountZeroForAddCashSavingsGoal() throws Exception {
        // Przygotuj dane dla żądania dodawania kwoty do celu oszczędnościowego z kwotą równą zero
        double currentAmount = 0.0;

        mockMvc.perform(post(ADD_CASH_SAVINGS_GOAL_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addSavingGoalRequest(tokenAccess, "Dom", currentAmount).toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Current amount must be greater than 0")));
    }

    @Test
    public void shouldReturnBadRequestWhenSavingsGoalDoesNotExistForAddCashSavingsGoal() throws Exception {
        // Przygotuj dane dla żądania dodawania kwoty do celu oszczędnościowego, który nie istnieje
        double currentAmount = 500.0;

        mockMvc.perform(post(ADD_CASH_SAVINGS_GOAL_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addSavingGoalRequest(tokenAccess, "Samochod", currentAmount).toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Savings Goal does not exist")));
    }

    @Test
    public void shouldReturnBadRequestWhenSavingsGoalAlreadyCompletedForAddCashSavingsGoal() throws Exception {
        // Przygotuj dane dla żądania dodawania kwoty do celu oszczędnościowego, który jest już zakończony
        double currentAmount = 500.0;
        // Ustaw status zakończenia dla celu oszczędnościowego
        SavingsGoals existingSavingsGoal = savingsGoalsRepository.findByGoalName("Dom", userEntityRepository.findByEmail(USER_EMAIL).getIdUser());
        existingSavingsGoal.setStatus(false);
        savingsGoalsRepository.save(existingSavingsGoal);

        mockMvc.perform(post(ADD_CASH_SAVINGS_GOAL_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addSavingGoalRequest(tokenAccess, "Dom", currentAmount).toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Savings Goal is already completed")));
    }

    @Test
    public void shouldCompleteSavingsGoalSuccessfullyForAddCashSavingsGoal() throws Exception {
        // Przygotuj dane dla żądania dodawania kwoty do celu oszczędnościowego

        double currentAmount = 100000.0; // Wartość, która uzupełni cel oszczędnościowy

        mockMvc.perform(post(ADD_CASH_SAVINGS_GOAL_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addSavingGoalRequest(tokenAccess, "Dom", currentAmount).toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Saving has been completed")));

        // Sprawdź, czy cel oszczędnościowy został zakończony i usunięty
        SavingsGoals savingsGoal = savingsGoalsRepository.findByGoalName("Dom", userEntityRepository.findByEmail(USER_EMAIL).getIdUser());
        assertNull(savingsGoal);
    }

    @Test
    public void shouldUpdateCurrentAmountSuccessfullyForAddCashSavingsGoal() throws Exception {

        double currentAmount = 500.0; // Wartość, która zostanie dodana do aktualnej kwoty celu oszczędnościowego

        mockMvc.perform(post(ADD_CASH_SAVINGS_GOAL_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addSavingGoalRequest(tokenAccess, "Dom", currentAmount).toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Change amount of savings goal - successfully")));

        // Sprawdź, czy aktualna kwota celu oszczędnościowego została zaktualizowana
        SavingsGoals savingsGoal = savingsGoalsRepository.findByGoalName("Dom", userEntityRepository.findByEmail(USER_EMAIL).getIdUser());
        assertNotNull(savingsGoal);
        assertEquals(500.0, savingsGoal.getCurrentAmount());
    }

    @Test
    public void shouldReturnSavingsGoalListSuccessfullyForGetSavingsGoalList() throws Exception {
        // Przygotuj dane dla żądania pobierania listy celów oszczędnościowych
        mockMvc.perform(post(GET_SAVINGS_GOAL_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createTokenRequest(tokenAccess).toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // Zakładając, że istnieje jeden cel oszczędnościowy w bazie danych
                .andExpect(jsonPath("$[0].title").isString())
                .andExpect(jsonPath("$[0].currentAmount").isNumber())
                .andExpect(jsonPath("$[0].targetAmount").isNumber())
                .andExpect(jsonPath("$[0].active").isBoolean());
    }

    @Test
    public void shouldReturnEmptyListWhenNoSavingsGoalsExistForGetSavingsGoalList() throws Exception {
        // Przygotuj dane dla żądania pobierania listy celów oszczędnościowych, gdy brak celów oszczędnościowych
        savingsGoalsRepository.deleteAll();

        mockMvc.perform(post(GET_SAVINGS_GOAL_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createTokenRequest(tokenAccess).toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0))); // Zakładając, że zwraca pustą listę, gdy brak celów oszczędnościowych
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

    private JsonObject createTokenRequest(String token) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", token);
        return jsonObject;
    }

    private JsonObject createSavingGoalRequest(String token, String goalName, double goalAmount) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("goalName", goalName);
        jsonObject.addProperty("goalAmount", goalAmount);
        return jsonObject;
    }

    private JsonObject addSavingGoalRequest(String token, String name, double currentAmount) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("currentAmount", currentAmount);
        return jsonObject;
    }

}