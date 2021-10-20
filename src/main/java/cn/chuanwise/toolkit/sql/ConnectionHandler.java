package cn.chuanwise.toolkit.sql;

import cn.chuanwise.toolkit.sql.commander.Commander;

import java.sql.Connection;

public interface ConnectionHandler
        extends AutoCloseable {
    Connection getConnection();

    Commander getCommander();
}
