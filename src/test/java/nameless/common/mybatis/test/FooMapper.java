package nameless.common.mybatis.test;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface FooMapper {
    String TABLE_SCHEMA = "create table tbl(id int not null primary key, value varchar(100) not null);";

    @Insert("insert into tbl(id, value) values (#{id}, #{value})")
    int insert(Foo foo);

    @Select("<script>select * from tbl where id in "
            + "<foreach collection='ids' item='id' open='(' close=')' separator=','>#{id}</foreach>"
            + "</script>"
    )
    List<Foo> selectTraditionalWay(@Param("ids") int[] ids);

    @Select("select * from tbl where id in (#{@ids})")
    List<Foo> select1(@Param("ids") int[] ids);

    @Select("select * from tbl where id in (#{@foos.id})")
    List<Foo> select2(@Param("foos") Foo[] foos);

    List<Foo> select3(@Param("ids") int[] ids);

    @Select("select * from tbl")
    List<Foo> selectAll();
}
