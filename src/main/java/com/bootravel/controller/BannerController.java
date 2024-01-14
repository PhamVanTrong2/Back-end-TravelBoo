package com.bootravel.controller;

import com.bootravel.entity.BannerEntity;
import com.bootravel.payload.requests.CreateBannerRequests;
import com.bootravel.service.BannerService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/banner")
@Setter
public class BannerController {
    @Autowired
    private BannerService bannerService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BannerEntity> createBanner(@RequestParam("types") String types,
                                                     @RequestParam(value = "hotelId", required = false) Integer hotelId,
                                                     @RequestPart("fileImage") MultipartFile fileImage) throws URISyntaxException, MessagingException, IOException {
        CreateBannerRequests createBannerRequest = new CreateBannerRequests();

        createBannerRequest.setTypes(types);
        createBannerRequest.setHotelId(hotelId);
        var result = bannerService.createBanner(createBannerRequest, fileImage);
        return ResponseEntity
                .created(new URI("/banner/create-banner/" + result.getId()))
                .body(result);
    }


    @PutMapping("/update-status/{id}")
    public ResponseEntity<?> updateBannerStatus(
            @PathVariable Long id,
            @RequestParam("status") boolean status) {
        BannerEntity updatedBanner = bannerService.updateBannerStatus(id, status);

        if (updatedBanner != null) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/list")
//    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority(1)")
    public ResponseEntity<List<BannerEntity>> getAllBanner() {
        List<BannerEntity> banners = bannerService.listBanners();

        if (banners != null && !banners.isEmpty()) {
            return ResponseEntity.ok(banners);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

}
