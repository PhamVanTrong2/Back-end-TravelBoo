package com.bootravel.common.database.holder;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Component
@RequestScope
@Getter
public class ConnectionHolder {

    private Connection connection;


    public Connection getConnection(DataSource dataSource) throws SQLException {
        if (connection == null) {
            connection = dataSource.getConnection();
        }
        return connection;
    }

    public void releaseConnection() {
        releaseConnection(this.connection);
    }

    private void releaseConnection(Connection con) {

        try {
            if (con != null && !con.isClosed()) {
                log.info("Release connection: " + con);
                con.commit();
                con.close();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            log.error(e.getMessage(), e);
        }
    }

    public void rollbackConnection() {
        rollbackConnection(connection);
    }

    private void rollbackConnection(Connection con) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.rollback();
            }
        } catch (SQLException e) {
            log.info(e.getMessage(), e);
        }
    }
}
