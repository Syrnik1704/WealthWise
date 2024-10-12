package com.example.wealthwise_api.Controller;


import com.example.wealthwise_api.Entity.*;
import com.example.wealthwise_api.Repository.CategoriesRepository;
import com.example.wealthwise_api.Repository.ExpensesRepository;
import com.example.wealthwise_api.Repository.IncomesRepository;
import com.example.wealthwise_api.Repository.UserEntityRepository;
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
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ExpensesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final String GET_EXPENSE_URL = "https://localhost:8443/expenses/getExpense";
    private final String ADD_EXPENSE_URL = "https://localhost:8443/expenses/saveExpense";
    private final String GET_MONTHLY_INCOME_URL = "https://localhost:8443/expenses/getMonthlyExpenseAndIncome";
    private final String GET_EXPENSE_BY_CATEGORY_URL = "https://localhost:8443/expenses/getByCategory";
    private final String LOGIN_URL = "https://localhost:8443/auth/login";
    private final String USER_EMAIL = "jan.kowalski@example.com";
    private final String USER_PASSWORD = "Password1";

    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final IncomesRepository incomesRepository;
    private final ExpensesRepository expensesRepository;

    private final CategoriesRepository categoriesRepository;
    private String tokenAccess;

    @Autowired
    ExpensesControllerTest(UserEntityRepository userEntityRepository, PasswordEncoder passwordEncoder, IncomesRepository incomesRepository,
                           ExpensesRepository expensesRepository, CategoriesRepository categoriesRepository) {
        this.userEntityRepository = userEntityRepository;
        this.passwordEncoder = passwordEncoder;
        this.incomesRepository = incomesRepository;
        this.expensesRepository = expensesRepository;
        this.categoriesRepository = categoriesRepository;
    }

    @BeforeEach
    public void addTestUser() throws Exception {
        String password = passwordEncoder.encode(USER_PASSWORD);
        UserEntity userEntity =  new UserEntity(USER_EMAIL, password, "jan", "kowalski", "12-12-2001", Role.USER);
        userEntityRepository.save(userEntity);
        Incomes incomes = new Incomes(5000, new Date(), userEntity);
        incomesRepository.save(incomes);
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

    @Test
    public void shouldSaveExpenseSuccessfully() throws Exception {
        // Przygotuj dane dla żądania zapisywania wydatków
        String category = "Mieszkanie";
        double value = 200.0;
        mockMvc.perform(post(ADD_EXPENSE_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createExpensesRequest(tokenAccess, value, category).toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Expense saved successfully")));

        // Sprawdź, czy wydatek został zapisany w bazie danych
        Expenses savedExpense = expensesRepository.findAll().get(0);
        assertEquals(value, savedExpense.getAmount());
        assertEquals(category, savedExpense.getCategory().getName());
    }

    @Test
    public void shouldReturnBadRequestWhenTokenMissing() throws Exception {
        // Przygotuj dane dla żądania zapisywania wydatków bez tokenu
        String category = "Mieszkanie";
        double value = 200.0;
        mockMvc.perform(post(ADD_EXPENSE_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createExpensesRequest("", value, category).toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Lack of token")));
    }

    @Test
    public void shouldReturnBadRequestWhenIncorrectValue() throws Exception {
        // Przygotuj dane dla żądania zapisywania wydatków z niepoprawną wartością
        String category = "Mieszkanie";
        double value = -50.0;
        mockMvc.perform(post(ADD_EXPENSE_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createExpensesRequest(tokenAccess, value, category).toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Incorrect value")));

        // Sprawdź, czy żaden wydatek nie został zapisany w bazie danych
        assertEquals(0, expensesRepository.count());
    }

    @Test
    public void shouldReturnBadRequestWhenIncorrectCategory() throws Exception {
        // Przygotuj dane dla żądania zapisywania wydatków z niepoprawną kategorią
        String category = "";
        double value = 200.0;
        mockMvc.perform(post(ADD_EXPENSE_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createExpensesRequest(tokenAccess, value, category).toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Incorrect category")));

        // Sprawdź, czy żaden wydatek nie został zapisany w bazie danych
        assertEquals(0, expensesRepository.count());
    }

    @Test
    public void shouldReturnBadRequestWhenCategoryDoesNotExist() throws Exception {
        // Przygotuj dane dla żądania zapisywania wydatków z kategorią, która nie istnieje
        String category = "NonexistentCategory";
        double value = 200.0;
        mockMvc.perform(post(ADD_EXPENSE_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createExpensesRequest(tokenAccess, value, category).toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Category does not exist")));

        // Sprawdź, czy żaden wydatek nie został zapisany w bazie danych
        assertEquals(0, expensesRepository.count());
    }

    @Test
    public void shouldReturnBadRequestWhenIncomesDoNotExist() throws Exception {
        // Przygotuj dane dla żądania zapisywania wydatków, gdy przychody nie istnieją
        incomesRepository.deleteAll();
        String category = "Mieszkanie";
        double value = 200.0;
        incomesRepository.deleteAll(); // Usuń przychody, żeby symulować brak przychodów
        mockMvc.perform(post(ADD_EXPENSE_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createExpensesRequest(tokenAccess, value, category).toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Incomes do not exist")));

        // Sprawdź, czy żaden wydatek nie został zapisany w bazie danych
        assertEquals(0, expensesRepository.count());
    }


    @Test
    public void shouldReturnBadRequestWhenExpensesExceedIncomes() throws Exception {
        // Przygotuj dane dla żądania zapisywania wydatków przekraczających przychody
        String category = "Mieszkanie";
        double value = 6000.0; // Przekraczająca wartość przychodów
        mockMvc.perform(post(ADD_EXPENSE_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createExpensesRequest(tokenAccess, value, category).toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Expenses cannot be greater than incomes")));

        // Sprawdź, czy żaden wydatek nie został zapisany w bazie danych
        assertEquals(0, expensesRepository.count());
    }

    @Test
    public void shouldReturnExpensesSuccessfully() throws Exception {

        // Dodaj kilka przykładowych wydatków do bazy danych
        Incomes principalIncomes = incomesRepository.findIncomesByUser(userEntityRepository.findByEmail(USER_EMAIL).getIdUser());
        Categories groceriesCategory = categoriesRepository.findByCategory("Żywność");
        Expenses expenses1 = new Expenses(310, new Date(), "PLN", principalIncomes.getUserEntity(), groceriesCategory);
        Expenses expenses2 = new Expenses(300, new Date(), "PLN", principalIncomes.getUserEntity(), groceriesCategory);
        expensesRepository.saveAll(List.of(expenses1, expenses2));

        // Wykonaj żądanie pobierania ostatnich wydatków
        mockMvc.perform(post(GET_EXPENSE_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createTokenRequest(tokenAccess).toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].category_id").isNumber())
                .andExpect(jsonPath("$[0].value").value(310.0))
                .andExpect(jsonPath("$[1].category_id").isNumber())
                .andExpect(jsonPath("$[1].value").value(300.0));
    }

    @Test
    public void shouldReturnEmptyListWhenNoExpensesExist() throws Exception {
        // Przygotuj dane dla żądania pobierania ostatnich wydatków
        expensesRepository.deleteAll();

        // Wykonaj żądanie pobierania ostatnich wydatków
        mockMvc.perform(post(GET_EXPENSE_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createTokenRequest(tokenAccess).toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(0))); // Zakładając, że zwraca pustą listę, gdy brak wydatków
    }

    @Test
    public void shouldReturnEmptyListWhenExpensesDoNotExistForGetExpenses() throws Exception {
        // Nie dodawaj żadnych wydatków

        mockMvc.perform(post(GET_EXPENSE_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createTokenRequest(tokenAccess).toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void shouldReturnMonthlySummarySuccessfully() throws Exception {
        // Dodaj kilka przykładowych wydatków i przychodów do bazy danych
        UserEntity user = userEntityRepository.findByEmail(USER_EMAIL);
        Categories groceriesCategory = categoriesRepository.findByCategory("Żywność");
        Expenses expenses = new Expenses(3100, new Date(), "PLN", user, groceriesCategory);
        expensesRepository.save(expenses);

        // Wykonaj żądanie pobierania podsumowania miesięcznego
        mockMvc.perform(post(GET_MONTHLY_INCOME_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createTokenRequest(tokenAccess).toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].month").isString())
                .andExpect(jsonPath("$[0].totalIncome").isNumber())
                .andExpect(jsonPath("$[0].totalExpenses").isNumber());
    }

    @Test
    public void shouldReturnExpensesForEachCategoryByMonthSuccessfully() throws Exception {
        // Dodaj kilka przykładowych wydatków do bazy danych
        UserEntity user = userEntityRepository.findByEmail(USER_EMAIL);
        Categories groceriesCategory = categoriesRepository.findByCategory("Żywność");
        Incomes principalIncomes = incomesRepository.findIncomesByUser(user.getIdUser());
        Expenses expenses1 = new Expenses(200, new Date(), "PLN", user, groceriesCategory);
        Expenses expenses2 = new Expenses(300, new Date(), "PLN", user, groceriesCategory);
        expensesRepository.saveAll(List.of(expenses1, expenses2));

        // Wykonaj żądanie pobierania wydatków dla każdej kategorii w danym miesiącu
        mockMvc.perform(post(GET_EXPENSE_BY_CATEGORY_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createTokenRequest(tokenAccess).toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].category").isString())
                .andExpect(jsonPath("$[0].value").isNumber());
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

    private JsonObject createExpensesRequest(String token, double value, String category) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("category", category);
        jsonObject.addProperty("value", value);
        return jsonObject;
    }
}