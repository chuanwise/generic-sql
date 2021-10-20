package cn.chuanwise.toolkit.sql.commander;

import cn.chuanwise.toolkit.sql.statement.InsertStatement;
import cn.chuanwise.toolkit.sql.statement.SelectStatement;
import cn.chuanwise.util.ArrayUtil;
import cn.chuanwise.util.ConditionUtil;
import cn.chuanwise.util.StringUtil;

import java.sql.*;
import java.util.Map;

public class SqlServerCommander
        extends AbstractCommander {
    @Override
    public String toTableName(String tableName) {
        return toIdentifier(tableName);
    }

    @Override
    public String toColumnName(String columnName) {
        return toIdentifier(columnName);
    }

    protected String toIdentifier(String identifier) {
        final boolean containsDoubleQuote = identifier.contains("\"");
        final boolean containsBracket = StringUtil.containsAny(identifier, "[]");

        ConditionUtil.checkArgument(!containsBracket && !containsDoubleQuote,
                "table name can not contains '\"', '[' and ']' at the same time!");

        if (containsBracket) {
            return '\"' + identifier + '\"';
        } else {
            return '[' + identifier + ']';
        }
    }

    @Override
    public SelectStatement select() {
        return new SelectStatement(this) {
            @Override
            public PreparedStatement prepare0() throws SQLException {
                ConditionUtil.notNull(from, "target table");
                ConditionUtil.checkArgument(StringUtil.notEmpty(format), "format is empty!");

                final StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("select " + format + " from " + toTableName(from) + " ");

                if (StringUtil.notEmpty(where)) {
                    stringBuffer.append("where " + where);
                }

                if (StringUtil.notEmpty(orderBy)) {
                    if (desc) {
                        stringBuffer.append("order by " + orderBy + " desc ");
                    } else {
                        stringBuffer.append("order by " + orderBy + " ");
                    }
                }

                final Connection connection = connectionHandler.getConnection();
                return connection.prepareStatement(stringBuffer.toString());
            }
        };
    }

    @Override
    public InsertStatement insert() {
        return new InsertStatement(this) {
            @Override
            public PreparedStatement prepare0() throws SQLException {
                ConditionUtil.checkArgument(StringUtil.notEmpty(tableName), "table name not set!");
                ConditionUtil.checkArgument(!values.isEmpty() || !fieldValues.isEmpty(), "insert value not set!");

                final PreparedStatement statement;
                final StringBuffer stringBuffer = new StringBuffer("insert into " + toTableName(tableName));
                final boolean isValueMode = !values.isEmpty();
                if (isValueMode) {
                    stringBuffer.append(" values(" + StringUtil.repeat("?", values.size(), ", ") + ")");

                    statement = connectionHandler.getConnection().prepareStatement(stringBuffer.toString());
                    for (int i = 0; i < values.size(); i++) {
                        statement.setObject(i + 1, values.get(i));
                    }
                } else {
                    final Map.Entry<String, Object>[] entries = fieldValues.entrySet().toArray(new Map.Entry[0]);

                    stringBuffer.append("(" + ArrayUtil.toString(entries, entry -> toColumnName(entry.getKey()), ", ") + ") " +
                            "values(" + StringUtil.repeat("?", fieldValues.size(), ", ") + ")");

                    statement = connectionHandler.getConnection().prepareStatement(stringBuffer.toString());
                    for (int i = 0; i < entries.length; i++) {
                        statement.setObject(i + 1, entries[i].getValue());
                    }
                }
                return statement;
            }
        };
    }
}