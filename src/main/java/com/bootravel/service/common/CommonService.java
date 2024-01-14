package com.bootravel.service.common;

import com.bootravel.common.constant.MasterDataConstants;
import com.bootravel.common.dto.BaseSearchPagingDTO;
import com.bootravel.common.dto.PageMetaDTO;
import com.bootravel.utils.PagingUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CommonService {

    @Autowired
    private DozerBeanMapper dozerBeanMapper;

    public String generateRandomPassword() {
        Random random = new Random();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int randomIndex = random.nextInt(MasterDataConstants.CHARACTERS.length());
            password.append(MasterDataConstants.CHARACTERS.charAt(randomIndex));
        }

        return password.toString();
    }

    public PageMetaDTO settingPageMetaInfo(BaseSearchPagingDTO searchCondition, List<String> sortColumns,
                                           String sortColumn, Integer totalRecord) {

        // chekc pageSize
        if (Objects.isNull(searchCondition.getPageSize()) || Objects.equals(searchCondition.getPageSize(), 0)) {
            searchCondition.setPageSize(MasterDataConstants.DEFAULT_PAGE_SIZE);
        }

        // check setting limit
        int displayTotalMax = MasterDataConstants.SETTING_DISPLAY_MAX; // get common display max

        // check total with display max
        if (totalRecord < displayTotalMax) {
            displayTotalMax = totalRecord;
        }

        // setting paging info
        int maxPageNum = PagingUtils.settingSearchInfo(searchCondition, sortColumns, sortColumn, displayTotalMax);


        // create meta
        PageMetaDTO meta = dozerBeanMapper.map(searchCondition, PageMetaDTO.class);
        meta.setMaxPageNum(maxPageNum);
        meta.setTotal(displayTotalMax);
        meta.setExceeding(totalRecord > displayTotalMax);

        // check offset > total max => reset to display max
        int offsetDiff = searchCondition.getOffset() + searchCondition.getPageSize();
        if (displayTotalMax > 0 && offsetDiff > displayTotalMax) {
            searchCondition.setPageSize(searchCondition.getPageSize() + displayTotalMax - offsetDiff);
        }

        return meta;
    }

    public boolean isValidEmail(String email) {
        // Define a regular expression pattern for email validation
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        // Define a regular expression pattern for phone number validation
        // This pattern allows digits and optional hyphens and spaces
        String regex = "^[0-9-\\s]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }


}
