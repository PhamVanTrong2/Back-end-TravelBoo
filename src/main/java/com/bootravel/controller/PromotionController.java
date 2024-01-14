package com.bootravel.controller;

import com.bootravel.common.constant.RoleConstants;
import com.bootravel.entity.PromotionsEntity;
import com.bootravel.payload.requests.CreatePromotionRequest;
import com.bootravel.payload.requests.SearchPromotionRequest;
import com.bootravel.payload.requests.UpdatePromotionRequest;
import com.bootravel.payload.requests.UpdateStatusPromotionRequest;
import com.bootravel.payload.responses.SearchPromotionResponse;
import com.bootravel.payload.responses.commonResponses.PromotionResponseTotal;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListWithMetaData;
import com.bootravel.payload.responses.data.ResultResponse;
import com.bootravel.service.PromotionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.Null;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@RestController
@Slf4j
@RequestMapping("/promotion/")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @PreAuthorize("hasAuthority(\"" + RoleConstants.MARKETING + "\")")
    @GetMapping("{id}")
    public ResponseData<PromotionsEntity> getPromotionById(@PathVariable("id") Long id) {
        return promotionService.getPromotionById(id);
    }

    @PostMapping("list")
    @PreAuthorize("hasAuthority(\"" + RoleConstants.MARKETING + "\")")
    public ResponseListWithMetaData<SearchPromotionResponse> listPromotion(@RequestBody @Null SearchPromotionRequest request) throws SQLException {
        return promotionService.listPromotion(request);
    }

    //@PostMapping(value = "create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PostMapping("create")
    @PreAuthorize("hasAuthority(\"" + RoleConstants.MARKETING + "\")")
    public ResultResponse createPromotion(
            @RequestParam String code,
            @RequestParam String name,
            String description,
            String startDate,
            String endDate,
            Integer typePromotion,
            String discountPercent,
            String maxDiscount,
            String fixMoneyDiscount,
            String maxUse,
            Integer typeMaxUse,
            @RequestParam("image") MultipartFile image) throws IOException, ParseException {
        CreatePromotionRequest request = new CreatePromotionRequest();
        request.setCode(code);
        request.setName(name);
        request.setDescription(description);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        request.setStartDate(formatter.parse(startDate));
        request.setEndDate(formatter.parse(endDate));
        request.setTypePromotion(typePromotion);
        request.setDiscountPercent(discountPercent);
        request.setMaxDiscount(maxDiscount);
        request.setFixMoneyDiscount(fixMoneyDiscount);
        request.setTypeMaxUse(typeMaxUse);
        request.setMaxUse(maxUse);
        return promotionService.createPromotion(request, image);
    }

    @PostMapping("update-status")
    @PreAuthorize("hasAuthority(\"" + RoleConstants.MARKETING + "\")")
    public ResultResponse updateStatus(@RequestBody UpdateStatusPromotionRequest request) {
        return promotionService.updateStatus(request);
    }

    @PostMapping("update")
    @PreAuthorize("hasAuthority(\"" + RoleConstants.MARKETING + "\")")
    public ResultResponse update(@RequestBody UpdatePromotionRequest request) {
        return promotionService.update(request);
    }


    @GetMapping("check-price-promotion")
    public PromotionResponseTotal update(@RequestParam("promotionCode") String promotionCode,
                                         @RequestParam("totalPrice") BigDecimal totalPrice

                                    ) throws Exception {
        return promotionService.checkPromotionIsValidAndTotal( promotionCode, totalPrice);
    }

    @PostMapping("public-list")
    public ResponseListWithMetaData<SearchPromotionResponse> listPublicPromotion() throws SQLException {
        return promotionService.listPublicPromotion();
    }
}
