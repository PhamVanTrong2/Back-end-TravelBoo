package com.bootravel.repository;

import com.bootravel.common.CommonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;


@Slf4j
@Repository
public class ManagementMarketingRepository extends CommonRepository {
    public ManagementMarketingRepository() throws ParserConfigurationException, IOException, SAXException {
    }
    @Override
    protected String getFileKey() {
        return "/sql/sqlCommon.xml";
    }

}
