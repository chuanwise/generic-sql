package cn.chuanwise.toolkit.sql.commander;

import cn.chuanwise.toolkit.sql.ConnectionHandler;
import cn.chuanwise.util.ConditionUtil;
import lombok.Data;

import java.util.Objects;

@Data
public abstract class AbstractCommander
        implements Commander {
    ConnectionHandler connectionHandler;

    @Override
    public void setConnectionHandler(ConnectionHandler connectionHandler) {
        ConditionUtil.notNull(connectionHandler, "connection handler");
        ConditionUtil.checkState(Objects.isNull(connectionHandler.getCommander()), "connection handler already set!");
        this.connectionHandler = connectionHandler;
    }
}
