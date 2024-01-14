package com.bootravel.controller;

import com.bootravel.entity.DistrictsEntity;
import com.bootravel.entity.ProvincesEntity;
import com.bootravel.entity.WardsEntity;
import com.bootravel.payload.responses.data.ResponseData;
import com.bootravel.payload.responses.data.ResponseListData;
import com.bootravel.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping("/list-province")
    public ResponseListData<ProvincesEntity> listProvince() {
        return addressService.listProvince();
    }

    @GetMapping("/list-district")
    public ResponseListData<DistrictsEntity> listDistrict(Long provinceId) {
        return addressService.listDistrict(provinceId);
    }

    @GetMapping("/list-ward")
    public ResponseListData<WardsEntity> listWard(Long districtId) {
        return addressService.listWard(districtId);
    }

    @GetMapping("/get-province/{id}")
    public ResponseData<ProvincesEntity> getProvinceById(@PathVariable("id") Long id) {
        return addressService.getProvinceById(id);
    }

    @GetMapping("/get-district/{id}")
    public ResponseData<DistrictsEntity> getDistrictById(@PathVariable("id") Long id) {
        return addressService.getDistrictById(id);
    }

    @GetMapping("/get-ward/{id}")
    public ResponseData<WardsEntity> getWardById(@PathVariable("id") Long id) {
        return addressService.getWardById(id);
    }
}
