package io.penguin.compiler.jdk.scanner;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Token {
    private Integer TokenNumber;
    private String TokenName;
    private String TokenValue;

    public Token(Integer tokenNumber, String tokenName, String tokenValue) {
        TokenNumber = tokenNumber;
        TokenName = tokenName;
        TokenValue = tokenValue;
    }

    public static Map<String, Token> TOKEN_MAP;
    static {
        int idx = 0;
        TOKEN_MAP = new HashMap<>();
        TOKEN_MAP.put("identifier", new Token(idx++, "identifier", ""));
        TOKEN_MAP.put("int", new Token(idx++, "int", ""));
        TOKEN_MAP.put("float", new Token(idx++, "float", ""));
        TOKEN_MAP.put("double", new Token(idx++, "double", ""));
        TOKEN_MAP.put("++", new Token(idx++, "increment", ""));
        TOKEN_MAP.put("=", new Token(idx++, "assign", ""));
        TOKEN_MAP.put(";", new Token(idx++, "semicolon", ""));
        TOKEN_MAP.put("\"", new Token(idx++, "double quotation", ""));
        TOKEN_MAP.put("'", new Token(idx++, "quotation", ""));
        TOKEN_MAP.put("number", new Token(idx++, "number", ""));
        TOKEN_MAP.put("public", new Token(idx++, "public", ""));
        TOKEN_MAP.put("private", new Token(idx++, "private", ""));
        TOKEN_MAP.put("protected", new Token(idx++, "protected", ""));
        TOKEN_MAP.put("static", new Token(idx++, "static", ""));
        TOKEN_MAP.put("class", new Token(idx++, "class", ""));
        TOKEN_MAP.put("{", new Token(idx++, "left brace", ""));
        TOKEN_MAP.put("}", new Token(idx++, "right brace", ""));
        TOKEN_MAP.put("dummy", new Token(idx, "dummy", ""));
    }
}

