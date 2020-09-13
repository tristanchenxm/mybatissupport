package nameless.common.mybatis.test;

import java.util.Objects;

public class Foo {
    private int id;
    private String value;

    public Foo() {}

    public Foo(int id) {
        this.id = id;
    }

    public Foo(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return id * 31 + (value == null ? 0 : value.hashCode());
    }

    @Override
    public boolean equals(Object another) {
        if (!(another instanceof Foo))
            return false;
        return Objects.equals(id, ((Foo) another).id) && Objects.equals(value, ((Foo) another).value);
    }
}
