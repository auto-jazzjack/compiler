package io.penguin.compiler.jdk.scanner;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class Scanner {

    private final Map<String, Token> tokenMap;

    public Scanner() {
        tokenMap = new HashMap<>();

        int idx = 0;
        tokenMap.put("identifier", new Token(idx++, "identifier", ""));
        tokenMap.put("int", new Token(idx++, "int", ""));
        tokenMap.put("float", new Token(idx++, "float", ""));
        tokenMap.put("double", new Token(idx++, "double", ""));
        tokenMap.put("++", new Token(idx++, "increment", ""));
        tokenMap.put("=", new Token(idx++, "assign", ""));
        tokenMap.put(";", new Token(idx++, "semicolon", ""));
        tokenMap.put("\"", new Token(idx++, "double quotation", ""));
        tokenMap.put("'", new Token(idx++, "quotation", ""));
        tokenMap.put("number", new Token(idx++, "number", ""));
        tokenMap.put("public", new Token(idx++, "public", ""));
        tokenMap.put("private", new Token(idx++, "private", ""));
        tokenMap.put("protected", new Token(idx++, "protected", ""));
        tokenMap.put("static", new Token(idx++, "static", ""));
        tokenMap.put("class", new Token(idx++, "class", ""));
        tokenMap.put("{", new Token(idx++, "left brace", ""));
        tokenMap.put("}", new Token(idx, "right brace", ""));
    }

    public Token readToken(String list, AtomicInteger idx) {
        return Optional.ofNullable(readWord(list, idx))
                .map(i -> {
                    if (StringUtils.isNumeric(i)) {
                        return tokenMap.get("number");
                    }
                    return tokenMap.get(i);
                })
                .orElse(tokenMap.get("identifier"));
    }

    boolean IsAlphaNum_(char ch) {
        if (ch == '_') {
            return true;
        } else if (ch >= 'a' && ch <= 'z') {
            return true;
        } else if (ch >= 'A' && ch <= 'Z') {
            return true;
        } else if (ch >= '0' && ch <= '9') {
            return true;
        } else {
            return false;
        }
    }

    private String readWord(String list, AtomicInteger idx) {
        StringBuilder retv = new StringBuilder();
        skipBlank(list, idx);
        while (list.length() > idx.get()) {
            char c = list.charAt(idx.getAndIncrement());
            if (!IsAlphaNum_(c)) {
                if (retv.length() == 0) {
                    retv.append(c);
                    idx.getAndIncrement();
                }
                break;
            }
            switch (c) {
                case '\n':
                case '\t':
                case ' ':
                    return retv.toString();
                case '{':
                case '}':
                    if (retv.toString().length() > 0) {
                        idx.decrementAndGet();
                        return String.valueOf(retv);
                    } else {
                        return String.valueOf(c);
                    }
                default:
                    retv.append(c);
            }

        }
        return retv.toString();
    }

    //helper to skip blank
    private void skipBlank(String list, AtomicInteger idx) {
        while (list.length() > idx.get()) {
            switch (list.charAt(idx.getAndIncrement())) {
                case '\n':
                case '\t':
                case ' ':
                    break;
                default:
                    idx.decrementAndGet();
                    return;
            }
        }
    }
}

