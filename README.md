## 对mybatis sql脚本语句的一个小增强
mybatis对列表参数的支持不够友好，得通过<foreach/>块实现，增加了开发的烦琐程度，降低源sql的可读性。
通过此工具，简单的sql IN语句可以写成
```sql
select * from tbl where column_name in (#{@list})
``` 
或者
```sql
select * from tbl where column_name in (#{@list.property})
``` 
替代原有的写法
```sql
select * from tbl where column_name in
<foreach collection='list' item='item' open='(' close=')' separator=','>#{item}</foreach>
```

### 使用方式
mybatis.xml配置添加
```xml
<settings>
    <setting name="defaultScriptingLanguage" value="nameless.common.mybatis.parser.XMLLanguageDriver"/>
</settings>
```
或者java代码配置
```java
configuration.setDefaultScriptingLanguage(new nameless.common.mybatis.parser.XMLLanguageDriver());
```