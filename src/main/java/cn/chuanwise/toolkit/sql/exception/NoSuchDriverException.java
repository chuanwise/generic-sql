package cn.chuanwise.toolkit.sql.exception;

import lombok.Data;

@Data
public class NoSuchDriverException extends WiseSQLException {
    final String driverClassName;

    public NoSuchDriverException(String driverClassName) {
        super(driverClassName);
        this.driverClassName = driverClassName;
    }
}