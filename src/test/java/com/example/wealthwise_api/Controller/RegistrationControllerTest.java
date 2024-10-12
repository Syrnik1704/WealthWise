package com.example.wealthwise_api.Controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.example.wealthwise_api.Repository.UserEntityRepository;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private final String REGISTER_URL = "https://localhost:8443/auth/register";
    private final UserEntityRepository userEntityRepository;

    @Autowired
    RegistrationControllerTest(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }


    private JsonObject createRegisterRequest(String name, String surname, String birthDay, String email, String password, String confirmPassword) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("surname", surname);
        jsonObject.addProperty("birthDay", birthDay);
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("confirmPassword", confirmPassword);
        return jsonObject;
    }

    @AfterEach
    public void deleteTestUser() {
        userEntityRepository.deleteAll();
    }

    @Test
    void shouldReturnOk_whenSuccessful() throws Exception {
        // Act and Assert
        mockMvc
                .perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRegisterRequest("jan", "kowalski", "12-12-2001", "jan.kowalski@example.com", "Password1", "Password1").toString()))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"User jan.kowalski@example.com registered successfully\"}"));

    }

    @Test
    void shouldReturnBadRequest_whenFieldsAreMissing() throws Exception {
        // Act and Assert
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRegisterRequest("", "kowalski", "12-12-2001", "jan.kowalski@example.com", "Password1", "Password1").toString()))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Please fill all fields!\"}"));
    }

    @Test
    void shouldReturnBadRequest_whenInvalidEmail() throws Exception {
        // Act and Assert
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRegisterRequest("jan", "kowalski", "12-12-2001", "invalidEmail", "Password1", "Password1").toString()))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Email is not valid!\"}"));
    }

    @Test
    void shouldReturnBadRequest_whenInvalidPassword() throws Exception {
        // Act and Assert
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRegisterRequest("jan", "kowalski", "12-12-2001", "jan.kowalski@example.com", "weakPassword", "weakPassword").toString()))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Password is not valid!\"}"));
    }

    @Test
    void shouldReturnBadRequest_whenPasswordMismatch() throws Exception {
        // Act and Assert
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRegisterRequest("jan", "kowalski", "12-12-2001", "jan.kowalski@example.com", "Password1", "Password2").toString()))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Passwords do not match!\"}"));
    }

}