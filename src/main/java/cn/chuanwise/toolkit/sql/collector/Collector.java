package cn.chuanwise.toolkit.sql.collector;

import cn.chuanwise.toolkit.container.Container;
import cn.chuanwise.toolkit.sql.annotation.SqlField;
import cn.chuanwise.toolkit.sql.exception.CollectorException;
import cn.chuanwise.util.*;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;

@FunctionalInterface
public interface Collector<T> {
    T collect(ResultSet resultSet) throws SQLException;

//    static Collector<Map<String, List<Object>>> toColumnMap() {
//        return resultSet -> {
//            final ResultSetMetaData metaData = resultSet.getMetaData();
//            final int columnCount = metaData.getColumnCount();
//            final Map<String, List<Object>> result = new HashMap<>(columnCount);
//
//            while (resultSet.next()) {
//                resultSet.getObject()
//            }
//        };
//    }

    static Collector<ResultSet> toResultSet() {
        return resultSet -> resultSet;
    }

    static Collector<List<Object>> toSingleFieldList(String fieldName) {
        return resultSet -> {
            final List<Object> results = new ArrayList<>(resultSet.getRow());
            while (resultSet.next()) {
                results.add(resultSet.getObject(fieldName));
            }
            return Collections.unmodifiableList(results);
        };
    }

    static <T> Collector<List<T>> toSingleFieldList(String fieldName, Class<T> fieldClass) {
        return resultSet -> {
            final List<T> results = new ArrayList<>(resultSet.getRow());
            while (resultSet.next()) {
                results.add(resultSet.getObject(fieldName, fieldClass));
            }
            return Collections.unmodifiableList(results);
        };
    }

    static Collector<Map<String, List<Object>>> toMultipleFieldLists() {
        return resultSet -> {
            final ResultSetMetaData metaData = resultSet.getMetaData();
            final int columnCount = metaData.getColumnCount();

            final Map<String, List<Object>> results = new HashMap<>(columnCount);
            for (int i = 1; i <= columnCount; i++) {
                final String columnName = metaData.getColumnName(i);
                results.put(columnName, new ArrayList<>());
            }

            while (resultSet.next()) {
                for (String columnName : results.keySet()) {
                    results.get(columnName).add(resultSet.getObject(columnName));
                }
            }

            return Collections.unmodifiableMap(results);
        };
    }

    static <T> Collector<List<T>> toList(Callable<T> constructor, Set<ObjectCollectorFeature> features) {
        return resultSet -> {
            final Map<String, List<Object>> fieldLists = toMultipleFieldLists().collect(resultSet);
            if (fieldLists.isEmpty()) {
                return Collections.emptyList();
            }

            final int rowCount = fieldLists.entrySet().iterator().next().getValue().size();
            final List<T> results = new ArrayList<>();
            for (int i = 0; i < rowCount; i++) {
                final T element;
                try {
                    element = constructor.call();
                } catch (Exception exception) {
                    throw new CollectorException(exception);
                }
                ConditionUtil.notNull(element, "return value of constructor");
                final Class<?> objectClass = element.getClass();

                for (Map.Entry<String, List<Object>> entry : fieldLists.entrySet()) {
                    final String selectedFieldName = entry.getKey();
                    final List<Object> selectedFieldValues = entry.getValue();

                    Object value = selectedFieldValues.get(i);
                    // check if trim string
                    if (value instanceof String
                            && features.contains(ObjectCollectorFeature.TRIM_STRING)) {
                        value = ((String) value).trim();
                    }
                    final Object selectedFieldValue = value;

                    // find field by @SqlField annotation or name of field
                    final Container<Field> fieldContainer = ArrayUtil.findFirst(ReflectUtil.getExistedFields(objectClass),
                            field -> Optional.ofNullable(field.getAnnotation(SqlField.class))
                                    .map(SqlField::value)
                                    .map(LambdaUtil.equalsFunction(selectedFieldName))
                                    .orElse(false)
                                    || Objects.equals(field.getName(), selectedFieldName));

                    if (fieldContainer.isPresent()) {
                        final Field field = fieldContainer.get();
                        ReflectUtil.setFieldValue(field, element, selectedFieldValue);
                    } else if (features.contains(ObjectCollectorFeature.ERROR_ON_NO_SUCH_FIELD)) {
                        throw new CollectorException("no such field: " + selectedFieldName);
                    }
                }
                results.add(element);
            }
            return results;
        };
    }

    static <T> Collector<List<T>> toList(Callable<T> constructor) {
        return toList(constructor, CollectionUtil.asSet(
                ObjectCollectorFeature.ERROR_ON_NO_SUCH_FIELD,
                ObjectCollectorFeature.TRIM_STRING
        ));
    }

    static <T> Collector<List<T>> toList(Class<T> clazz) {
        return toList(() -> {
            final T value;
            try {
                value = ReflectUtil.construct(clazz)
                        .orElseThrow(() -> new NoSuchElementException("can not call the default constructor"));
            } catch (Exception exception) {
                throw new CollectorException(exception);
            }
            return value;
        });
    }
}