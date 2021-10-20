package cn.chuanwise.toolkit.sql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlField {
    String value();
}
