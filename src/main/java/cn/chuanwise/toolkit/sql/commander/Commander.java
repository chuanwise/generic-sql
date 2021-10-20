package cn.chuanwise.toolkit.sql.commander;

import cn.chuanwise.annotation.NotRecommended;
import cn.chuanwise.toolkit.sql.ConnectionHandler;
import cn.chuanwise.toolkit.sql.statement.InsertStatement;
import cn.chuanwise.toolkit.sql.statement.SelectStatement;
import cn.chuanwise.toolkit.sql.statement.UpdateStatement;

public interface Commander {
    @NotRecommended
    void setConnectionHandler(ConnectionHandler connectionHandler);

    ConnectionHandler getConnectionHandler();

    SelectStatement select();

    InsertStatement insert();

    UpdateStatement update();

    Formatter formatter();
}
