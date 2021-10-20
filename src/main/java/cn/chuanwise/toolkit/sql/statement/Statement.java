package cn.chuanwise.toolkit.sql.statement;

import cn.chuanwise.toolkit.sql.commander.Commander;
import cn.chuanwise.toolkit.sql.object.ConnectionObject;
import cn.chuanwise.util.ConditionUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public abstract class Statement
        extends ConnectionObject {
    protected PreparedStatement preparedStatement;

    public Statement(Commander commander) {
        super(commander);
    }

    public boolean isPrepared() {
        return Objects.nonNull(preparedStatement);
    }

    public synchronized PreparedStatement prepare() throws SQLException {
        if (!isPrepared()) {
            preparedStatement = prepare0();
        }
        return preparedStatement;
    }

    protected void checkNotPrepared() {
        ConditionUtil.isNull(preparedStatement, "statement already prepared!");
    }

    public abstract PreparedStatement prepare0() throws SQLException;
}
