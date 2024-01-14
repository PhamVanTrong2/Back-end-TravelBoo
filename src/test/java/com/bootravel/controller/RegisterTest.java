package com.bootravel.controller;

import com.bootravel.entity.UsersEntity;
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
public class RegisterTest {

    @Autowired
    private MockMvc mvc;

    private String executeFailRequest(UsersEntity request) throws Exception {

        String content = (new ObjectMapper()).writeValueAsString(request);

        final MvcResult result = this.mvc
                .perform(
                        post("/registered-users/process-register")
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
    void should_failed_when_all_field_is_null() throws Exception {
        UsersEntity request = new UsersEntity();
        request.setUsername(null);
        request.setPassword(null);
        request.setEmail(null);

        String response = this.executeFailRequest(request);
        ResponseData<String> responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(500);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("User name or email, password is invalid!");
    }

    @Order(2)
    @Test
    void should_failed_when_username_is_null() throws Exception {
        UsersEntity request = new UsersEntity();
        request.setUsername(null);
        request.setPassword("123456");
        request.setEmail("cenoz306@gmail.com");

        String response = this.executeFailRequest(request);
        ResponseData<String> responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(500);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("User name or email, password is invalid!");
    }

    @Order(3)
    @Test
    void should_failed_when_password_is_null() throws Exception {
        UsersEntity request = new UsersEntity();
        request.setUsername("newuser");
        request.setPassword(null);
        request.setEmail("cenoz306@gmail.com");

        String response = this.executeFailRequest(request);
        ResponseData<String> responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(500);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("User name or email, password is invalid!");
    }

    @Order(4)
    @Test
    void should_failed_when_email_is_null() throws Exception {
        UsersEntity request = new UsersEntity();
        request.setUsername("newuser");
        request.setPassword("123456");
        request.setEmail(null);

        String response = this.executeFailRequest(request);
        ResponseData<String> responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(500);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("User name or email, password is invalid!");
    }

    @Order(5)
    @Test
    void should_failed_when_all_field_is_blank() throws Exception {
        UsersEntity request = new UsersEntity();
        request.setUsername("");
        request.setPassword("");
        request.setEmail("");

        String response = this.executeFailRequest(request);
        ResponseData<String> responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(500);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("User name or email, password is invalid!");
    }

    @Order(6)
    @Test
    void should_failed_when_username_is_blank() throws Exception {
        UsersEntity request = new UsersEntity();
        request.setUsername("");
        request.setPassword("123456");
        request.setEmail("cenoz306@gmail.com");

        String response = this.executeFailRequest(request);
        ResponseData<String> responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(500);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("User name or email, password is invalid!");
    }

    @Order(7)
    @Test
    void should_failed_when_password_is_blank() throws Exception {
        UsersEntity request = new UsersEntity();
        request.setUsername("newuser");
        request.setPassword("");
        request.setEmail("cenoz306@gmail.com");

        String response = this.executeFailRequest(request);
        ResponseData<String> responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(500);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("User name or email, password is invalid!");
    }

    @Order(8)
    @Test
    void should_failed_when_email_is_blank() throws Exception {
        UsersEntity request = new UsersEntity();
        request.setUsername("newuser");
        request.setPassword("123456");
        request.setEmail("");

        String response = this.executeFailRequest(request);
        ResponseData<String> responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(500);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("User name or email, password is invalid!");
    }

    @Order(9)
    @Test
    void should_failed_when_email_is_invalid() throws Exception {
        UsersEntity request = new UsersEntity();
        request.setUsername("newuser");
        request.setPassword("123456");
        request.setEmail("abc");

        String response = this.executeFailRequest(request);
        ResponseData<String> responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(500);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Email is invalid");
    }

    @Order(10)
    @Test
    void should_failed_when_username_and_email_existed() throws Exception {
        UsersEntity request = new UsersEntity();
        request.setUsername("luong2");
        request.setPassword("123456");
        request.setEmail("lhchung306@gmail");

        String content = (new ObjectMapper()).writeValueAsString(request);

        MvcResult result = this.mvc
                .perform(
                        post("/registered-users/process-register")
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-LOCALE", "en")
                                .header("X-FROM", "http://localhost:3000/")
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();

        ResponseData<String> responseData = mapJsonToObject(result.getResponse().getContentAsString());

        Assertions.assertThat(responseData.getCode()).isEqualTo(500);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("User name or email is already in use!");
    }

    @Order(11)
    @Test
    void should_failed_when_username_existed() throws Exception {
        UsersEntity request = new UsersEntity();
        request.setUsername("luong2");
        request.setPassword("123456");
        request.setEmail("cenoz306@gmail.com");

        String content = (new ObjectMapper()).writeValueAsString(request);

        MvcResult result = this.mvc
                .perform(
                        post("/registered-users/process-register")
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-LOCALE", "en")
                                .header("X-FROM", "http://localhost:3000/")
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();

        ResponseData<String> responseData = mapJsonToObject(result.getResponse().getContentAsString());

        Assertions.assertThat(responseData.getCode()).isEqualTo(500);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("User name or email is already in use!");
    }

    @Order(12)
    @Test
    void should_failed_when_email_existed() throws Exception {
        UsersEntity request = new UsersEntity();
        request.setUsername("newuser");
        request.setPassword("123456");
        request.setEmail("lhchung306@gmail.com");

        String content = (new ObjectMapper()).writeValueAsString(request);

        MvcResult result = this.mvc
                .perform(
                        post("/registered-users/process-register")
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-LOCALE", "en")
                                .header("X-FROM", "http://localhost:3000/")
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();

        ResponseData<String> responseData = mapJsonToObject(result.getResponse().getContentAsString());

        Assertions.assertThat(responseData.getCode()).isEqualTo(500);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("User name or email is already in use!");
    }

    @Order(13)
    @Test
    void register_success() throws Exception {
        UsersEntity request = new UsersEntity();
        request.setUsername("newuser");
        request.setPassword("123456");
        request.setEmail("cenoz306@gmail.com");
        String content = (new ObjectMapper()).writeValueAsString(request);

        MvcResult result = this.mvc
                .perform(
                        post("/registered-users/process-register")
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-LOCALE", "en")
                                .header("X-FROM", "http://localhost:3000/")
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();

        ResponseData<String> responseData = mapJsonToObject(result.getResponse().getContentAsString());

        Assertions.assertThat(responseData.getCode()).isEqualTo(200);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Register successfully");

    }
}
