package nameless.common.mybatis.test;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class MainTest {
    final int SIZE = 4;
    private SqlSessionFactory factory;
    private SqlSession sqlSession;

    public void prepare() throws Exception {
        try (InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml")) {
            factory = new SqlSessionFactoryBuilder().build(inputStream);
            sqlSession = factory.openSession();
        }

        Connection connection = sqlSession.getConnection();
        Statement statement = connection.createStatement();
        statement.execute(FooMapper.TABLE_SCHEMA);

        FooMapper fooMapper = sqlSession.getMapper(FooMapper.class);
        for (int i = 1; i <= SIZE; i++) {
            Foo foo = new Foo();
            foo.setId(i);
            foo.setValue("value-" + i);
            int inserted = fooMapper.insert(foo);
            Assert.assertEquals(inserted, 1);
        }
    }

    @Test
    public void test() throws Exception {
        prepare();
        FooMapper fooMapper = sqlSession.getMapper(FooMapper.class);
        for (int i = 1; i <= SIZE; i++) {
            int[] ids = new int[i];
            Foo[] foos = new Foo[i];
            for (int j = 1; j <= i; j++) {
                ids[j - 1] = j;
                foos[j - 1] = new Foo(j);
            }

            List<Foo> l1 = fooMapper.selectTraditionalWay(ids);
            List<Foo> l2 = fooMapper.select1(ids);
            List<Foo> l3 = fooMapper.select2(foos);
            List<Foo> l4 = fooMapper.select3(ids);
            Assert.assertEquals(l1.size(), i);
            Assert.assertEquals(l1.size(), l2.size());
            Assert.assertEquals(l1.size(), l3.size());
            Assert.assertEquals(l1.size(), l4.size());
            for (int k = 0; k < l1.size(); k++) {
                Assert.assertEquals(l1.get(k), l2.get(k));
                Assert.assertEquals(l1.get(k), l3.get(k));
                Assert.assertEquals(l1.get(k), l4.get(k));
            }
        }
    }
}
