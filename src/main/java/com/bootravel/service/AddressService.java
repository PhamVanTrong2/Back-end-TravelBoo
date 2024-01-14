package com.bootravel.service;

import com.bootravel.entity.DistrictsEntity;
import com.bootravel.entity.ProvincesEntity;
import com.bootravel.entity.WardsEntity;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListData;
import com.bootravel.repository.AddressRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    public ResponseListData<ProvincesEntity> listProvince() {
        List<ProvincesEntity> data = addressRepository.listProvince();
        ResponseListData<ProvincesEntity> responseListData = new ResponseListData<>();
        responseListData.setListData(data);
        return responseListData;
    }

    public ResponseListData<DistrictsEntity> listDistrict(Long provinceId) {
        List<DistrictsEntity> data = addressRepository.listDistrict(provinceId);
        ResponseListData<DistrictsEntity> responseListData = new ResponseListData<>();
        responseListData.setListData(data);
        return responseListData;
    }

    public ResponseListData<WardsEntity> listWard(Long districtId) {
        List<WardsEntity> data = addressRepository.listWard(districtId);
        ResponseListData<WardsEntity> responseListData = new ResponseListData<>();
        responseListData.setListData(data);
        return responseListData;
    }

    public ResponseData<ProvincesEntity> getProvinceById(Long id) {
        ProvincesEntity data = addressRepository.getProvinceById(id);
        ResponseData<ProvincesEntity> response = new ResponseData<>();
        response.setData(data);
        return response;
    }

    public ResponseData<DistrictsEntity> getDistrictById(Long id) {
        DistrictsEntity data = addressRepository.getDistrictById(id);
        ResponseData<DistrictsEntity> response = new ResponseData<>();
        response.setData(data);
        return response;
    }

    public ResponseData<WardsEntity> getWardById(Long id) {
        WardsEntity data = addressRepository.getWardById(id);
        ResponseData<WardsEntity> response = new ResponseData<>();
        response.setData(data);
        return response;
    }
}
