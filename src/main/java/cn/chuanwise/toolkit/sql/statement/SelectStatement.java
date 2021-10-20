package cn.chuanwise.toolkit.sql.statement;

import cn.chuanwise.toolkit.sql.commander.Commander;

public abstract class SelectStatement extends ResultStatement {
    protected String format;
    protected String from;
    protected String where;

    protected String orderBy;
    protected boolean desc;

    public SelectStatement(Commander commander) {
        super(commander);
    }

    public SelectStatement from(String tableName) {
        checkNotPrepared();
        this.from = tableName;
        return this;
    }

    public SelectStatement where(String where) {
        checkNotPrepared();
        this.where = where;
        return this;
    }

    public SelectStatement format(String format) {
        checkNotPrepared();
        this.format = format;
        return this;
    }

    public SelectStatement orderBy(String orderBy, boolean desc) {
        checkNotPrepared();
        this.orderBy = orderBy;
        this.desc = desc;
        return this;
    }

    public SelectStatement orderBy(String orderBy) {
        checkNotPrepared();
        this.orderBy = orderBy;
        return this;
    }
}