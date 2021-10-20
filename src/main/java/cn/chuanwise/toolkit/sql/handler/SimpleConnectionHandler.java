package cn.chuanwise.toolkit.sql.handler;

import cn.chuanwise.toolkit.sql.ConnectionHandler;
import cn.chuanwise.toolkit.sql.commander.Commander;
import cn.chuanwise.util.ConditionUtil;
import lombok.Getter;

import java.sql.Connection;
import java.util.Objects;

@Getter
public class SimpleConnectionHandler
        implements ConnectionHandler {
    final Connection connection;
    final Commander commander;

    public SimpleConnectionHandler(Connection connection, Commander commander) {
        ConditionUtil.notNull(connection, "sql connection");
        ConditionUtil.notNull(commander, "sql commander");
        ConditionUtil.checkArgument(Objects.isNull(commander.getConnectionHandler()), "connection handler already set!");

        // initialize connection handler
        commander.setConnectionHandler(this);

        this.connection = connection;
        this.commander = commander;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}