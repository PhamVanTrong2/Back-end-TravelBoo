package com.bootravel.controller;

import com.bootravel.common.constant.MasterDataConstants;
import com.bootravel.entity.PromotionsEntity;
import com.bootravel.payload.requests.SearchPromotionRequest;
import com.bootravel.payload.responses.SearchPromotionResponse;
import com.bootravel.payload.responses.constant.ResponseType;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListWithMetaData;
import com.bootravel.service.BookingService;
import com.bootravel.service.PromotionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class PromotionControllerTest {
       @InjectMocks
       private PromotionController promotionController;
       @Mock
       private PromotionService promotionService;

       @Before
        public void setUp() {
            MockitoAnnotations.initMocks(this);
        }

        // GET request to /promotion/{id} returns a ResponseData<PromotionsEntity> object
        @Test
        public void test_getPromotionById_returnsResponseData() {
            // Arrange
            Long id = 1L;

            // Mock the PromotionService
            ResponseData<PromotionsEntity> responseData = new ResponseData<>();
            responseData.setType(ResponseType.OK);
            responseData.setCode(200);
            PromotionsEntity mockPromotionEntity = new PromotionsEntity();
            mockPromotionEntity.setId(id);
            mockPromotionEntity.setName("Test Promotion");
            mockPromotionEntity.setDescription("Test Description");
            mockPromotionEntity.setCreatedDate(new Timestamp(Instant.now().toEpochMilli()));
            mockPromotionEntity.setModifiedDate(new Timestamp(Instant.now().toEpochMilli()));
            mockPromotionEntity.setStatus("1");
            mockPromotionEntity.setStartDate(new Timestamp(Instant.now().toEpochMilli()));
            mockPromotionEntity.setEndDate(new Timestamp(Instant.now().toEpochMilli()));
            mockPromotionEntity.setDiscountPercent(10);
            mockPromotionEntity.setMaxDiscount(BigDecimal.valueOf(100));
            mockPromotionEntity.setMaxUse(1);
            responseData.setData(mockPromotionEntity);

            // Ensure to return a non-null data in the ResponseData
            responseData.setData(mockPromotionEntity);

            Mockito.when(promotionService.getPromotionById(id)).thenReturn(responseData);

            // Set the mocked PromotionService in the controller
            promotionController = new PromotionController(promotionService);

            // Act
            ResponseData<PromotionsEntity> response = promotionController.getPromotionById(id);

            // Assert
            assertNotNull(response);
            assertEquals(ResponseType.OK, response.getType());
            assertEquals(200, response.getCode());
            assertNotNull(response.getData());
        }


    // POST request to /promotion/list with valid SearchPromotionRequest returns a ResponseListWithMetaData<SearchPromotionResponse> object
//        @Test
//        public void test_listPromotion_withValidRequest_returnsResponseListWithMetaData() throws SQLException {
//            // Arrange
//            SearchPromotionRequest request = new SearchPromotionRequest();
//            request.setSearchParams("searchParams");
//            promotionController = new PromotionController(promotionService);
//            // Act
//            ResponseListWithMetaData<SearchPromotionResponse> response = promotionController.listPromotion(request);
//            Mockito.when(promotionService.getPromotionById(id)).thenReturn(responseData);
//            // Assert
//            assertNotNull(response);
//            assertEquals(MasterDataConstants.SUCCESS_TYPE, response.getType());
//            assertEquals(MasterDataConstants.SUCCESS_CODE, response.getCode());
//            assertNotNull(response.getData());
//        }
//
//        // POST request to /promotion/create with valid CreatePromotionRequest and image file returns a ResultResponse object
//        @Test
//        public void test_createPromotion_withValidRequestAndImage_returnsResultResponse() throws IOException, ParseException {
//            // Arrange
//            String code = "code";
//            String name = "name";
//            String description = "description";
//            String startDate = "2022-01-01";
//            String endDate = "2022-01-31";
//            Integer typePromotion = 1;
//            String discountPercent = "10";
//            String maxDiscount = "100";
//            String fixMoneyDiscount = "50";
//            String maxUse = "5";
//            Integer typeMaxUse = 1;
//            MultipartFile image = new MockMultipartFile("image", new byte[0]);
//
//            // Act
//            ResultResponse response = promotionController.createPromotion(code, name, description, startDate, endDate, typePromotion, discountPercent, maxDiscount, fixMoneyDiscount, maxUse, typeMaxUse, image);
//
//            // Assert
//            assertNotNull(response);
//            assertEquals(ResultResponse.SUCCESS, response.getMessage());
//            assertEquals(200, response.getCode());
//        }
//
//        // POST request to /promotion/update-status with valid UpdateStatusPromotionRequest returns a ResultResponse object
//        @Test
//        public void test_updateStatus_withValidRequest_returnsResultResponse() {
//            // Arrange
//            UpdateStatusPromotionRequest request = new UpdateStatusPromotionRequest();
//            request.setId(1L);
//            request.setStatus("active");
//
//            // Act
//            ResultResponse response = promotionController.updateStatus(request);
//
//            // Assert
//            assertNotNull(response);
//            assertEquals(ResultResponse.SUCCESS, response.getMessage());
//            assertEquals(200, response.getCode());
//        }
//
//        // POST request to /promotion/update with valid UpdatePromotionRequest returns a ResultResponse object
//        @Test
//        public void test_update_withValidRequest_returnsResultResponse() {
//            // Arrange
//            UpdatePromotionRequest request = new UpdatePromotionRequest();
//            request.setId(1L);
//            request.setName("new name");
//
//            // Act
//            ResultResponse response = promotionController.update(request);
//
//            // Assert
//            assertNotNull(response);
//            assertEquals(ResultResponse.SUCCESS, response.getMessage());
//            assertEquals(200, response.getCode());
//        }
//
//        // GET request to /promotion/check-price-promotion with valid parameters returns a PromotionResponseTotal object
//        @Test
//        public void test_checkPromotionIsValidAndTotal_withValidParameters_returnsPromotionResponseTotal() throws Exception {
//            // Arrange
//            String promotionCode = "code";
//            Integer totalDay = 5;
//            long roomId = 1L;
//            Date checkin = new Date();
//            Integer numberRoom = 2;
//
//            // Act
//            PromotionResponseTotal response = promotionController.checkPromotionIsValidAndTotal(promotionCode, totalDay, roomId, checkin, numberRoom);
//
//            // Assert
//            assertNotNull(response);
//            assertNotNull(response.getTotal());
//            assertNotNull(response.getTotalRedemption());
//        }
//
//        // GET request to /promotion/{id} with non-existent id returns a ResponseData object with error message and code 404
//        @Test
//        public void test_getPromotionById_withNonExistentId_returnsResponseDataWithErrorMessageAndCode404() {
//            // Arrange
//            Long id = 999L;
//
//            // Act
//            ResponseData<PromotionsEntity> response = promotionController.getPromotionById(id);
//
//            // Assert
//            assertNotNull(response);
//            assertEquals(ResponseType.ERROR, response.getType());
//            assertEquals(404, response.getCode());
//            assertNull(response.getData());
//        }
//
//        // POST request to /promotion/list with invalid SearchPromotionRequest returns a ResponseListWithMetaData object with error message and code 400
//        @Test
//        public void test_listPromotion_withInvalidRequest_returnsResponseListWithMetaDataWithErrorMessageAndCode400() throws SQLException {
//            // Arrange
//            SearchPromotionRequest request = new SearchPromotionRequest();
//
//            // Act
//            ResponseListWithMetaData<SearchPromotionResponse> response = promotionController.listPromotion(request);
//
//            // Assert
//            assertNotNull(response);
//            assertEquals(MasterDataConstants.ERROR_TYPE, response.getType());
//            assertEquals(MasterDataConstants.ERROR_CODE, response.getCode());
//            assertNull(response.getData());
//        }
//
//        // POST request to /promotion/create with invalid CreatePromotionRequest returns a ResultResponse object with error message and code 400
//        @Test
//        public void test_createPromotion_withInvalidRequest_returnsResultResponseWithErrorMessageAndCode400() throws IOException, ParseException {
//
//            // POST request to /promotion/update-status with invalid UpdateStatusPromotionRequest returns a ResultResponse object with error message and code 400
//            @Test
//            public void test_updateStatus_withInvalidRequest_returnsResultResponseWithError() {
//                // Arrange
//                UpdateStatusPromotionRequest request = new UpdateStatusPromotionRequest();
//                request.setId(1L);
//                request.setStatus("invalid_status");
//
//                // Act
//                ResultResponse response = promotionController.updateStatus(request);
//
//                // Assert
//                assertNotNull(response);
//                assertEquals(ResponseType.ERROR, response.getType());
//                assertEquals(400, response.getCode());
//                assertEquals("Invalid status", response.getMessage());
//            }
//
//            // POST request to /promotion/update with invalid UpdatePromotionRequest returns a ResultResponse object with error message and code 400
//            @Test
//            public void test_update_withInvalidRequest_returnsResultResponseWithError() {
//                // Arrange
//                UpdatePromotionRequest request = new UpdatePromotionRequest();
//                request.setId(1L);
//                request.setName("");
//
//                // Act
//                ResultResponse response = promotionController.update(request);
//
//                // Assert
//                assertNotNull(response);
//                assertEquals(ResponseType.ERROR, response.getType());
//                assertEquals(400, response.getCode());
//                assertEquals("Invalid name", response.getMessage());
//            }
//
//            // GET request to /promotion/check-price-promotion with invalid parameters returns a PromotionResponseTotal object with error message and code 400
//            @Test
//            public void test_checkPricePromotion_withInvalidParameters_returnsPromotionResponseTotalWithError() {
//                // Arrange
//                String promotionCode = "invalid_code";
//                Integer totalDay = -1;
//                long roomId = 1L;
//                Date checkin = new Date();
//                Integer numberRoom = 0;
//
//                // Act
//                PromotionResponseTotal response = promotionController.checkPricePromotion(promotionCode, totalDay, roomId, checkin, numberRoom);
//
//                // Assert
//                assertNotNull(response);
//                assertEquals(ResponseType.ERROR, response.getType());
//                assertEquals(400, response.getCode());
//                assertEquals("Invalid parameters", response.getMessage());
//            }

//        }
}