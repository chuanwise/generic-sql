package cn.chuanwise.toolkit.sql.statement;

import cn.chuanwise.toolkit.sql.collector.Collector;
import cn.chuanwise.toolkit.sql.commander.Commander;

import java.sql.SQLException;

public abstract class ResultStatement extends Statement {
    public ResultStatement(Commander commander) {
        super(commander);
    }

    public <T> T collect(Collector<T> collector) throws SQLException {
        return collector.collect(prepare().executeQuery());
    }
}