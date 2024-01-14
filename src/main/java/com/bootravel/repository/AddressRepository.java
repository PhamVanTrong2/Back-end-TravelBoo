package com.bootravel.repository;

import com.bootravel.common.CommonRepository;
import com.bootravel.entity.DistrictsEntity;
import com.bootravel.entity.ProvincesEntity;
import com.bootravel.entity.WardsEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class AddressRepository extends CommonRepository {
    public AddressRepository() throws ParserConfigurationException, IOException, SAXException {
        super();
    }

    @Override
    protected String getFileKey() {
        return "/sql/sqlAddressRepository.xml";
    }
    public List<ProvincesEntity> listProvince() {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<ProvincesEntity> listProvinces = new ArrayList<>();
        String sql = sqlLoader.getSql("SELECT_PROVINCE");
        try {
            ps = preparedStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                ProvincesEntity provincesEntity = new ProvincesEntity();
                provincesEntity.setId(rs.getLong("ID"));
                provincesEntity.setName(rs.getString(("PROVINCE_NAME")));
                provincesEntity.setRegionId(rs.getInt("REGION_ID"));

                listProvinces.add(provincesEntity);
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return listProvinces;
    }

    public List<DistrictsEntity> listDistrict(Long provinceId) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<DistrictsEntity> listDistrict = new ArrayList<>();
        String sql = sqlLoader.getSql("SELECT_DISTRICT");
        List<Object> params = new ArrayList<>();
        params.add(provinceId);
        try {
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            while (rs.next()) {
                DistrictsEntity districtsEntity = new DistrictsEntity();

                districtsEntity.setId(rs.getLong("ID"));
                districtsEntity.setName(rs.getString("DISTRICT_NAME"));
                districtsEntity.setProvinceId(rs.getInt("PROVINCE_ID"));

                listDistrict.add(districtsEntity);
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return listDistrict;
    }

    public List<WardsEntity> listWard(Long districtId) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<WardsEntity> listWard = new ArrayList<>();
        String sql = sqlLoader.getSql("SELECT_WARD");
        List<Object> params = new ArrayList<>();
        params.add(districtId);
        try {
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            while (rs.next()) {
                WardsEntity wardsEntity = new WardsEntity();

                wardsEntity.setId(rs.getLong("ID"));
                wardsEntity.setName(rs.getString("WARD_NAME"));
                wardsEntity.setDistrictId(rs.getInt("DISTRICT_ID"));

                listWard.add(wardsEntity);
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return listWard;
    }

    public ProvincesEntity getProvinceById(Long id) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("SELECT_PROVINCE_BY_ID");
        List<Object> params = new ArrayList<>();
        params.add(id);
        try {
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            if (rs.next()) {
                ProvincesEntity provincesEntity = new ProvincesEntity();
                provincesEntity.setId(rs.getLong("ID"));
                provincesEntity.setName(rs.getString(("PROVINCE_NAME")));
                provincesEntity.setRegionId(rs.getInt("REGION_ID"));

                return provincesEntity;
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null;
    }

    public DistrictsEntity getDistrictById(Long id) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("SELECT_DISTRICT_BY_ID");
        List<Object> params = new ArrayList<>();
        params.add(id);
        try {
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            if (rs.next()) {
                DistrictsEntity districtsEntity = new DistrictsEntity();

                districtsEntity.setId(rs.getLong("ID"));
                districtsEntity.setName(rs.getString("DISTRICT_NAME"));
                districtsEntity.setProvinceId(rs.getInt("PROVINCE_ID"));

                return districtsEntity;
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null;
    }

    public WardsEntity getWardById(Long id) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("SELECT_WARD_BY_ID");
        List<Object> params = new ArrayList<>();
        params.add(id);
        try {
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            if (rs.next()) {
                WardsEntity wardsEntity = new WardsEntity();

                wardsEntity.setId(rs.getLong("ID"));
                wardsEntity.setName(rs.getString("WARD_NAME"));
                wardsEntity.setDistrictId(rs.getInt("DISTRICT_ID"));

                return wardsEntity;
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null;
    }
}
