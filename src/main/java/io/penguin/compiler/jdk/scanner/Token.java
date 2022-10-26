package io.penguin.compiler.jdk.scanner;

import lombok.Data;

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
}

