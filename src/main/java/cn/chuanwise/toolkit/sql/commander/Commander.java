package cn.chuanwise.toolkit.sql.commander;

import cn.chuanwise.annotation.NotRecommended;
import cn.chuanwise.toolkit.sql.ConnectionHandler;
import cn.chuanwise.toolkit.sql.statement.InsertStatement;
import cn.chuanwise.toolkit.sql.statement.SelectStatement;

public interface Commander {
    @NotRecommended
    void setConnectionHandler(ConnectionHandler connectionHandler);

    ConnectionHandler getConnectionHandler();

    SelectStatement select();

    InsertStatement insert();

    String toTableName(String tableName);

    String toColumnName(String tableName);
}
