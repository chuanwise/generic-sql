package cn.chuanwise.toolkit.sql;

import cn.chuanwise.toolkit.sql.commander.Commander;
import cn.chuanwise.toolkit.sql.handler.SimpleConnectionHandler;

import java.sql.Connection;

public interface ConnectionHandler
        extends AutoCloseable {
    Connection getConnection();

    Commander getCommander();

    static ConnectionHandler of(Connection connection, Commander commander) {
        return new SimpleConnectionHandler(connection, commander);
    }
}
