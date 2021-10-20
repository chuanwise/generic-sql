package cn.chuanwise.toolkit.sql.statement;

import cn.chuanwise.toolkit.sql.commander.Commander;
import cn.chuanwise.util.ConditionUtil;
import cn.chuanwise.util.StringUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SelectStatement extends ResultStatement {
    protected String format;
    protected Map<String, TableName> tableNames = new HashMap<>();
    protected String where;

    protected String orderBy;
    protected boolean desc;

    protected List<JoinSubStatement> joinSubStatements = new ArrayList<>();

    @Data
    protected class TableName {
        final String tableName;
        final String alias;
    }

    public SelectStatement(Commander commander) {
        super(commander);
    }

    public SelectStatement table(String tableName, String alias) {
        checkNotPrepared();
        ConditionUtil.checkArgument(StringUtil.notEmpty(tableName), "table name is empty!");
        ConditionUtil.checkArgument(StringUtil.notEmpty(alias), "table alias is empty!");
        ConditionUtil.checkArgument(!tableNames.containsKey(tableName), "table already added!");

        tableNames.put(tableName, new TableName(tableName, alias));
        return this;
    }

    public SelectStatement table(String tableName) {
        checkNotPrepared();
        ConditionUtil.checkArgument(StringUtil.notEmpty(tableName), "table name is empty!");
        ConditionUtil.checkArgument(!tableNames.containsKey(tableName), "table already added!");

        tableNames.put(tableName, new TableName(tableName, null));
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

    @Data
    public class JoinSubStatement {
        protected String joinType;
        protected TableName tableName;
        protected String filter;

        public SelectStatement on(String filter) {
            ConditionUtil.checkArgument(StringUtil.notEmpty(filter), "filter is empty!");
            checkNotPrepared();

            this.filter = filter;
            return SelectStatement.this;
        }
    }

    public JoinSubStatement join(String tableName) {
        return join0("join", tableName, null);
    }

    public JoinSubStatement join(String tableName, String alias) {
        return join0("join", tableName, alias);
    }

    public JoinSubStatement innerJoin(String tableName) {
        return join0("inner join", tableName, null);
    }

    public JoinSubStatement outerJoin(String tableName) {
        return join0("outer join", tableName, null);
    }

    public JoinSubStatement leftOuterJoin(String tableName) {
        return join0("left outer join", tableName, null);
    }

    public JoinSubStatement rightOuterJoin(String tableName) {
        return join0("right outer join", tableName, null);
    }

    public JoinSubStatement innerJoin(String tableName, String alias) {
        return join0("inner join", tableName, alias);
    }

    public JoinSubStatement outerJoin(String tableName, String alias) {
        return join0("outer join", tableName, alias);
    }

    public JoinSubStatement leftOuterJoin(String tableName, String alias) {
        return join0("left outer join", tableName, alias);
    }

    public JoinSubStatement rightOuterJoin(String tableName, String alias) {
        return join0("right outer join", tableName, alias);
    }

    protected JoinSubStatement join0(String joinType, String tableName, String alias) {
        ConditionUtil.checkArgument(StringUtil.notEmpty(tableName), "table name is empty!");
        checkNotPrepared();

        final JoinSubStatement statement = new JoinSubStatement();
        statement.tableName = new TableName(tableName, alias);
        statement.joinType = joinType;
        joinSubStatements.add(statement);
        return statement;
    }
}