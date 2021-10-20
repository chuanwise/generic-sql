#### GenericSQL 
# 一款轻量级数据库操作类库

这个项目的初衷是快速实现将 `select` 结果直接注入到 `Java POJO` 里。
为了防止注入，额外编写了链式调用语句等部分规范 `SQL` 语句生成，渐渐形成了一个简单的数据库操作类库。

### 示例
以下示例均能在 `SQL Server 2019` 测试通过。

#### 查询，并将结果注入为 POJO
```sql
select *
from [choose-question]
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
        final SqlServerConnector connector = new SqlServerConnector();
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
update [choose-question]
set summary = 'set to'
where [question-code] = 10
```
对应下面的代码
```java
public class SqlServerTest {
    public static void main(String[] args) {
        final SqlServerConnector connector = new SqlServerConnector();
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
insert into [choose-question]([question-code], title, summary)
values(10, '题目标题', NULL)
```
对应下面的代码
```java
public class SqlServerTest {
    public static void main(String[] args) {
        final SqlServerConnector connector = new SqlServerConnector();
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