package cn.chuanwise.toolkit.sql.connector;

import cn.chuanwise.toolkit.sql.ConnectionHandler;
import cn.chuanwise.toolkit.sql.exception.NoSuchDriverException;
import lombok.Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

@Data
public abstract class Connector {
    protected final String protocolName;
    protected final String driverClassName;

    public String getConnectionUrl() {
        return "jdbc:" + protocolName + ":" + getConnectionUrl0();
    }

    protected abstract String getConnectionUrl0();

    public Optional<Class<?>> getDriverClass() {
        try {
            return Optional.of(Class.forName(driverClassName));
        } catch (ClassNotFoundException classNotFoundException) {
            return Optional.empty();
        }
    }

    public ConnectionHandler connect() throws SQLException {
        if (getDriverClass().isEmpty()) {
            throw new NoSuchDriverException(driverClassName);
        }
        return connect0(DriverManager.getConnection(getConnectionUrl()));
    }

    protected abstract ConnectionHandler connect0(Connection connection) throws SQLException;
}