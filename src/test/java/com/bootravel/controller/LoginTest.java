package com.bootravel.controller;

import com.bootravel.payload.requests.commonRequests.LoginRequest;
import com.bootravel.payload.responses.data.ResponseData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTest {

    @Autowired
    private MockMvc mvc;

    private String executeFailRequest(LoginRequest request) throws Exception {

        String content = (new ObjectMapper()).writeValueAsString(request);

        final MvcResult result = this.mvc
                .perform(
                        post("/registered-users/login")
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-LOCALE", "en")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        return result.getResponse().getContentAsString();
    }

    public static ResponseData<String> mapJsonToObject(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        String message = jsonObject.getString("message");
        int code = jsonObject.getInt("code");
        ResponseData<String> responseData = new ResponseData<>();
        responseData.setCode(code);
        responseData.setMessage(message);
        return responseData;
    }

    @Order(1)
    @Test
    void should_failed_when_username_and_password_is_null() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUserName(null);
        request.setUserPassword(null);
        String response = this.executeFailRequest(request);
        ResponseData<String> responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(401);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Username or password must not be null");
    }

    @Order(2)
    @Test
    void should_failed_when_username_is_null_1() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUserName(null);
        request.setUserPassword(" ");
        String response = this.executeFailRequest(request);
        ResponseData<String> responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(401);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Username or password must not be null");
    }

    @Order(3)
    @Test
    void should_failed_when_username_is_null_2() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUserName(null);
        request.setUserPassword("1111");
        String response = this.executeFailRequest(request);
        ResponseData<String> responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(401);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Username or password must not be null");
    }

    @Order(4)
    @Test
    void should_failed_when_username_is_null_3() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUserName(null);
        request.setUserPassword("123456");
        String response = this.executeFailRequest(request);
        ResponseData<String> responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(401);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Username or password must not be null");
    }

    @Order(5)
    @Test
    void should_failed_when_username_is_blank() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUserName(" ");
        request.setUserPassword(null);
        String response = this.executeFailRequest(request);
        ResponseData<String> responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(401);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Username or password must not be null");
    }

    @Order(6)
    @Test
    void should_failed_when_username_is_blank_2() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUserName(" ");
        request.setUserPassword(" ");
        String response = this.executeFailRequest(request);
        ResponseData<String> responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(401);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("User not found");
    }

    @Order(7)
    @Test
    void should_failed_when_username_is_blank_3() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUserName(" ");
        request.setUserPassword("1111");
        String response = this.executeFailRequest(request);
        ResponseData<String> responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(401);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("User not found");
    }

    @Order(8)
    @Test
    void should_failed_when_username_is_blank_4() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUserName(" ");
        request.setUserPassword("123456");
        String response = this.executeFailRequest(request);
        ResponseData<String> responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(401);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("User not found");
    }

    @Order(9)
    @Test
    void should_failed_when_username_is_incorrect() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUserName("nunu");
        request.setUserPassword(null);
        String response = this.executeFailRequest(request);
        ResponseData<String> responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(401);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Username or password must not be null");
    }
    @Order(10)
    @Test
    void should_failed_when_username_is_incorrect_2() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUserName("nunu");
        request.setUserPassword(" ");
        String response = this.executeFailRequest(request);
        ResponseData<String> responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(401);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("User not found");
    }
    @Order(11)
    @Test
    void should_failed_when_username_is_incorrect_3() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUserName("nunu");
        request.setUserPassword("1111");
        String response = this.executeFailRequest(request);
        ResponseData<String> responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(401);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("User not found");
    }
    @Order(12)
    @Test
    void should_failed_when_username_is_incorrect_4() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUserName("nunu");
        request.setUserPassword("123456");
        String response = this.executeFailRequest(request);
        ResponseData<String> responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(401);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("User not found");
    }

    @Order(13)
    @Test
    void should_failed_when_username_is_correct() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUserName("luong2");
        request.setUserPassword(null);
        String response = this.executeFailRequest(request);
        ResponseData<String> responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(401);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Username or password must not be null");
    }

    @Order(14)
    @Test
    void should_failed_when_username_is_correct_2() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUserName("luong2");
        request.setUserPassword(" ");

        String content = (new ObjectMapper()).writeValueAsString(request);

        final MvcResult result = this.mvc
                .perform(
                        post("/registered-users/login")
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-LOCALE", "en")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is5xxServerError()).andReturn();

        ResponseData<String> responseData = mapJsonToObject(result.getResponse().getContentAsString());
        Assertions.assertThat(responseData.getCode()).isEqualTo(500);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Bad credentials");
    }

    @Order(15)
    @Test
    void should_failed_when_username_is_correct_3() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUserName("luong2");
        request.setUserPassword("1111");

        String content = (new ObjectMapper()).writeValueAsString(request);

        final MvcResult result = this.mvc
                .perform(
                        post("/registered-users/login")
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-LOCALE", "en")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is5xxServerError()).andReturn();

        ResponseData<String> responseData = mapJsonToObject(result.getResponse().getContentAsString());
        Assertions.assertThat(responseData.getCode()).isEqualTo(500);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Bad credentials");
    }

    @Order(16)
    @Test
    void should_failed_when_account_banned() throws Exception {
        LoginRequest request = new LoginRequest();
            request.setUserName("businessadmin10");
        request.setUserPassword("123456");

        String content = (new ObjectMapper()).writeValueAsString(request);

        MvcResult result = this.mvc
                .perform(
                        post("/registered-users/login")
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-LOCALE", "en")
                                .header("X-FROM", "http://localhost:3000/")
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();

        ResponseData<String> responseData = mapJsonToObject(result.getResponse().getContentAsString());
        Assertions.assertThat(responseData.getCode()).isEqualTo(401);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Account has been banned");
    }

    @Order(17)
    @Test
    void correct_login() throws Exception{
        LoginRequest request = new LoginRequest();
        request.setUserName("luong2");
        request.setUserPassword("123456");

        String content = (new ObjectMapper()).writeValueAsString(request);

        MvcResult result = this.mvc
                .perform(
                        post("/registered-users/login")
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-LOCALE", "en")
                                .header("X-FROM", "http://localhost:3000/")
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();

        ResponseData<String> responseData = mapJsonToObject(result.getResponse().getContentAsString());
        Assertions.assertThat(responseData.getCode()).isEqualTo(200);

    }
}
