package com.bootravel.repository;

import com.bootravel.common.CommonRepository;
import com.bootravel.entity.HotelServicesEntity;
import com.bootravel.entity.RoomServicesEntity;
import com.bootravel.payload.responses.GetListRoomServiceResponse;
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
public class ServiceRepository extends CommonRepository {

    public ServiceRepository() throws ParserConfigurationException, IOException, SAXException {
        super();
    }

    @Override
    protected String getFileKey() {
        return "/sql/sqlService.xml";
    }

    public List<HotelServicesEntity> getListServiceHotel() {
        List<HotelServicesEntity> hotelServices = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("SELECT_LIST_SERVICE_HOTEL");

        try {
            ps = preparedStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                HotelServicesEntity service = new HotelServicesEntity();
                service.setId(rs.getLong("ID"));
                service.setName(rs.getString("HOTEL_SERVICE_NAME"));

                hotelServices.add(service);
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return hotelServices;
    }

    public List<GetListRoomServiceResponse> getListServiceRoom() {
        List<GetListRoomServiceResponse> response = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("SELECT_LIST_SERVICE_ROOM_TYPE");
        try {
            ps = preparedStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                GetListRoomServiceResponse service = new GetListRoomServiceResponse();
                service.setRoomServiceTypeId(rs.getLong("ID"));
                service.setRoomServiceTypeName(rs.getString("ROOM_SERVICE_TYPE_NAME"));
                List<RoomServicesEntity> roomServices = getRoomServiceByTypeId(service.getRoomServiceTypeId());
                service.setListRoomService(roomServices);

                response.add(service);
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return response;
    }

    public List<RoomServicesEntity> getRoomServiceByTypeId(Long typeId) {
        List<RoomServicesEntity> roomServices = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("SELECT_LIST_SERVICE_ROOM_BY_TYPE_ID");
        List<Object> params = new ArrayList<>();
        params.add(typeId);
        try {
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            while (rs.next()) {
                RoomServicesEntity entity = new RoomServicesEntity();
                entity.setId(rs.getLong("ID"));
                entity.setName(rs.getString("ROOM_SERVICE_NAME"));

                roomServices.add(entity);
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
            return null;
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return roomServices;
    }
}
