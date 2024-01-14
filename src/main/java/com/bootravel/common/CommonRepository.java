package com.bootravel.common;

import com.bootravel.common.database.SqlLoader;
import com.bootravel.common.database.holder.ConnectionHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.xml.sax.SAXException;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

public abstract class CommonRepository {

    protected static final Log log = LogFactory.getLog(CommonRepository.class);

    @Autowired
    private ConnectionHolder connectionHolder;

    protected SqlLoader sqlLoader;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    protected abstract String getFileKey();

    public CommonRepository() throws ParserConfigurationException, IOException, SAXException {
        sqlLoader = new SqlLoader(getFileKey());
    }

    public Connection getConnection() throws SQLException {
        return connectionHolder.getConnection(dataSource);
    }

    protected PreparedStatement preparedStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    protected PreparedStatement preparedStatement(String sql, Collection<Object> params) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement(sql);
        int idx = 0;
        for (Object param : params) {
            if (param instanceof Date) {
                java.sql.Date newDate = new java.sql.Date(((Date) param).getTime());
                ps.setDate(++idx, newDate);
            } else {
                ps.setObject(++idx, param);
            }
        }
        return ps;
    }

    protected void closeResultSet(ResultSet rs) {
        if (Objects.nonNull(rs)) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException("Exception when close ResultSet!");
            }
        }
    }

    protected void closePS(PreparedStatement ps) {
        if (Objects.nonNull(ps)) {
            try {
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException("Exception when close PreparedStatement!");
            }
        }
    }
}
