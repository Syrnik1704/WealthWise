package com.example.wealthwise_api.Controller;

import com.example.wealthwise_api.DTO.AssetsListRequestDelete;
import com.example.wealthwise_api.DTO.AssetsRequestDelete;
import com.example.wealthwise_api.Entity.*;
import com.example.wealthwise_api.Repository.*;
import com.google.gson.JsonArray;
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
import java.util.Collections;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AssetControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final String ADD_ASSET_URL = "https://localhost:8443/asset/addAsset";
    private final String GET_ASSET_URL = "https://localhost:8443/asset/getAsset";
    private final String DELETE_ASSET_URL = "https://localhost:8443/asset/deleteAsset";
    private final String LOGIN_URL = "https://localhost:8443/auth/login";

    private final PasswordEncoder passwordEncoder;

    private final UserEntityRepository userEntityRepository;

    private final AssetsRepository assetsRepository;

    private final String USER_EMAIL = "jan.kowalski@example.com";
    private final String USER_PASSWORD = "Password1";
    private String tokenAccess;

    @Autowired
    AssetControllerTest(PasswordEncoder passwordEncoder, UserEntityRepository userEntityRepository, AssetsRepository assetsRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userEntityRepository = userEntityRepository;
        this.assetsRepository = assetsRepository;
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
    public void shouldSaveAssetSuccessfully() throws Exception {
        // Przygotuj dane dla żądania zapisywania aktywów
        String name = "Obligacje";
        String currency = "PLN";
        double value = 200.0;

        mockMvc.perform(post(ADD_ASSET_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAssetsRequest(tokenAccess, currency, name, value).toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Assets has been saved successfully")));

        // Sprawdź, czy aktywo zostało zapisane w bazie danych
        Assets savedAsset = assetsRepository.findAll().get(0);
        assertEquals(value, savedAsset.getValue());
        assertEquals(name, savedAsset.getName());
        assertEquals(currency, savedAsset.getCurrency());
    }

    @Test
    public void shouldReturnBadRequestWhenDataMissing() throws Exception {
        // Przygotuj dane dla żądania zapisywania aktywów z brakującymi danymi
        String name = "";
        String currency = "";
        double value = -50.0;

        mockMvc.perform(post(ADD_ASSET_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAssetsRequest(tokenAccess,currency, name, value).toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Missing data")));

        // Sprawdź, czy żadne aktywo nie zostało zapisane w bazie danych
        assertEquals(0, assetsRepository.count());
    }

    @Test
    public void shouldDeleteAssetsSuccessfully() throws Exception {
        // Dodaj aktywo do bazy danych
        Assets assets = new Assets(1000.0, "PLN", "Akcje", userEntityRepository.findByEmail(USER_EMAIL));
        assetsRepository.save(assets);

        // Przygotuj dane dla żądania usuwania aktywów
        List<AssetsRequestDelete> assetsRequestDeleteList = List.of(
                new AssetsRequestDelete("PLN", "Akcje", 1000.0)
        );

        mockMvc.perform(post(DELETE_ASSET_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAssetsListRequestDelete(tokenAccess, assetsRequestDeleteList).toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Assets has been deleted successfully")));

        // Sprawdź, czy aktywo zostało usunięte z bazy danych
        assertEquals(0, assetsRepository.count());
    }

    @Test
    public void shouldReturnBadRequestWhenDeletingEmptyAssetsList() throws Exception {
        // Przygotuj dane dla żądania usuwania aktywów z pustą listą
        List<AssetsRequestDelete> emptyAssetsList = Collections.emptyList();

        mockMvc.perform(post(DELETE_ASSET_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAssetsListRequestDelete(tokenAccess, emptyAssetsList).toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Missing data")));

        // Sprawdź, czy żadne aktywo nie zostało usunięte z bazy danych
        assertEquals(0, assetsRepository.count());
    }

    @Test
    public void shouldReturnAssetsListSuccessfully() throws Exception {
        // Dodaj aktywo do bazy danych
        Assets assets = new Assets(1000.0, "PLN", "Akcje", userEntityRepository.findByEmail(USER_EMAIL));
        assetsRepository.save(assets);

        // Wywołaj metodę getAllAssetsList
        mockMvc.perform(post(GET_ASSET_URL)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAccess)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createTokenRequest(tokenAccess).toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currency").value("PLN"))
                .andExpect(jsonPath("$[0].name").value("Akcje"))
                .andExpect(jsonPath("$[0].value").value(1000.0));
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

    private JsonObject createAssetsRequest(String token,String currency ,String name, double value) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("currency", currency);
        jsonObject.addProperty("value", value);
        return jsonObject;
    }

    private JsonObject createAssetsListRequestDelete(String token, List<AssetsRequestDelete> assetsRequestDeleteList) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", token);

        // Utwórz tablicę JSON na podstawie listy assetsRequestDeleteList
        JsonArray jsonArray = new JsonArray();
        for (AssetsRequestDelete requestDelete : assetsRequestDeleteList) {
            JsonObject requestObject = new JsonObject();
            requestObject.addProperty("value", requestDelete.getValue());
            requestObject.addProperty("currency", requestDelete.getCurrency());
            requestObject.addProperty("name", requestDelete.getName());
            jsonArray.add(requestObject);
        }

        jsonObject.add("assetsRequestDeleteList", jsonArray);

        return jsonObject;
    }

}