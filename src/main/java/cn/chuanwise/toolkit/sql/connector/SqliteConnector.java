package cn.chuanwise.toolkit.sql.connector;

import cn.chuanwise.toolkit.sql.ConnectionHandler;
import cn.chuanwise.toolkit.sql.handler.SimpleConnectionHandler;
import cn.chuanwise.util.ConditionUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

@Getter
@Setter
public class SqliteConnector
        extends Connector {
    public static final String PROTOCOL_NAME = "sqlite";
    public static final String DRIVER_CLASS_NAME = "org.sqlite.JDBC";

    File file;

    public SqliteConnector() {
        super(PROTOCOL_NAME, DRIVER_CLASS_NAME);
    }

    @Override
    protected String getConnectionUrl0() {
        ConditionUtil.notNull(file, "database file");

        return file.getAbsolutePath();
    }

    @Override
    protected ConnectionHandler connect0(Connection connection) throws SQLException {
//        return new SimpleConnectionHandler(connection, new SqliteCommander());
        return null;
    }
}
