package com.bootravel.controller;

import com.bootravel.payload.requests.UpdatePromotionRequest;
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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UpdatePromotionTest {

    private final String authToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtYXJrZXRpbmcxIiwiZXhwIjoxNzg5MTM5MDEzLCJpYXQiOjE3MDI3MzkwMTN9.iwhOZfnzUvQwzKOulZzjaxPPWtFPvUnWn672bd04EuJglzc5hj7fU9hmt4sTmsJSyR6pLwaWWdWCD9-hkdQDBQ";

    @Autowired
    private MockMvc mvc;

    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    private String executeRequest(UpdatePromotionRequest request) throws Exception {

        String content = (new ObjectMapper()).writeValueAsString(request);

        final MvcResult result = this.mvc
                .perform(
                        post("/promotion/update")
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
    void should_failed_when_field_is_null() throws Exception{
        UpdatePromotionRequest request = new UpdatePromotionRequest();
        request.setId(null);
        request.setName(null);
        request.setDescription(null);
        request.setStartDate(null);
        request.setEndDate(null);
        request.setTypePromotion(null);
        request.setDiscountPercent(null);
        request.setMaxDiscount(null);
        request.setFixMoneyDiscount(null);
        request.setTypeMaxUse(null);
        request.setMaxUse(null);

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(500);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Fields is not null");
    }

    @Order(2)
    @Test
    void should_failed_when_field_is_null_2() throws Exception{
        UpdatePromotionRequest request = new UpdatePromotionRequest();
        request.setId(0L);
        request.setName(null);
        request.setDescription(null);
        request.setStartDate(null);
        request.setEndDate(null);
        request.setTypePromotion(null);
        request.setDiscountPercent(null);
        request.setMaxDiscount(null);
        request.setFixMoneyDiscount(null);
        request.setTypeMaxUse(null);
        request.setMaxUse(null);

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(500);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Fields is not null");
    }

    @Order(3)
    @Test
    void should_failed_when_end_date_less_then_current() throws Exception{
        UpdatePromotionRequest request = new UpdatePromotionRequest();
        request.setId(1L);
        request.setName("Happy update");
        request.setDescription("update description");
        request.setStartDate(format.parse("20/11/2023"));
        request.setEndDate(format.parse("25/11/2023"));
        request.setTypePromotion(1);
        request.setDiscountPercent(null);
        request.setMaxDiscount(null);
        request.setFixMoneyDiscount(null);
        request.setTypeMaxUse(1);
        request.setMaxUse(null);

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(409);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("End date must be greater than current date");
    }

    @Order(4)
    @Test
    void should_failed_when_id_not_exist() throws Exception{
        UpdatePromotionRequest request = new UpdatePromotionRequest();
        request.setId(0L);
        request.setName("Happy update");
        request.setDescription("update description");
        request.setStartDate(format.parse("20/11/2023"));
        request.setEndDate(format.parse("25/12/2023"));
        request.setTypePromotion(1);
        request.setDiscountPercent(null);
        request.setMaxDiscount(null);
        request.setFixMoneyDiscount(null);
        request.setTypeMaxUse(1);
        request.setMaxUse(null);

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(404);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Promotion has not been existed");
    }

    @Order(5)
    @Test
    void should_failed_when_type_promotion_wrong() throws Exception{
        UpdatePromotionRequest request = new UpdatePromotionRequest();
        request.setId(1L);
        request.setName("Happy update");
        request.setDescription("update description");
        request.setStartDate(format.parse("20/11/2023"));
        request.setEndDate(format.parse("25/12/2023"));
        request.setTypePromotion(0);
        request.setDiscountPercent(10);
        request.setMaxDiscount(new BigDecimal(20000));
        request.setFixMoneyDiscount(null);
        request.setTypeMaxUse(1);
        request.setMaxUse(null);

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(400);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Type is not correct");
    }

    @Order(6)
    @Test
    void should_failed_when_discount_percent_wrong() throws Exception{
        UpdatePromotionRequest request = new UpdatePromotionRequest();
        request.setId(1L);
        request.setName("Happy update");
        request.setDescription("update description");
        request.setStartDate(format.parse("20/11/2023"));
        request.setEndDate(format.parse("25/12/2023"));
        request.setTypePromotion(1);
        request.setDiscountPercent(0);
        request.setMaxDiscount(new BigDecimal(20000));
        request.setFixMoneyDiscount(null);
        request.setTypeMaxUse(1);
        request.setMaxUse(null);

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(400);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Value must be greater than 0");
    }

    @Order(7)
    @Test
    void should_failed_when_max_discount_wrong() throws Exception{
        UpdatePromotionRequest request = new UpdatePromotionRequest();
        request.setId(1L);
        request.setName("Happy update");
        request.setDescription("update description");
        request.setStartDate(format.parse("20/11/2023"));
        request.setEndDate(format.parse("25/12/2023"));
        request.setTypePromotion(1);
        request.setDiscountPercent(10);
        request.setMaxDiscount(new BigDecimal(0));
        request.setFixMoneyDiscount(null);
        request.setTypeMaxUse(1);
        request.setMaxUse(null);

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(400);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Value must be greater than 0");
    }

    @Order(8)
    @Test
    void should_failed_when_fix_money_discount_wrong() throws Exception{
        UpdatePromotionRequest request = new UpdatePromotionRequest();
        request.setId(1L);
        request.setName("Happy update");
        request.setDescription("update description");
        request.setStartDate(format.parse("20/11/2023"));
        request.setEndDate(format.parse("25/12/2023"));
        request.setTypePromotion(2);
        request.setDiscountPercent(null);
        request.setMaxDiscount(null);
        request.setFixMoneyDiscount(new BigDecimal(0));
        request.setTypeMaxUse(1);
        request.setMaxUse(null);

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(400);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Value must be greater than 0");
    }

    @Order(9)
    @Test
    void should_failed_when_type_max_use_wrong() throws Exception{
        UpdatePromotionRequest request = new UpdatePromotionRequest();
        request.setId(1L);
        request.setName("Happy update");
        request.setDescription("update description");
        request.setStartDate(format.parse("20/11/2023"));
        request.setEndDate(format.parse("25/12/2023"));
        request.setTypePromotion(2);
        request.setDiscountPercent(null);
        request.setMaxDiscount(null);
        request.setFixMoneyDiscount(new BigDecimal(30000));
        request.setTypeMaxUse(0);
        request.setMaxUse(null);

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(400);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Type is not correct");
    }

    @Order(10)
    @Test
    void should_failed_when_max_use_wrong() throws Exception{
        UpdatePromotionRequest request = new UpdatePromotionRequest();
        request.setId(1L);
        request.setName("Happy update");
        request.setDescription("update description");
        request.setStartDate(format.parse("20/11/2023"));
        request.setEndDate(format.parse("25/12/2023"));
        request.setTypePromotion(2);
        request.setDiscountPercent(null);
        request.setMaxDiscount(null);
        request.setFixMoneyDiscount(new BigDecimal(30000));
        request.setTypeMaxUse(2);
        request.setMaxUse(-5);

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(400);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Value must be greater than 0");
    }

    @Order(11)
    @Test
    void should_failed_when_discount_percent_wrong_2() throws Exception{
        UpdatePromotionRequest request = new UpdatePromotionRequest();
        request.setId(1L);
        request.setName("Happy update");
        request.setDescription("update description");
        request.setStartDate(format.parse("20/11/2023"));
        request.setEndDate(format.parse("25/12/2023"));
        request.setTypePromotion(1);
        request.setDiscountPercent(-5);
        request.setMaxDiscount(new BigDecimal(20000));
        request.setFixMoneyDiscount(null);
        request.setTypeMaxUse(1);
        request.setMaxUse(null);

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(400);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Value must be greater than 0");
    }

    @Order(12)
    @Test
    void should_failed_when_max_discount_wrong_2() throws Exception{
        UpdatePromotionRequest request = new UpdatePromotionRequest();
        request.setId(1L);
        request.setName("Happy update");
        request.setDescription("update description");
        request.setStartDate(format.parse("20/11/2023"));
        request.setEndDate(format.parse("25/12/2023"));
        request.setTypePromotion(1);
        request.setDiscountPercent(10);
        request.setMaxDiscount(new BigDecimal(-5));
        request.setFixMoneyDiscount(null);
        request.setTypeMaxUse(1);
        request.setMaxUse(null);

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(400);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Value must be greater than 0");
    }

    @Order(13)
    @Test
    void should_failed_when_fix_money_discount_2() throws Exception{
        UpdatePromotionRequest request = new UpdatePromotionRequest();
        request.setId(1L);
        request.setName("Happy update");
        request.setDescription("update description");
        request.setStartDate(format.parse("20/11/2023"));
        request.setEndDate(format.parse("25/12/2023"));
        request.setTypePromotion(2);
        request.setDiscountPercent(null);
        request.setMaxDiscount(null);
        request.setFixMoneyDiscount(new BigDecimal(-5));
        request.setTypeMaxUse(1);
        request.setMaxUse(null);

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(400);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("Value must be greater than 0");
    }

    @Order(14)
    @Test
    void update_success() throws Exception{
        UpdatePromotionRequest request = new UpdatePromotionRequest();
        request.setId(1L);
        request.setName("Happy update");
        request.setDescription("update description");
        request.setStartDate(format.parse("20/11/2023"));
        request.setEndDate(format.parse("25/12/2023"));
        request.setTypePromotion(1);
        request.setDiscountPercent(10);
        request.setMaxDiscount(null);
        request.setFixMoneyDiscount(null);
        request.setTypeMaxUse(1);
        request.setMaxUse(null);

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(200);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("");
    }

    @Order(15)
    @Test
    void update_success_2() throws Exception{
        UpdatePromotionRequest request = new UpdatePromotionRequest();
        request.setId(1L);
        request.setName("Happy update");
        request.setDescription("update description");
        request.setStartDate(format.parse("20/11/2023"));
        request.setEndDate(format.parse("25/12/2023"));
        request.setTypePromotion(1);
        request.setDiscountPercent(10);
        request.setMaxDiscount(new BigDecimal(10000));
        request.setFixMoneyDiscount(null);
        request.setTypeMaxUse(1);
        request.setMaxUse(null);

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(200);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("");
    }

    @Order(16)
    @Test
    void update_success_3() throws Exception{
        UpdatePromotionRequest request = new UpdatePromotionRequest();
        request.setId(1L);
        request.setName("Happy update");
        request.setDescription("update description");
        request.setStartDate(format.parse("20/11/2023"));
        request.setEndDate(format.parse("25/12/2023"));
        request.setTypePromotion(2);
        request.setDiscountPercent(null);
        request.setMaxDiscount(null);
        request.setFixMoneyDiscount(new BigDecimal(30000));
        request.setTypeMaxUse(1);
        request.setMaxUse(null);

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(200);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("");
    }

    @Order(17)
    @Test
    void update_success_4() throws Exception{
        UpdatePromotionRequest request = new UpdatePromotionRequest();
        request.setId(1L);
        request.setName("Happy update");
        request.setDescription("update description");
        request.setStartDate(format.parse("20/11/2023"));
        request.setEndDate(format.parse("25/12/2023"));
        request.setTypePromotion(2);
        request.setDiscountPercent(null);
        request.setMaxDiscount(null);
        request.setFixMoneyDiscount(new BigDecimal(30000));
        request.setTypeMaxUse(2);
        request.setMaxUse(10);

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(200);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("");
    }

    @Order(18)
    @Test
    void update_success_6() throws Exception{
        UpdatePromotionRequest request = new UpdatePromotionRequest();
        request.setId(1L);
        request.setName("Happy update");
        request.setDescription("update description");
        request.setStartDate(format.parse("20/11/2023"));
        request.setEndDate(format.parse("25/12/2023"));
        request.setTypePromotion(1);
        request.setDiscountPercent(10);
        request.setMaxDiscount(new BigDecimal(20000));
        request.setFixMoneyDiscount(null);
        request.setTypeMaxUse(2);
        request.setMaxUse(10);

        String response = this.executeRequest(request);
        ResultResponse responseData = mapJsonToObject(response);
        Assertions.assertThat(responseData.getCode()).isEqualTo(200);
        Assertions.assertThat(responseData.getMessage()).isEqualTo("");
    }
}
