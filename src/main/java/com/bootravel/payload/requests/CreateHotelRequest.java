package com.bootravel.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateHotelRequest {
    private String name;
    private Integer star;
    private String description;
    private String note;
    private Long businessOwner;
    private String taxCode;
    private List<Long> listHotelService;
    private Long wardId;
    private String address1;
    private String address2;
    private String phoneNumber;
    private boolean includesBreakfast;
    private boolean confirmNow;
}
