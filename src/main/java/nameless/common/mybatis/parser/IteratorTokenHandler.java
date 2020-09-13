package nameless.common.mybatis.parser;

import org.apache.ibatis.parsing.TokenHandler;

public class IteratorTokenHandler implements TokenHandler {
    public static final String ITERATOR_OPEN_TAG = "#{@";
    public static final String ITERATOR_CLOSE_TAG = "}";

    private boolean iteratorTagPresents = false;

    @Override
    public String handleToken(String content) {
        iteratorTagPresents = true;
        StringBuilder collectionName = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            char ch = content.charAt(i);
            if (isDigit(ch) || isAlphabet(ch)) {
                collectionName.append(ch);
            } else if (collectionName.length() > 0) {
                break;
            }
        }
        String newExpression = "item" + content.substring(collectionName.length());
        return String.format("<foreach collection='%s' item='item' separator=','>#{%s}</foreach>", collectionName.toString(), newExpression);
    }

    private boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private boolean isAlphabet(char ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z');
    }

    boolean isIteratorTagPresents() {
        return iteratorTagPresents;
    }
}
