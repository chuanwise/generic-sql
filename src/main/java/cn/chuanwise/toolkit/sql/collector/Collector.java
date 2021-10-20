package cn.chuanwise.toolkit.sql.collector;

import cn.chuanwise.toolkit.container.Container;
import cn.chuanwise.toolkit.sql.annotation.SqlField;
import cn.chuanwise.toolkit.sql.exception.CollectorException;
import cn.chuanwise.util.*;
import lombok.Getter;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;

@Getter
public abstract class Collector<T> {
    /** 收集器特性 */
    protected final Set<CollectorFeature> features = CollectionUtil.asSet(
            CollectorFeature.ERROR_ON_NO_SUCH_FIELD,
            CollectorFeature.TRIM_STRING
    );

    public Set<CollectorFeature> getFeatures() {
        return Collections.unmodifiableSet(features);
    }

    public Collector<T> addFeature(CollectorFeature feature) {
        features.add(feature);
        return this;
    }

    public Collector<T> addFeatures(CollectorFeature... features) {
        this.features.addAll(Arrays.asList(features));
        return this;
    }

    public Collector<T> addFeatures(Collection<CollectorFeature> features) {
        this.features.addAll(features);
        return this;
    }

    public Collector<T> removeFeature(CollectorFeature feature) {
        features.remove(feature);
        return this;
    }

    public Collector<T> removeFeatures(CollectorFeature... features) {
        this.features.removeAll(Arrays.asList(features));
        return this;
    }

    public Collector<T> removeFeatures(Collection<CollectorFeature> features) {
        this.features.removeAll(features);
        return this;
    }

    /** 根据给出的收集器特性获取结果集中的内容 */
    public abstract T collect(ResultSet resultSet) throws SQLException;

    /** 原样返回数据库的结果集 */
    public static Collector<ResultSet> toResultSet() {
        return new Collector<ResultSet>() {
            @Override
            public ResultSet collect(ResultSet resultSet) throws SQLException {
                return resultSet;
            }
        };
    }

    /**
     * 截取查询结果中的一列
     * @param columnName 列名
     * @return 该列列名的
     */
    public static Collector<List<Object>> toFieldList(String columnName) {
        return toFieldList(columnName, Object.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> Collector<List<T>> toFieldList(String fieldName, Class<T> fieldClass) {
        return new Collector<>() {
            @Override
            public List<T> collect(ResultSet resultSet) throws SQLException {
                final List<T> results = new ArrayList<>(resultSet.getRow());
                while (resultSet.next()) {
                    // trim string
                    T object = resultSet.getObject(fieldName, fieldClass);
                    if (object instanceof String && features.contains(CollectorFeature.TRIM_STRING)) {
                        object = (T) ((String) object).trim();
                    }

                    results.add(object);
                }
                return Collections.unmodifiableList(results);
            }
        };
    }

    /**
     * 获取查询结果中的所有列
     * @return 列名为键值的哈希表
     */
    public static Collector<Map<String, List<Object>>> toFieldListMap() {
        return new Collector<>() {
            @Override
            public Map<String, List<Object>> collect(ResultSet resultSet) throws SQLException {
                final ResultSetMetaData metaData = resultSet.getMetaData();
                final int columnCount = metaData.getColumnCount();

                final Map<String, List<Object>> results = new HashMap<>(columnCount);
                for (int i = 1; i <= columnCount; i++) {
                    final String columnName = metaData.getColumnName(i);
                    results.put(columnName, new ArrayList<>());
                }

                while (resultSet.next()) {
                    for (String columnName : results.keySet()) {
                        // trim string
                        Object object = resultSet.getObject(columnName);
                        if (object instanceof String && features.contains(CollectorFeature.TRIM_STRING)) {
                            object = ((String) object).trim();
                        }

                        results.get(columnName).add(object);
                    }
                }

                return Collections.unmodifiableMap(results);
            }
        };
    }

    public static Collector<List<List<Object>>> toFieldLists() {
        return new Collector<>() {
            @Override
            public List<List<Object>> collect(ResultSet resultSet) throws SQLException {
                final ResultSetMetaData metaData = resultSet.getMetaData();
                final int columnCount = metaData.getColumnCount();

                final List<List<Object>> results = new ArrayList<>(columnCount);
                for (int i = 0; i < columnCount; i++) {
                    results.add(new ArrayList<>());
                }

                while (resultSet.next()) {
                    for (int i = 0; i < results.size(); i++) {
                        // trim string
                        Object object = resultSet.getObject(i + 1);
                        if (object instanceof String && features.contains(CollectorFeature.TRIM_STRING)) {
                            object = ((String) object).trim();
                        }

                        results.get(i).add(object);
                    }
                }

                return Collections.unmodifiableList(results);
            }
        };
    }

    public static <T> Collector<List<T>> toList(Callable<T> constructor) {
        return new Collector<>() {
            @Override
            public List<T> collect(ResultSet resultSet) throws SQLException {
                final Map<String, List<Object>> fieldLists = toFieldListMap().collect(resultSet);
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
                                && features.contains(CollectorFeature.TRIM_STRING)) {
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
                        } else if (features.contains(CollectorFeature.ERROR_ON_NO_SUCH_FIELD)) {
                            throw new CollectorException("no such field: " + selectedFieldName);
                        }
                    }
                    results.add(element);
                }
                return results;
            }
        };
    }

    public static <T> Collector<List<T>> toList(Class<T> clazz) {
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