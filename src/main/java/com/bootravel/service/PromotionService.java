package com.bootravel.service;

import com.bootravel.common.constant.FileAwsConstants;
import com.bootravel.common.constant.PromotionTypeConstants;
import com.bootravel.common.constant.StatusPromotionConstants;
import com.bootravel.common.constant.TypeMaxUsePromotion;
import com.bootravel.common.dto.BaseSearchPagingDTO;
import com.bootravel.common.dto.PageMetaDTO;
import com.bootravel.common.security.jwt.service.RefreshTokenService;
import com.bootravel.entity.PromotionRedemptionsEntity;
import com.bootravel.entity.PromotionsEntity;
import com.bootravel.exception.BadRequestAlertException;
import com.bootravel.payload.requests.CreatePromotionRequest;
import com.bootravel.payload.requests.SearchPromotionRequest;
import com.bootravel.payload.requests.UpdatePromotionRequest;
import com.bootravel.payload.requests.UpdateStatusPromotionRequest;
import com.bootravel.payload.responses.SearchPromotionResponse;
import com.bootravel.payload.responses.commonResponses.PromotionResponseTotal;
import com.bootravel.payload.responses.constant.ResponseType;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListWithMetaData;
import com.bootravel.payload.responses.data.ResultResponse;
import com.bootravel.repository.PromotionRepository;
import com.bootravel.repository.RoomsRepository;
import com.bootravel.service.common.AmazonS3StorageHandler;
import com.bootravel.service.common.CommonService;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class PromotionService {

    private static final String DEFAULT_SORT = "createdDate";

    private static final List<String> HEADER_SORT = Collections.singletonList("createdDate");
    private static final String ENTITY_NAME = "PromotionService";

    @Autowired
    private AmazonS3StorageHandler amazonS3StorageHandler;

    @Autowired
    private CommonService commonService;

    @Autowired
    private PromotionRepository promotionRepository;

    private final RoomsRepository roomsRepository;

    public PromotionService(RoomsRepository roomsRepository) {
        this.roomsRepository = roomsRepository;
    }


    public ResponseData<PromotionsEntity> getPromotionById(Long id) {
        ResponseData<PromotionsEntity> response = new ResponseData<>();
        boolean isExistPromotion = promotionRepository.isExistPromotion(id);
        if(!isExistPromotion) {
            response.setType(ResponseType.ERROR);
            response.setMessage("Promotion has not been existed");
            response.setCode(404);
            return response;
        }
        PromotionsEntity entity = promotionRepository.getPromotionById(id);
        response.setData(entity);
        return response;
    }

    public ResponseListWithMetaData<SearchPromotionResponse> listPromotion(SearchPromotionRequest request) throws SQLException {
        if (Objects.isNull(request)) {
            request = new SearchPromotionRequest();
        }
        ResponseListWithMetaData<SearchPromotionResponse> response = new ResponseListWithMetaData<>();
        Integer totalRecords = promotionRepository.getTotalPromotionOutput(request);

        BaseSearchPagingDTO pagingDTO = request.getSearchPaging();
        PageMetaDTO meta = commonService.settingPageMetaInfo(request.getSearchPaging(),
                StringUtils.isEmpty(pagingDTO.getSortBy()) ? HEADER_SORT : Collections.singletonList(pagingDTO.getSortBy()),
                DEFAULT_SORT, totalRecords);

        Collection<SearchPromotionResponse> listPromotion = promotionRepository.searchPromotion(request);

        response.setSuccessResponse(meta, listPromotion);
        return response;
    }

    public ResultResponse createPromotion(CreatePromotionRequest request,  MultipartFile multipartFile) throws IOException {
        ResultResponse response = new ResultResponse();
        if(request.getEndDate() != null) {
            if(request.getEndDate().before(new Date())) {
                response.setType(ResponseType.ERROR);
                response.setMessage("End date must be greater than current date");
                response.setCode(409);
                return response;
            }
        }


        boolean isExistCode = promotionRepository.isExistCodePromotion(request.getCode());
        if(isExistCode) {
            response.setType(ResponseType.ERROR);
            response.setMessage("Code has been existed");
            response.setCode(409);
            return response;
        } else {
            if (multipartFile == null || multipartFile.isEmpty()) {
                throw new BadRequestAlertException("No files uploaded", PromotionsEntity.class.toString(), "NO_FILES_UPLOADED");
            }
            List<String> allowedExtensions = Arrays.asList(FileAwsConstants.PNG, FileAwsConstants.JPEG, FileAwsConstants.JPG);
            String fileUrls = null;

            var bytes = multipartFile.getBytes();
            InputStream inputStream = new ByteArrayInputStream(bytes);
            String originalFilename = Objects.requireNonNull(multipartFile.getOriginalFilename());
            String fileExtension = org.springframework.util.StringUtils.getFilenameExtension(originalFilename);

            if (!org.springframework.util.StringUtils.hasText(fileExtension) || !allowedExtensions.contains(fileExtension.toLowerCase())) {
                throw new BadRequestAlertException("File invalid", PromotionsEntity.class.toString(), "FILE_INVALID");
            }

            String contentType = FileAwsConstants.CONTENT_TYPE_JPEG;
            if (fileExtension.equalsIgnoreCase(FileAwsConstants.PNG)) {
                contentType = FileAwsConstants.CONTENT_TYPE_PNG;
            }

            String filename = FileAwsConstants.FILE_PROMOTION + System.currentTimeMillis() + "_image." + fileExtension;
            String linkImage = amazonS3StorageHandler.storeFilePublic(inputStream, filename, contentType);
            fileUrls = linkImage;

            Instant currentInstant = Instant.now();
            Timestamp createdTimestamp = Timestamp.from(currentInstant);
            Timestamp lastModifiedTimestamp = Timestamp.from(currentInstant);

            PromotionsEntity promotionsEntity = new PromotionsEntity();
            promotionsEntity.setCode(request.getCode());
            promotionsEntity.setName(request.getName());
            promotionsEntity.setImageUrl(fileUrls);
            promotionsEntity.setDescription(request.getDescription());
            promotionsEntity.setStartDate(request.getStartDate());
            promotionsEntity.setEndDate(request.getEndDate());
            promotionsEntity.setTypePromotion(request.getTypePromotion());
            try {
                promotionsEntity.setDiscountPercent(Integer.valueOf(request.getDiscountPercent()));
            } catch (Exception e) {
                promotionsEntity.setDiscountPercent(null);
            }
            try {
                promotionsEntity.setMaxDiscount(BigDecimal.valueOf(Long.parseLong(request.getMaxDiscount())));
            } catch (Exception e) {
                promotionsEntity.setMaxDiscount(null);
            }
            try {
                promotionsEntity.setFixMoneyDiscount(BigDecimal.valueOf(Long.parseLong(request.getFixMoneyDiscount())));
            } catch (Exception e) {
                promotionsEntity.setFixMoneyDiscount(null);
            }
            try {
                promotionsEntity.setMaxUse(Integer.valueOf(request.getMaxUse()));
            } catch (Exception e) {
                promotionsEntity.setMaxUse(null);
            }
            promotionsEntity.setTypeMaxUse(request.getTypeMaxUse());
            promotionsEntity.setStatus(String.valueOf(StatusPromotionConstants.ACTIVE));
            promotionsEntity.setCreatedDate(createdTimestamp);
            promotionsEntity.setModifiedDate(lastModifiedTimestamp);

            promotionRepository.createPromotion(promotionsEntity);

            return response;
        }
    }

    public ResultResponse updateStatus(UpdateStatusPromotionRequest request) {
        ResultResponse response = new ResultResponse();

        boolean isExistPromotion = promotionRepository.isExistPromotion(request.getId());
        if(!isExistPromotion) {
            response.setType(ResponseType.ERROR);
            response.setMessage("Promotion has not been existed");
            response.setCode(404);
            return response;
        }
        Instant currentInstant = Instant.now();
        Timestamp lastModifiedTimestamp = Timestamp.from(currentInstant);
        promotionRepository.updateStatus(request, lastModifiedTimestamp);
        return response;
    }

    public ResultResponse update(UpdatePromotionRequest request) {
        ResultResponse response = new ResultResponse();

        if(request.getId() == null
            || request.getName() == null
                || request.getDescription() == null
                || request.getStartDate() == null
                || request.getEndDate() == null
                || request.getTypePromotion() == null
                || request.getTypeMaxUse() == null
        ) {
            response.setCode(500);
            response.setMessage("Fields is not null");
            return response;
        }

        if(request.getEndDate().before(new Date())) {
            response.setType(ResponseType.ERROR);
            response.setMessage("End date must be greater than current date");
            response.setCode(409);
            return response;
        }

        boolean isExistPromotion = promotionRepository.isExistPromotion(request.getId());
        if(!isExistPromotion) {
            response.setType(ResponseType.ERROR);
            response.setMessage("Promotion has not been existed");
            response.setCode(404);
            return response;
        }

        if(request.getTypePromotion() != 1 && request.getTypePromotion() != 2) {
            response.setMessage("Type is not correct");
            response.setCode(400);
            return response;
        }

        if(request.getTypeMaxUse() != 1 && request.getTypeMaxUse() != 2) {
            response.setMessage("Type is not correct");
            response.setCode(400);
            return response;
        }

        if(request.getTypePromotion() == 1) {
            if(request.getDiscountPercent() <= 0) {
                response.setCode(400);
                response.setMessage("Value must be greater than 0");
                return response;
            }
            if(request.getMaxDiscount() != null && request.getMaxDiscount().compareTo(new BigDecimal(0)) <= 0) {
                response.setCode(400);
                response.setMessage("Value must be greater than 0");
                return response;
            }
        } else {
            if(request.getFixMoneyDiscount().compareTo(new BigDecimal(0)) <= 0 ) {
                response.setCode(400);
                response.setMessage("Value must be greater than 0");
                return response;
            }
        }

        if(request.getTypeMaxUse() == 2) {
            if(request.getMaxUse() <= 0) {
                response.setCode(400);
                response.setMessage("Value must be greater than 0");
                return response;
            }
        }


        Instant currentInstant = Instant.now();
        Timestamp lastModifiedTimestamp = Timestamp.from(currentInstant);
        promotionRepository.update(request, lastModifiedTimestamp);
        return response;
    }

    public ResponseData<PromotionsEntity> getPromotionByCode(String code) {
        ResponseData<PromotionsEntity> response = new ResponseData<>();
        PromotionsEntity entity = promotionRepository.getPromotionByCode(code);
        if(entity == null){
               throw new BadRequestAlertException("Promotion you are entered not exist !",ENTITY_NAME,"Invalid");
        }
        response.setData(entity);
        return response;
    }

    public PromotionsEntity updateMaxUse(long id, int request) {
        var isExistPromotion = promotionRepository.updateMaxUsePromotion(id,request);
        if(isExistPromotion == null) {
            throw new BadRequestAlertException("Promotion you are entered not exist !",ENTITY_NAME,"Invalid");
        }
        return isExistPromotion;
    }

    public PromotionResponseTotal checkPromotionIsValidAndTotal(String promotionCode, BigDecimal totalBill) throws Exception {
        var promotions = getPromotionByCode(promotionCode);

        BigDecimal totalRedemption;

        Optional<Long> currentUserLogin = RefreshTokenService.getCurrentUserLogin();
        Integer createdBy = currentUserLogin.map(Math::toIntExact).orElse(null);
        

        if((promotions.getData().getStatus().equals((StatusPromotionConstants.ACTIVE).toString()))){
            if(promotions.getData().getTypeMaxUse().equals(TypeMaxUsePromotion.UNLIMITED) ){
                // Giảm theo phần trăm
                if(Objects.equals(promotions.getData().getTypePromotion(), PromotionTypeConstants.PERCENT_REDUCTION)){
                    // số tiền được giảm
                    totalRedemption = totalBill
                            .multiply(new BigDecimal(promotions.getData().getDiscountPercent()).divide(new BigDecimal(100)));
                    //final bill
                    var totalDiscounted = totalBill.subtract(totalRedemption);
                    // số tiền đc giảm nhỏ hơn giới hạn số tiền giảm đã quy định thì thực hiện
                    if(promotions.getData().getMaxDiscount() != null){
                        if (totalRedemption.compareTo(promotions.getData().getMaxDiscount()) < 0) {
                            PromotionResponseTotal responseTotal = new PromotionResponseTotal();
                            responseTotal.setTotalRedemption(totalRedemption);
                            responseTotal.setTotal(totalDiscounted);
                            return responseTotal;

                        }else {
                            PromotionResponseTotal responseTotal = new PromotionResponseTotal();
                            responseTotal.setTotalRedemption(promotions.getData().getMaxDiscount());
                            responseTotal.setTotal(totalBill.subtract(promotions.getData().getMaxDiscount()));
                            return responseTotal;
                        }
                    }
                    PromotionResponseTotal responseTotal = new PromotionResponseTotal();
                    responseTotal.setTotalRedemption(totalRedemption);
                    responseTotal.setTotal(totalDiscounted);
                    return responseTotal;

                }
                // Giảm theo tiền cố định
                PromotionResponseTotal responseTotal = new PromotionResponseTotal();
                responseTotal.setTotalRedemption(promotions.getData().getFixMoneyDiscount());
                responseTotal.setTotal(totalBill.subtract(promotions.getData().getFixMoneyDiscount()));
                return responseTotal;
            }
            else {
                var checkUserUsedPromotion = checkUserUsedPromotion(createdBy,promotions.getData().getId());
                if(checkUserUsedPromotion ){
                    throw new BadRequestAlertException("You have already used this promotion code",ENTITY_NAME,"Invalid");
                }
                if((promotions.getData().getMaxUse() - 1) < 0){
                    throw new BadRequestAlertException("This promotion code has been used up",ENTITY_NAME,"Invalid");
                }
                if(Objects.equals(promotions.getData().getTypePromotion(), PromotionTypeConstants.PERCENT_REDUCTION)){
                    // số tiền được giảm
                    totalRedemption = totalBill
                            .multiply(new BigDecimal(promotions.getData().getDiscountPercent()).divide(new BigDecimal(100)));
                    //final bill
                    var totalDiscounted = totalBill.subtract(totalRedemption);
                    // số tiền đc giảm nhỏ hơn giới hạn số tiền giảm đã quy định thì thực hiện
                    if (totalRedemption.compareTo(promotions.getData().getMaxDiscount()) < 0) {
                        PromotionResponseTotal responseTotal = new PromotionResponseTotal();
                        responseTotal.setTotalRedemption(totalRedemption);
                        responseTotal.setTotal(totalDiscounted);
                        return responseTotal;
                    }else {
                        PromotionResponseTotal responseTotal = new PromotionResponseTotal();
                        responseTotal.setTotalRedemption(promotions.getData().getMaxDiscount());
                        responseTotal.setTotal(totalBill.subtract(promotions.getData().getMaxDiscount()));
                        return responseTotal;
                    }
                }
                // Giảm theo tiền cố định
                PromotionResponseTotal responseTotal = new PromotionResponseTotal();
                responseTotal.setTotalRedemption(promotions.getData().getFixMoneyDiscount());
                responseTotal.setTotal(totalBill.subtract(promotions.getData().getFixMoneyDiscount()));
                return responseTotal;
            }
        }
        throw new BadRequestAlertException("Your promotion is expired  or not ready to use",ENTITY_NAME,"PROMOTION_INVALID");
    }

   //--- PromotionRedemptions---//

    public PromotionRedemptionsEntity insertPromotionRedemp(PromotionRedemptionsEntity redemptions) {
        promotionRepository.insertPromotionRedemptions(redemptions);
        return redemptions;
    }

    public boolean checkUserUsedPromotion(long userId, long promotionId) {
        return promotionRepository.checkUserUsedPromotion(userId,promotionId);
    }

    public ResponseListWithMetaData<SearchPromotionResponse> listPublicPromotion() throws SQLException {
        ResponseListWithMetaData<SearchPromotionResponse> response = new ResponseListWithMetaData<>();

        Collection<SearchPromotionResponse> listPromotion = promotionRepository.searchPublicPromotion();

        response.setData(listPromotion);
        return response;
    }

}
