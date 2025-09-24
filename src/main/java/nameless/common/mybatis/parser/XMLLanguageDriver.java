package nameless.common.mybatis.parser;

import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.GenericTokenParser;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;

import static com.pintar.common.mybatis.parser.IteratorTokenHandler.ITERATOR_CLOSE_TAG;
import static com.pintar.common.mybatis.parser.IteratorTokenHandler.ITERATOR_OPEN_TAG;


public class XMLLanguageDriver extends org.apache.ibatis.scripting.xmltags.XMLLanguageDriver {

    @Override
    public SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType) {
        String sqlScriptBefore = script.getStringBody().trim();
        IteratorTokenHandler tokenHandler = new IteratorTokenHandler();
        String sqlScriptAfter = new GenericTokenParser(ITERATOR_OPEN_TAG, ITERATOR_CLOSE_TAG, tokenHandler).parse(sqlScriptBefore);
        if (tokenHandler.isIteratorTagPresents()) {
            try {
                Field bodyField = XNode.class.getDeclaredField("body");
                bodyField.setAccessible(true);
                bodyField.set(script, sqlScriptAfter);
                String xml = getXml(script, sqlScriptBefore, sqlScriptAfter);
                XPathParser parser = new XPathParser(xml, false, configuration.getVariables(), new XMLMapperEntityResolver());
                return super.createSqlSource(configuration, parser.evalNode("/" + script.getName()), parameterType);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return super.createSqlSource(configuration, script, parameterType);
    }

    /**
     * Get the XML string of the XNode, and replace the old SQL script with the new one.
     * @param script the XNode
     * @param sqlScriptBefore the old SQL script
     * @param sqlScriptAfter the new SQL script
     * @return the new XML string
     */
    private static String getXml(XNode script, String sqlScriptBefore, String sqlScriptAfter) {
        String xml = script.toString();
        int indexOfOldSqlScript = xml.indexOf(sqlScriptBefore.trim());
        if (indexOfOldSqlScript >= 0) {
            xml = xml.substring(0, indexOfOldSqlScript) + sqlScriptAfter + xml.substring(indexOfOldSqlScript + sqlScriptBefore.length());
        }
        return xml;
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
