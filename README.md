# GenericSQL  轻量级数据库操作类库

这个项目的初衷是快速实现将 `select` 结果直接注入到 `Java POJO` 里。
为了防止注入，额外编写了链式调用语句等部分规范 `SQL` 语句生成，渐渐形成了一个简单的数据库操作类库。

具体的数据库类型（如 `SQL Server` `MySQL`）只和数据库连接器 `Connector` 相关。
例如，下面的代码将产生对于 `SQL Server` 可用的语句。
```java
final Connector connector = new SqlServerConnector();
// ...

try (ConnectionHandler connection = connector.connect()) {
    // do something
} catch (Exception exception) {
    exception.printStackTrace();
}
```
如果需要更换数据库类型，只需要修改 `Connector` 即可。

### 示例
以下示例均能在 `SQL Server 2019` 测试通过。

#### 查询，并将结果注入为 POJO
```sql
select * from [choose-question]
```
对应下面的代码
```java
public class SqlServerTest {
    public static class ChooseQuestion {
        @SqlField("question-code")
        long questionCode;
        String title;
        String summary;
    }

    public static void main(String[] args) {
        final Connector connector = new SqlServerConnector();
        // ...

        try (ConnectionHandler connection = connector.connect()) {
            final Commander commander = connection.getCommander();
            final List<ChooseQuestion> collect = commander.select()
                    .format("*")
                    .table("choose-question")
                    .collect(Collector.toList(ChooseQuestion.class));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
```

### 更新表中的值
```sql
update [choose-question] set summary = 'set to' where [question-code] = 10
```
对应下面的代码
```java
public class SqlServerTest {
    public static void main(String[] args) {
        final Connector connector = new SqlServerConnector();
        // ...

        try (ConnectionHandler connection = connector.connect()) {
            final Commander commander = connection.getCommander();
            commander.update()
                    .table("choose-question")
                    .set("summary", "set to")
                    .where("question-code = 10")
                    .execute();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
```

### 插入新值
```sql
insert into [choose-question]([question-code], title, summary) values(10, '题目标题', NULL)
```
对应下面的代码
```java
public class SqlServerTest {
    public static void main(String[] args) {
        final Connector connector = new SqlServerConnector();
        // ...

        try (ConnectionHandler connection = connector.connect()) {
            final Commander commander = connection.getCommander();
            commander.insert()
                    .into("choose-question")
                    .fieldValue("question-code", 10)
                    .fieldValue("title", "题目标题")
                    .fieldValue("summary", null)
                    .execute();        
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
```