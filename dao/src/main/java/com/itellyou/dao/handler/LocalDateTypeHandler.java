package com.itellyou.dao.handler;

import com.itellyou.util.DateUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class LocalDateTypeHandler extends BaseTypeHandler<LocalDate> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, LocalDate parameter, JdbcType jdbcType) throws SQLException {
        ps.setLong(i, DateUtils.getTimestamp(parameter));
    }

    @Override
    public LocalDate getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object object = rs.getObject(columnName);
        Long timestamp = rs.wasNull() || object == null ? null : (Long)object;
        return timestamp == null ? null : DateUtils.toLocalDate(timestamp);
    }

    @Override
    public LocalDate getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object object = rs.getObject(columnIndex);
        Long timestamp = rs.wasNull() || object == null ? null : (Long)object;
        return timestamp == null ? null : DateUtils.toLocalDate(timestamp);
    }

    @Override
    public LocalDate getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object object = cs.getObject(columnIndex);
        Long timestamp = cs.wasNull() || object == null ? null : (Long)object;
        return timestamp == null ? null : DateUtils.toLocalDate(timestamp);
    }
}
