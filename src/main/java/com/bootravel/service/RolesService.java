package com.bootravel.service;

import com.bootravel.entity.RolesEntity;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.repository.CustomCommonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@Service
@Slf4j
@Transactional
public class RolesService {

    @Autowired
    private CustomCommonRepository customCommonRepository;

    public RolesEntity getRoleByID(Long Id) throws SQLException {
        RolesEntity data = customCommonRepository.getRoleById(Id);
        ResponseData<RolesEntity> responseData = new ResponseData<>();
        responseData.setData(data);
        return responseData.getData();
    }
}
