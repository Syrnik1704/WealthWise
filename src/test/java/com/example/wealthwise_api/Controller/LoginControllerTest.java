package com.example.wealthwise_api.Controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.example.wealthwise_api.Entity.Role;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.Repository.UserEntityRepository;
import com.example.wealthwise_api.WealthwiseApiApplication;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final String LOGIN_URL = "https://localhost:8443/auth/login";
    private final String REFRESHTOKEN_URL = "https://localhost:8443/auth/refreshToken";
    private final String USER_EMAIL = "jan.kowalski@example.com";
    private final String USER_PASSWORD = "Password1";

    @Autowired
    LoginControllerTest(UserEntityRepository userEntityRepository, PasswordEncoder passwordEncoder) {
        this.userEntityRepository = userEntityRepository;
        this.passwordEncoder = passwordEncoder;
    }


    private JsonObject createLoginRequest(String email, String password) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("password", password);
        return jsonObject;
    }

    private JsonObject createRefreshTokenRequest(String token) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("refreshToken", token);
        return jsonObject;
    }

    @BeforeEach
    public void addTestUser() throws Exception {
        String password = passwordEncoder.encode(USER_PASSWORD);
        UserEntity userEntity =  new UserEntity( USER_EMAIL, password, "jan", "kowalski", "12-12-2001", Role.USER);
        userEntityRepository.save(userEntity);
    }

    @AfterEach
    public void deleteTestUser() {
        userEntityRepository.deleteAll();
    }

    @Test
    public void shouldReturnOk_whenSuccessfulWithEmailAndPassword() throws Exception {
        mockMvc
                .perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createLoginRequest(USER_EMAIL, USER_PASSWORD).toString()))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void shouldReturnTokens_whenSuccessfulLogin() throws Exception {
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createLoginRequest(USER_EMAIL, USER_PASSWORD).toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("tokenAccess").isString())
                .andExpect(jsonPath("tokenRefresh").isString());
    }

    @Test
    public void shouldReturnUnauthorized_whenInvalidCredentials() throws Exception {
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createLoginRequest(USER_EMAIL, "InvalidPassword").toString()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnNewTokens_whenRefreshTokenIsValid() throws Exception {
        // 1. Ponownie zaloguj użytkownika, aby uzyskać nowe tokeny
        MvcResult loginResult = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createLoginRequest(USER_EMAIL, USER_PASSWORD).toString()))
                .andDo(print())
                .andReturn();

        // 2. Wyodrębnij tokeny z wyniku logowania
        String tokenAccess = extractTokenFromResponse(loginResult, "tokenAccess");
        String tokenRefresh = extractTokenFromResponse(loginResult, "tokenRefresh");

        Thread.sleep(15000);

        // 3. Wykonaj test odświeżania tokenów
        mockMvc.perform(post(REFRESHTOKEN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRefreshTokenRequest(tokenRefresh).toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("tokenAccess").isString())
                .andExpect(jsonPath("tokenRefresh").isString());
    }

    private String extractTokenFromResponse(MvcResult result, String tokenKey) throws UnsupportedEncodingException {
        String responseJson = result.getResponse().getContentAsString();
        return JsonPath.read(responseJson, "$." + tokenKey);
    }

}