package com.bootravel.payload.responses.commonResponses;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromotionResponseTotal  {
    private BigDecimal total;
    private BigDecimal totalRedemption;


}
