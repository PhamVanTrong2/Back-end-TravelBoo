package com.bootravel.repository;

import com.bootravel.common.CommonRepository;
import com.bootravel.entity.BannerEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
@Slf4j
public class BannerRepository extends CommonRepository {
    public BannerRepository() throws ParserConfigurationException, IOException, SAXException {
        super();
    }
    @Override
    public String getFileKey() {
        return "/sql/sqlBannerRepository.xml";
    }
    public BannerEntity insertBanner(BannerEntity banner) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("INSERT_BANNER");

        try {
            List<Object> params = new ArrayList<>();
            params.add(banner.getImages());
            params.add(banner.getStatus());
            params.add(banner.getTypes());
            params.add(banner.getHotelId());
            params.add(banner.getCreatedBy());
            params.add(banner.getCreatedDate());
            params.add(banner.getLastModifyDate());

            ps = preparedStatement(sql, params);

            int rowsInserted = ps.executeUpdate();

            if (rowsInserted > 0) {
                // Banner was successfully inserted, retrieve the generated banner ID
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    banner.setId(rs.getLong(1));
                }

                return banner;
            }
        } catch (SQLException e) {
            log.error("Error inserting banner: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null; // Return null if the insertion was not successful
    }

    public BannerEntity updateBanner(Long id, boolean status) {
        PreparedStatement ps = null;

        // Define your SQL update query
        String sql = sqlLoader.getSql("UPDATE_BANNER");

        try {
            Instant currentInstant = Instant.now();
            java.sql.Timestamp lastModifiedTimestamp = java.sql.Timestamp.from(currentInstant);
            List<Object> params = new ArrayList<>();
            // Set the parameters to update the banner record
            params.add(status);
            params.add(lastModifiedTimestamp);
            params.add(id); // Use the ID to identify the record to update

            ps = preparedStatement(sql, params);

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                // Create a BannerEntity with the updated values and return it
                BannerEntity updatedBanner = new BannerEntity();
                updatedBanner.setId(id);
                updatedBanner.setStatus(status);
                updatedBanner.setLastModifyDate(Timestamp.from(currentInstant));
                return updatedBanner;
            }
        } catch (SQLException e) {
            log.error("Error updating banner: " + e.getMessage(), e);
        } finally {
            closePS(ps);
        }

        return null; // Return null if the update was not successful
    }




    public BannerEntity findById(long bannerId) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("FIND_BANNER_BY_ID"); // Define the SQL query in your SQL file

        try {
            List<Object> params = new ArrayList<>();
            params.add(bannerId);

            ps = preparedStatement(sql, params);

            rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSetToBannerEntity(rs);
            }
        } catch (SQLException e) {
            log.error("Error finding banner by ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null; // Return null if the banner with the provided ID was not found
    }

    // You'll also need a method to map the result set to a BannerEntity
    private BannerEntity mapResultSetToBannerEntity(ResultSet rs) throws SQLException {
        BannerEntity banner = new BannerEntity();
        banner.setId(rs.getLong("ID"));
        banner.setImages(rs.getString("IMAGES"));
        banner.setStatus(rs.getBoolean("STATUS"));
        banner.setTypes(rs.getString("TYPES"));
        banner.setHotelId((int) rs.getLong("HOTEL_ID"));
        banner.setCreatedBy((int) rs.getLong("CREATED_BY"));
        banner.setCreatedDate(Timestamp.from(rs.getTimestamp("CREATED_DATE").toInstant()));
        banner.setLastModifyDate(Timestamp.from(rs.getTimestamp("LAST_MODIFY_DATE").toInstant()));
        return banner;
    }

    public List<BannerEntity> findAllBanners() {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("SELECT_BANNERS"); // Use the SQL query defined in your XML

        try {
            ps = preparedStatement(sql, new ArrayList<>());

            rs = ps.executeQuery();

            List<BannerEntity> banners = new ArrayList<>();
            while (rs.next()) {
                banners.add(mapResultSetToBannerEntity(rs));
            }

            return banners;
        } catch (SQLException e) {
            log.error("Error fetching banners: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return Collections.emptyList(); // Return an empty list if no banners are found
    }


}
