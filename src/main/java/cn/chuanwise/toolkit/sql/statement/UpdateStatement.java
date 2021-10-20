package cn.chuanwise.toolkit.sql.statement;

import cn.chuanwise.toolkit.sql.commander.Commander;
import cn.chuanwise.util.ConditionUtil;
import cn.chuanwise.util.StringUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class UpdateStatement extends NoResultStatement {
    protected String tableName;
    protected String where;
    protected Map<String, Object> fieldValues = new HashMap<>();

    public UpdateStatement(Commander commander) {
        super(commander);
    }

    public UpdateStatement table(String tableName) {
        ConditionUtil.checkState(StringUtil.notEmpty(tableName), "table name is empty!");
        ConditionUtil.checkState(Objects.isNull(this.tableName), "table name already set!");
        checkNotPrepared();

        this.tableName = tableName;
        return this;
    }

    public UpdateStatement set(String columnName, Object value) {
        ConditionUtil.checkState(!fieldValues.containsKey(columnName), "column: " + columnName + " already set!");
        checkNotPrepared();

        fieldValues.put(columnName, value);
        return this;
    }

    public UpdateStatement where(String where) {
        ConditionUtil.checkArgument(StringUtil.notEmpty(where), "filter is empty!");
        ConditionUtil.checkArgument(Objects.isNull(this.where), "filter already set!");
        checkNotPrepared();

        this.where = where;
        return this;
    }
}
