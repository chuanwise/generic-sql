package cn.chuanwise.toolkit.sql.object;

import cn.chuanwise.toolkit.sql.ConnectionHandler;
import cn.chuanwise.toolkit.sql.commander.Commander;
import cn.chuanwise.util.ConditionUtil;
import lombok.Data;

@Data
public class ConnectionObject {
    protected final ConnectionHandler connectionHandler;
    protected final Commander commander;

    public ConnectionObject(ConnectionHandler connectionHandler) {
        ConditionUtil.notNull(connectionHandler, "connection handler");
        this.connectionHandler = connectionHandler;

        ConditionUtil.notNull(connectionHandler.getCommander(), "commander of connection handler hasn't set!");
        this.commander = connectionHandler.getCommander();
    }

    public ConnectionObject(Commander commander) {
        ConditionUtil.notNull(commander, "commander");
        this.commander = commander;

        ConditionUtil.notNull(commander.getConnectionHandler(), "connection handler hasn't set!");
        this.connectionHandler = commander.getConnectionHandler();
    }
}
