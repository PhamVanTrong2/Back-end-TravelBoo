package com.bootravel.controller;

import com.bootravel.payload.requests.SetPriceByDateRequest;
import com.bootravel.payload.responses.data.ResultResponse;
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

import java.text.SimpleDateFormat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SetPriceByDateTest {
    @Autowired
    private MockMvc mvc;

    private final String authToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsdW9uZ1Rlc3QyIiwiZXhwIjoxNzg5MTg1OTA2LCJpYXQiOjE3MDI3ODU5MDZ9.axtoNuBwp-V0io6sLCUlAKdLps42RO2cQw-h_l2WQ9C7jWgpTseT85Wj5XvdkMe2LXrVRaM-5gpXmuChuOxt4g";

    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    private String executeRequest(SetPriceByDateRequest request) throws Exception {

        String content = (new ObjectMapper()).writeValueAsString(request);

        final MvcResult result = this.mvc
                .perform(
                        post("/room/set-price")
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-LOCALE", "en")
                                .header("Authorization", authToken)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        return result.getResponse().getContentAsString();
    }

    public static ResultResponse mapJsonToObject(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        String message = jsonObject.getString("message");
        int code = jsonObject.getInt("code");
        ResultResponse responseData = new ResultResponse();
        responseData.setCode(code);
        responseData.setMessage(message);
        return responseData;
    }

    @Order(1)
    @Test
    void should_failed_when_all_field_is_null() throws Exception {
        SetPriceByDateRequest request = new SetPriceByDateRequest();
        request.setRoomId(null);
        request.setPrice(null);
        request.setDateFrom(null);
        request.setDateTo(null);

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(400);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Field is not null");
    }

    @Order(2)
    @Test
    void should_failed_when_room_not_exist() throws Exception {
        SetPriceByDateRequest request = new SetPriceByDateRequest();
        request.setRoomId(0L);
        request.setPrice(150000L);
        request.setDateFrom(format.parse("11/11/2023"));
        request.setDateTo(format.parse("26/12/2023"));

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(400);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Room is not exist");
    }

    @Order(3)
    @Test
    void should_failed_when_date_from_greater_than_date_to() throws Exception {
        SetPriceByDateRequest request = new SetPriceByDateRequest();
        request.setRoomId(108L);
        request.setPrice(150000L);
        request.setDateFrom(format.parse("11/11/2023"));
        request.setDateTo(format.parse("5/11/2023"));

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(400);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("DateTo must be greater then DateFrom");
    }

    @Order(4)
    @Test
    void should_failed_when_price_less_0() throws Exception {
        SetPriceByDateRequest request = new SetPriceByDateRequest();
        request.setRoomId(108L);
        request.setPrice(-5000L);
        request.setDateFrom(format.parse("11/11/2023"));
        request.setDateTo(format.parse("26/12/2023"));

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(400);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Price must be greater than 0");
    }

    @Order(5)
    @Test
    void should_failed_when_price_equals_0() throws Exception {
        SetPriceByDateRequest request = new SetPriceByDateRequest();
        request.setRoomId(108L);
        request.setPrice(0L);
        request.setDateFrom(format.parse("11/11/2023"));
        request.setDateTo(format.parse("26/12/2023"));

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(400);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Price must be greater than 0");
    }

    @Order(6)
    @Test
    void should_failed_when_price_less_min_price() throws Exception {
        SetPriceByDateRequest request = new SetPriceByDateRequest();
        request.setRoomId(108L);
        request.setPrice(5000L);
        request.setDateFrom(format.parse("11/11/2023"));
        request.setDateTo(format.parse("26/12/2023"));

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(409);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Price update must be less than min price");
    }

    @Order(7)
    @Test
    void update_success_with_price_equal_min_price() throws Exception {
        SetPriceByDateRequest request = new SetPriceByDateRequest();
        request.setRoomId(108L);
        request.setPrice(100000L);
        request.setDateFrom(format.parse("11/11/2023"));
        request.setDateTo(format.parse("26/12/2023"));

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(200);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Update successfully");
    }

    @Order(8)
    @Test
    void update_success_with_set_1_day() throws Exception {
        SetPriceByDateRequest request = new SetPriceByDateRequest();
        request.setRoomId(108L);
        request.setPrice(150000L);
        request.setDateFrom(format.parse("11/11/2023"));
        request.setDateTo(format.parse("11/11/2023"));

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(200);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Update successfully");
    }

    @Order(9)
    @Test
    void update_success_with_set_multiple_days() throws Exception {
        SetPriceByDateRequest request = new SetPriceByDateRequest();
        request.setRoomId(108L);
        request.setPrice(150000L);
        request.setDateFrom(format.parse("11/11/2023"));
        request.setDateTo(format.parse("26/12/2023"));

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(200);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Update successfully");
    }
}
