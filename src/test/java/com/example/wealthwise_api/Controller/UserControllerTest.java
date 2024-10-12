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

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final String LOGIN_URL = "https://localhost:8443/auth/login";
    private final String CHANGE_PASSWORD_URL = "https://localhost:8443/user/changePassword";
    private final String GET_DATA_USER_URL = "https://localhost:8443/user/getDataUser";
    private final String DELETE_USER_URL = "https://localhost:8443/user/deleteUser";
    private final PasswordEncoder passwordEncoder;

    private final UserEntityRepository userEntityRepository;

    private final String USER_EMAIL = "jan.kowalski@example.com";
    private final String USER_PASSWORD = "Password1";
    private String tokenAccess;

    @Autowired
    UserControllerTest(PasswordEncoder passwordEncoder, UserEntityRepository userEntityRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userEntityRepository = userEntityRepository;
    }

    @BeforeEach
    public void addTestUser() throws Exception {
        String password = passwordEncoder.encode(USER_PASSWORD);
        UserEntity userEntity =  new UserEntity(USER_EMAIL, password, "jan", "kowalski", "12-12-2001", Role.USER);
        userEntityRepository.save(userEntity);
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
    public void shouldChangePasswordSuccessfully() throws Exception {
        // Wywołaj metodę changePassword
        mockMvc.perform(post(CHANGE_PASSWORD_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createChangePasswordRequest(tokenAccess, USER_PASSWORD,  "PaSSwOrd1").toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Password changed successfully"));
    }

    @Test
    public void shouldReturnBadRequestWhenOldPasswordIncorrect() throws Exception {
        // Wywołaj metodę changePassword z błędnym starym hasłem
        mockMvc.perform(post(CHANGE_PASSWORD_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createChangePasswordRequest(tokenAccess, "IncorrectPassword",  "NPassword1").toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Old password is incorrect"));
    }

    @Test
    public void shouldReturnBadRequestWhenNewPasswordAndConfirmPasswordNotEqual() throws Exception {
        // Wywołaj metodę changePassword z niezgodnym nowym hasłem i potwierdzeniem hasła
        mockMvc.perform(post(CHANGE_PASSWORD_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createChangePasswordRequest(tokenAccess, USER_PASSWORD, USER_PASSWORD).toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("New password and confirm password are the same"));
    }

    @Test
    public void shouldReturnUserDataSuccessfully() throws Exception {
        // Wywołaj metodę getDataUser
        mockMvc.perform(post(GET_DATA_USER_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createTokenRequest(tokenAccess).toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("jan"))
                .andExpect(jsonPath("$.surname").value("kowalski"))
                .andExpect(jsonPath("$.email").value(USER_EMAIL))
                .andExpect(jsonPath("$.birthDay").value("12-12-2001"));
    }

    @Test
    public void shouldDeleteUserSuccessfully() throws Exception {
        // Wywołaj metodę deleteUser
        mockMvc.perform(post(DELETE_USER_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createTokenRequest(tokenAccess).toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Delete successfully"));
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

    private JsonObject createChangePasswordRequest(String token, String password, String confirmPassword) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("confirmPassword", confirmPassword);
        return jsonObject;
    }

}