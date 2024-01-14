package com.bootravel.repository;

import com.bootravel.common.CommonRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;


@Repository
@Slf4j
public class BusinessAdminRepository extends CommonRepository {

    public BusinessAdminRepository() throws ParserConfigurationException, IOException, SAXException {
        super();
    }

    @Override
    protected String getFileKey() {
        return "/sql/sqlBusinessAdminRepository.xml";
    }
}

