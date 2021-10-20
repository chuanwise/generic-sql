package cn.chuanwise.toolkit.sql.result;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ColumnFormat {
    final String originalName;
    final String alias;

    public static ColumnFormat of(String name) {
        return new ColumnFormat(name, null);
    }

    public static ColumnFormat of(String name, String alias) {
        return new ColumnFormat(name, alias);
    }
}