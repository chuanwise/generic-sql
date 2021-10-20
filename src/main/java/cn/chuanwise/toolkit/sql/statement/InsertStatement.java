package cn.chuanwise.toolkit.sql.statement;

import cn.chuanwise.toolkit.sql.commander.Commander;
import cn.chuanwise.util.ConditionUtil;
import lombok.Data;
import lombok.Getter;

import java.util.*;

/**
 * insert into [tableName] values(value1, value2... valuen)
 * insert into [tableName](column1, column2... columnm) values(value1, value2... valuem)
 */
public abstract class InsertStatement extends NoResultStatement {
    protected String tableName;

    protected Map<String, Object> fieldValues =  new HashMap<>();
    protected List<Object> values = new ArrayList<>();

    public InsertStatement(Commander commander) {
        super(commander);
    }

    public Map<String, Object> getFieldValues() {
        return Collections.unmodifiableMap(fieldValues);
    }

    public List<Object> getValues() {
        return Collections.unmodifiableList(values);
    }

    public InsertStatement into(String tableName) {
        checkNotPrepared();

        this.tableName = tableName;
        return this;
    }

    public InsertStatement fieldValue(String fieldName, Object value) {
        checkValuesIsEmpty();

        ConditionUtil.checkArgument(!fieldValues.containsKey(fieldName), "field: " + fieldName + " already set!");
        fieldValues.put(fieldName, value);
        return this;
    }

    public InsertStatement value(Object value) {
        checkFieldValueIsEmpty();

        values.add(value);
        return this;
    }

    protected void checkFieldValueIsEmpty() {
        ConditionUtil.checkArgument(fieldValues.isEmpty(), "already choose field-value mode!");
    }

    protected void checkValuesIsEmpty() {
        ConditionUtil.checkArgument(values.isEmpty(), "already choose value mode!");
    }
}
