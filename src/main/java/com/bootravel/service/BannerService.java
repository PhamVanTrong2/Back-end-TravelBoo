package com.bootravel.service;

import com.bootravel.common.constant.FileAwsConstants;
import com.bootravel.common.security.jwt.service.RefreshTokenService;
import com.bootravel.entity.BannerEntity;
import com.bootravel.exception.BadRequestAlertException;
import com.bootravel.payload.requests.CreateBannerRequests;
import com.bootravel.repository.BannerRepository;
import com.bootravel.service.common.AmazonS3StorageHandler;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.mail.MessagingException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;

import org.springframework.util.StringUtils;


@Service
@Slf4j
public class BannerService {

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private AmazonS3StorageHandler amazonS3StorageHandler;

    public BannerEntity createBanner(CreateBannerRequests banner, MultipartFile multipartFile) throws IOException, MessagingException {
        var bannerRequest = new BannerEntity();

        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new BadRequestAlertException("No files uploaded", BannerEntity.class.toString(), "NO_FILES_UPLOADED");
        }

        List<String> allowedExtensions = Arrays.asList(FileAwsConstants.PNG, FileAwsConstants.JPEG, FileAwsConstants.JPG);
        String fileUrls = null;

        var bytes = multipartFile.getBytes();
        InputStream inputStream = new ByteArrayInputStream(bytes);
        String originalFilename = Objects.requireNonNull(multipartFile.getOriginalFilename());
        String fileExtension = StringUtils.getFilenameExtension(originalFilename);

        if (!StringUtils.hasText(fileExtension) || !allowedExtensions.contains(fileExtension.toLowerCase())) {
            throw new BadRequestAlertException("File invalid", BannerEntity.class.toString(), "FILE_INVALID");
        }

        String contentType = FileAwsConstants.CONTENT_TYPE_JPEG;
        if (fileExtension.equalsIgnoreCase(FileAwsConstants.PNG)) {
            contentType = FileAwsConstants.CONTENT_TYPE_PNG;
        }

        String filename = FileAwsConstants.FILE_BANNER + System.currentTimeMillis() + "_image." + fileExtension;
        String linkImage = amazonS3StorageHandler.storeFilePublic(inputStream, filename, contentType);
        fileUrls = linkImage;

        Optional<Long> currentUserLogin = RefreshTokenService.getCurrentUserLogin();
        Long createdBy = Long.valueOf(currentUserLogin.map(Math::toIntExact).orElse(1));

        bannerRequest.setCreatedBy(Math.toIntExact(createdBy));
        bannerRequest.setStatus(true);
        bannerRequest.setImages(fileUrls);

        Instant currentInstant = Instant.now();
        java.sql.Timestamp createdTimestamp = java.sql.Timestamp.from(currentInstant);
        java.sql.Timestamp lastModifiedTimestamp = java.sql.Timestamp.from(currentInstant);

        bannerRequest.setCreatedDate(createdTimestamp);
        bannerRequest.setLastModifyDate(lastModifiedTimestamp);

        bannerRequest.setHotelId(banner.getHotelId());
        bannerRequest.setTypes(banner.getTypes());


        var createBanner = bannerRepository.insertBanner(bannerRequest); // Assuming you have an update method in your repository
        return createBanner;
    }
    public BannerEntity updateBannerStatus(Long id, boolean status) {
        return bannerRepository.updateBanner(id, status);
    }
    public List<BannerEntity> listBanners() {
        return bannerRepository.findAllBanners();
    }
}
