package nameless.common.mybatis.parser;

import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.GenericTokenParser;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;

import static nameless.common.mybatis.parser.IteratorTokenHandler.ITERATOR_CLOSE_TAG;
import static nameless.common.mybatis.parser.IteratorTokenHandler.ITERATOR_OPEN_TAG;

public class XMLLanguageDriver extends org.apache.ibatis.scripting.xmltags.XMLLanguageDriver {

    @Override
    public SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType) {
        String sqlScript = script.getStringBody();
        IteratorTokenHandler tokenHandler = new IteratorTokenHandler();
        sqlScript = new GenericTokenParser(ITERATOR_OPEN_TAG, ITERATOR_CLOSE_TAG, tokenHandler).parse(sqlScript);
        if (tokenHandler.isIteratorTagPresents()) {
            try {
                Field bodyField = XNode.class.getDeclaredField("body");
                bodyField.setAccessible(true);
                bodyField.set(script, sqlScript);
                XPathParser parser = new XPathParser(script.toString(), false, configuration.getVariables(), new XMLMapperEntityResolver());
                return super.createSqlSource(configuration, parser.evalNode("/" + script.getName()), parameterType);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return super.createSqlSource(configuration, script, parameterType);
    }

    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        IteratorTokenHandler tokenHandler = new IteratorTokenHandler();
        script = new GenericTokenParser(ITERATOR_OPEN_TAG, ITERATOR_CLOSE_TAG, tokenHandler).parse(script);
        if (tokenHandler.isIteratorTagPresents() && !script.startsWith("<script>")) {
            script = "<script>" + script + "</script>";
        }
        return super.createSqlSource(configuration, script, parameterType);
    }

}
