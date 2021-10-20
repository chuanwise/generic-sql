package cn.chuanwise.toolkit.sql.exception;

import lombok.NoArgsConstructor;

import java.sql.SQLException;

@NoArgsConstructor
public class WiseSQLException extends SQLException {
    public WiseSQLException(Throwable cause) {
        super(cause);
    }

    public WiseSQLException(String message, Throwable cause) {
        super(message, cause);
    }

    public WiseSQLException(String message) {
        super(message);
    }
}