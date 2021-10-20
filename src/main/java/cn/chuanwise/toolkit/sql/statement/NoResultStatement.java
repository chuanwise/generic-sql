package cn.chuanwise.toolkit.sql.statement;

import cn.chuanwise.toolkit.sql.commander.Commander;

import java.sql.SQLException;

public abstract class NoResultStatement extends Statement {
    public NoResultStatement(Commander commander) {
        super(commander);
    }

    public void execute() throws SQLException {
        prepare().executeUpdate();
    }
}
