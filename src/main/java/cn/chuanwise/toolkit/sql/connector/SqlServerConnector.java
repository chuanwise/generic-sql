package cn.chuanwise.toolkit.sql.connector;

import cn.chuanwise.toolkit.sql.ConnectionHandler;
import cn.chuanwise.toolkit.sql.commander.SqlServerCommander;
import cn.chuanwise.toolkit.sql.handler.SimpleConnectionHandler;
import cn.chuanwise.util.ConditionUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Getter
@Setter
public class SqlServerConnector
        extends Connector {
    public static final String PROTOCOL_NAME = "sqlserver";
    public static final String DRIVER_CLASS_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    String address;
    int port;

    Properties properties = new Properties();

    public SqlServerConnector() {
        super(PROTOCOL_NAME, DRIVER_CLASS_NAME);
    }

    @Override
    protected String getConnectionUrl0() {
        ConditionUtil.notNull(address, "sql server address");

        final StringBuffer arguments = new StringBuffer();
        properties.forEach((key, value) -> arguments.append(key + "=" + value + "; "));

        /*
        "jdbc:sqlserver://yourserver.database.windows.net:1433;"
                        + "database=AdventureWorks;"
                        + "user=yourusername@yourserver;"
                        + "password=yourpassword;"
                        + "encrypt=true;"
                        + "trustServerCertificate=false;"
                        + "loginTimeout=30;";
        */
        return "//" + address + ":" + port + "; "
                + arguments;
    }

    @Override
    protected ConnectionHandler connect0(Connection connection) throws SQLException {
        return new SimpleConnectionHandler(connection, new SqlServerCommander());
    }
}