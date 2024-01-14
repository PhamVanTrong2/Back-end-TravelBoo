package com.bootravel.repository;

import com.bootravel.common.CommonRepository;
import com.bootravel.entity.UsersEntity;
import com.bootravel.payload.requests.commonRequests.RegisterRequest;
import com.bootravel.payload.responses.SeachBoByManagerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
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
public class UserRepository extends CommonRepository {
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserRepository() throws ParserConfigurationException, IOException, SAXException {
        super();
    }

    @Override
    protected String getFileKey() {
        return "/sql/sqlUserRepository.xml";
    }

    public UsersEntity findByUsername(String username) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("SELECT_USER_BY_NAME");
        List<Object> params = new ArrayList<>();
        params.add(username);
        try {
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            if (rs.next()) {
                UsersEntity user = new UsersEntity();
                user.setId(rs.getLong("ID"));
                user.setUsername(rs.getString("USER_NAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setPassword(rs.getString("USER_PASSWORD"));
                user.setFirstName(rs.getString("FIRST_NAME"));
                user.setLastName(rs.getString("LAST_NAME"));
                user.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                user.setBirthDate(rs.getDate("BIRTH_DATE"));
                user.setGender(rs.getString("GENDER"));
                user.setAddressId((long) rs.getInt("ADDRESS_ID"));
                user.setAvatar(rs.getString("AVATAR"));
                user.setStatus(rs.getBoolean("STATUS"));
                user.setRoleId((long) rs.getInt("ROLE_ID"));
                return user;
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

    public UsersEntity findById(long userId) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("SELECT_USER_BY_ID");
        List<Object> params = new ArrayList<>();
        params.add(userId);
        try {
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            if (rs.next()) {
                UsersEntity user = new UsersEntity();
                user.setId(rs.getLong("ID"));
                user.setPassword(rs.getString("USER_PASSWORD"));
                user.setRoleId(rs.getLong("ROLE_ID"));
                user.setUsername(rs.getString("USER_NAME"));
                return user;
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

    public String checkInfoAlreadyExist(String email, String phoneNumber, String userName) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("CHECK_INFO_ALREADY_EXIST");
        List<Object> params = new ArrayList<>();
        params.add(email);
        params.add(phoneNumber);
        params.add(userName);
        params.add(email);
        params.add(phoneNumber);
        params.add(userName);
        try {
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("INFO_EXIST");
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

    public UsersEntity updateRegisteredUsersStatus(long userId, boolean newStatus) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("UPDATE_STATUS_REGISTERED_USER");

        try {
            // Create a list to hold the query parameters
            List<Object> params = new ArrayList<>();
            params.add(newStatus); // For the first placeholder (STATUS)
            params.add(userId);     // For the second placeholder (ID)

            // Create the prepared statement with parameters
            ps = preparedStatement(sql, params);

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                // The user status was successfully updated
                UsersEntity updatedUser = new UsersEntity(); // Create a UsersEntity and set relevant fields
                updatedUser.setId(userId); // Set the user ID
                updatedUser.setStatus(newStatus); // Set the new status
                return updatedUser;
            }
        } catch (SQLException e) {
            log.error("Error updating user status: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null; // Return null if the update was not successful
    }

    public UsersEntity updateRegisteredUsers(RegisterRequest updatedUser) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("UPDATE_REGISTERED_USER");

        try {
            // Create a list to hold the query parameters
            List<Object> params = new ArrayList<>();
            params.add(updatedUser.getUsername());            // For the first placeholder (USER_NAME)
            params.add(updatedUser.getEmail());               // For the second placeholder (EMAIL)
            params.add(updatedUser.getFirstName());           // For the third placeholder (FIRST_NAME)
            params.add(updatedUser.getLastName());            // For the fourth placeholder (LAST_NAME)
            params.add(updatedUser.getPhoneNumber());         // For the fifth placeholder (PHONE_NUMBER)
            params.add(new java.sql.Date(updatedUser.getBirthDate().getTime())); // For the sixth placeholder (BIRTH_DATE)
            params.add(updatedUser.getGender());              // For the seventh placeholder (GENDER)
            params.add(updatedUser.getAddressId());            // For the eighth placeholder (ADDRESS_ID)
            params.add(updatedUser.getAvatar());              // For the ninth placeholder (AVATAR)
            params.add(updatedUser.getStatus());              // For the tenth placeholder (STATUS)
            params.add(updatedUser.getId());                  // For the eleventh placeholder (ID)

            // Create the prepared statement with parameters
            ps = preparedStatement(sql, params);

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                // The user was successfully updated
                return mapRegisterRequestToUsersEntity(updatedUser);
            }
        } catch (SQLException e) {
            log.error("Error updating user: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null; // Return null if the update was not successful
    }

    public UsersEntity getUsersById(Long userId) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("CHECK_REGISTERED_USER");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        try {
            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();

            if (rs.next()) {
                UsersEntity user = new UsersEntity();
                user.setId(rs.getLong("ID"));
                user.setUsername(rs.getString("USER_NAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setPassword(rs.getString("USER_PASSWORD"));
                user.setFirstName(rs.getString("FIRST_NAME"));
                user.setLastName(rs.getString("LAST_NAME"));
                user.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                user.setBirthDate(rs.getDate("BIRTH_DATE"));
                user.setGender(rs.getString("GENDER"));
                user.setAddressId((long) rs.getInt("ADDRESS_ID"));
                user.setAvatar(rs.getString("AVATAR"));
                user.setStatus(rs.getBoolean("STATUS"));
                user.setRoleId((long) rs.getInt("ROLE_ID"));

                return user;
            } else {
                // No user found with the given ID
                return null;
            }
        } catch (SQLException e) {
            log.error("Error fetching user by ID: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null; // Return null if there is an error or the user is not found
    }


    public UsersEntity insertUser(UsersEntity newUser) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("INSERT_REGISTERED_USER");

        try {
            // Create a list to hold the query parameters
            List<Object> params = new ArrayList<>();
            params.add(newUser.getUsername());
            params.add(newUser.getEmail());
            params.add(passwordEncoder.encode(newUser.getPassword()));
            params.add(newUser.getFirstName());
            params.add(newUser.getLastName());
            params.add(newUser.getPhoneNumber());
            params.add(null);
            params.add(newUser.getGender());
            params.add(newUser.getAddressId());
            params.add(newUser.getAvatar());
            params.add(newUser.getStatus());
            params.add(newUser.getRoleId());
            params.add(newUser.getVerificationCode());

            ps = preparedStatement(sql, params);

            int rowsInserted = ps.executeUpdate();

            if (rowsInserted > 0) {
                // User was successfully inserted, retrieve the generated user ID
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    newUser.setId(rs.getLong(1));
                }

                return newUser;
            }
        } catch (SQLException e) {
            log.error("Error inserting user: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null; // Return null if the insertion was not successful
    }

    public UsersEntity findUserByRoleIdAndId(Long userId) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("SELECT_DETAIL_REGISTERED_USER");

        try {
            // Create a list to hold the query parameters
            List<Object> params = new ArrayList<>();
            params.add(userId);  // For the first placeholder (ID)

            // Create the prepared statement with parameters
            ps = preparedStatement(sql, params);

            rs = ps.executeQuery();

            if (rs.next()) {
                UsersEntity user = new UsersEntity();
                user.setId(rs.getLong("ID"));
                user.setUsername(rs.getString("USER_NAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setPassword(rs.getString("USER_PASSWORD"));
                user.setFirstName(rs.getString("FIRST_NAME"));
                user.setLastName(rs.getString("LAST_NAME"));
                user.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                user.setBirthDate(rs.getDate("BIRTH_DATE"));
                user.setGender(rs.getString("GENDER"));
                user.setAvatar(rs.getString("AVATAR"));
                user.setStatus(rs.getBoolean("STATUS"));
                user.setRoleId(rs.getLong("ROLE_ID"));

                return user;
            }
        } catch (SQLException e) {
            log.error("Error finding user: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null; // Return null if the user is not found or an error occurred
    }
    public List<UsersEntity> findUserByRoleId(Long roleId) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("SELECT_USER_BY_ROLE_ID");

        List<UsersEntity> users = new ArrayList<>();

        try {
            // Create a list to hold the query parameters
            List<Object> params = new ArrayList<>();
            params.add(roleId);  // For the first placeholder (ID)

            // Create the prepared statement with parameters
            ps = preparedStatement(sql, params);

            rs = ps.executeQuery();

            while (rs.next()) {
                UsersEntity user = new UsersEntity();
                user.setId(rs.getLong("ID"));
                user.setUsername(rs.getString("USER_NAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setPassword(rs.getString("USER_PASSWORD"));
                user.setFirstName(rs.getString("FIRST_NAME"));
                user.setLastName(rs.getString("LAST_NAME"));
                user.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                user.setBirthDate(rs.getDate("BIRTH_DATE"));
                user.setGender(rs.getString("GENDER"));
                user.setAvatar(rs.getString("AVATAR"));
                user.setStatus(rs.getBoolean("STATUS"));
                user.setRoleId(rs.getLong("ROLE_ID"));

                users.add(user);
            }

            return users;
        } catch (SQLException e) {
            log.error("Error finding users by role: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return new ArrayList<>(); // Return an empty list if no users are found or an error occurred
    }

    public UsersEntity findByVerificationCode(String verificationCode) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String sql = sqlLoader.getSql("FIND_USER_VERIFICATION_CODE");

        try {
            List<Object> params = new ArrayList<>();
            params.add(verificationCode);

            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();

            if (rs.next()) {
                UsersEntity user = new UsersEntity();
                user.setId(rs.getLong("ID"));
                user.setUsername(rs.getString("USER_NAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setPassword(rs.getString("USER_PASSWORD"));
                user.setFirstName(rs.getString("FIRST_NAME"));
                user.setLastName(rs.getString("LAST_NAME"));
                user.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                user.setBirthDate(rs.getDate("BIRTH_DATE"));
                user.setGender(rs.getString("GENDER"));
                user.setAddressId((long) rs.getInt("ADDRESS_ID"));
                user.setAvatar(rs.getString("AVATAR"));
                user.setStatus(rs.getBoolean("STATUS"));
                user.setRoleId(rs.getLong("ROLE_ID"));
                user.setVerificationCode(rs.getString("VERIFICATION_CODE"));
                return user;
            }
        } catch (SQLException e) {
            log.error("Error finding user by verification code: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null; // Return null if the user is not found or an error occurred
    }

    public UsersEntity updateVerificationCode(String verificationCode, String newVerificationCode, boolean newStatus) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = sqlLoader.getSql("UPDATE_STATUS_USER_AND_VERIFICATION_CODE");

        try {
            List<Object> params = new ArrayList<>();
            params.add(newVerificationCode);
            params.add(newStatus);
            params.add(verificationCode);

            ps = preparedStatement(sql, params);
            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                // Update was successful, return the updated user entity
                UsersEntity updatedUser = findByVerificationCode(newVerificationCode);
                return updatedUser;
            }
        } catch (SQLException e) {
            log.error("Error updating verification code and status: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return null; // Return null if the update was not successful
    }

    public UsersEntity mapRegisterRequestToUsersEntity(RegisterRequest registerRequest) {
        UsersEntity userEntity = new UsersEntity();
        userEntity.setId(registerRequest.getId());
        userEntity.setUsername(registerRequest.getUsername());
        userEntity.setEmail(registerRequest.getEmail());
        userEntity.setFirstName(registerRequest.getFirstName());
        userEntity.setLastName(registerRequest.getLastName());
        userEntity.setPhoneNumber(registerRequest.getPhoneNumber());
        userEntity.setBirthDate(registerRequest.getBirthDate());
        userEntity.setGender(registerRequest.getGender());
        userEntity.setAddressId(registerRequest.getAddressId());
        userEntity.setAvatar(registerRequest.getAvatar());
        userEntity.setStatus(registerRequest.getStatus());
        return userEntity;
    }

    public void updatePassword(UsersEntity users) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        // Execute the query to update the password
        String sql = sqlLoader.getSql("UPDATE_USER_PASSWORD");

        try {
            List<Object> params = new ArrayList<>();
            params.add(passwordEncoder.encode(users.getPassword()));
            params.add(users.getEmail());
            ps = preparedStatement(sql, params);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error updating updatePassword : " + e.getMessage(), e);
        } finally {
            // Close resources
            closeResultSet(rs);
            closePS(ps);
        }
    }

    public List<UsersEntity> getAllUser(){
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("SELECT_USER_BY_ROLE_ID");

        List<UsersEntity> users = new ArrayList<>();

        try {
            // Create the prepared statement with parameters
            ps = preparedStatement(sql);

            rs = ps.executeQuery();

            while (rs.next()) {
                UsersEntity user = new UsersEntity();
                user.setId(rs.getLong("ID"));
                user.setUsername(rs.getString("USER_NAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setPassword(rs.getString("USER_PASSWORD"));
                user.setFirstName(rs.getString("FIRST_NAME"));
                user.setLastName(rs.getString("LAST_NAME"));
                user.setPhoneNumber(rs.getString("PHONE_NUMBER"));
                user.setBirthDate(rs.getDate("BIRTH_DATE"));
                user.setGender(rs.getString("GENDER"));
                user.setAvatar(rs.getString("AVATAR"));
                user.setStatus(rs.getBoolean("STATUS"));
                user.setRoleId(rs.getLong("ROLE_ID"));

                users.add(user);
            }

            return users;
        } catch (SQLException e) {
            log.error("Error finding users by role: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return new ArrayList<>();
    }

    public List<SeachBoByManagerResponse> searchBoByManager(Long managerId) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("SEARCH_BO_BY_MANAGER");

        List<SeachBoByManagerResponse> users = new ArrayList<>();

        try {

            ps = preparedStatement(sql);
            int idx =0;
            ps.setLong(++idx,managerId);
            rs = ps.executeQuery();

            while (rs.next()) {
                SeachBoByManagerResponse response = new SeachBoByManagerResponse();
                response.setId(rs.getLong("ID"));
                response.setFullName(rs.getString("FULL_NAME"));
                users.add(response);
            }

        } catch (SQLException e) {
            log.error("Error finding users by role: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return users;
    }

    public void changePassword(Long id, String password) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        // Execute the query to update the password
        String sql = sqlLoader.getSql("CHANGE_PASSWORD");
        try {
            List<Object> params = new ArrayList<>();

            params.add(password);
            params.add(id);

            ps = preparedStatement(sql, params);

            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error updating updatePassword : " + e.getMessage(), e);
        } finally {
            // Close resources
            closeResultSet(rs);
            closePS(ps);
        }

    }

    public boolean checkExistUserByEmail(String email) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = sqlLoader.getSql("CHECK_EXIST_EMAIL");

        try {

            ps = preparedStatement(sql);
            int idx = 0;
            ps.setString(++idx, email);
            rs = ps.executeQuery();

            if (rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            log.error("Error finding users by role: " + e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }

        return false;
    }

    public void updateProfile(UsersEntity usersEntity) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = sqlLoader.getSql("UPDATE_PROFILE");
        try {
            List<Object> params = new ArrayList<>();

            params.add(usersEntity.getFirstName());
            params.add(usersEntity.getLastName());
            params.add(usersEntity.getGender());
            params.add(usersEntity.getBirthDate());
            params.add(usersEntity.getPhoneNumber());
            params.add(usersEntity.getAddressId());
            params.add(usersEntity.getAvatar());
            params.add(usersEntity.getId());
            ps = preparedStatement(sql, params);

            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Error updating : " + e.getMessage(), e);
        } finally {
            // Close resources
            closeResultSet(rs);
            closePS(ps);
        }
    }

    public int totalUserSystem() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int totalRecords = 0;
        try {
            String sql = sqlLoader.getSql("COUNT_TOTAL_USER_SYSTEM");
            ps = preparedStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                totalRecords = rs.getInt(1);
            }
            return totalRecords;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
    }

    public int totalStaffBusinessAdmin(Long businessAdminId) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int totalRecords = 0;
        try {
            String sql = sqlLoader.getSql("COUNT_TOTAL_STAFF_BUSINESS_ADMIN");

            List<Object> params = new ArrayList<>();
            params.add(businessAdminId);
            params.add(businessAdminId);

            ps = preparedStatement(sql, params);
            rs = ps.executeQuery();

            while (rs.next()) {
                totalRecords = rs.getInt(1);
            }
            return totalRecords;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePS(ps);
        }
    }
}
