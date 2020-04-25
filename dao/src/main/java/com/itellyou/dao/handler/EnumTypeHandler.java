package com.itellyou.dao.handler;

import com.itellyou.util.BaseEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnumTypeHandler<T extends Enum<T> & BaseEnum> extends BaseTypeHandler<T> {
    private final Class<T> enumClass;

    public EnumTypeHandler(Class<T> enumClass) {
        if (enumClass == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.enumClass = enumClass;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter.getValue());
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object value = rs.getObject(columnName);
        return rs.wasNull() ? null : locateEnumStatus(value);
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object value = rs.getObject(columnIndex);
        return rs.wasNull() ? null : locateEnumStatus(value);
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object value = cs.getObject(columnIndex);
        return cs.wasNull() ? null : locateEnumStatus(value);
    }

    private T locateEnumStatus(Object value) {
        T[] enumConstants = enumClass.getEnumConstants();
        if (enumConstants == null)
            throw new IllegalArgumentException(enumClass.getSimpleName() + " does not represent an enum type.");
        for(T e : enumConstants) {
            if(e.getValue().equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown enum type：" + value + ",Please check it " + enumClass.getSimpleName());
    }
}
