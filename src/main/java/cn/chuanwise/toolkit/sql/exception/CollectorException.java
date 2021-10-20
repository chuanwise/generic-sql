package cn.chuanwise.toolkit.sql.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CollectorException extends WiseSQLException {
    public CollectorException(Throwable cause) {
        super(cause);
    }

    public CollectorException(String message, Throwable cause) {
        super(message, cause);
    }

    public CollectorException(String message) {
        super(message);
    }
}
