package io.penguin.compiler.jdk.model;


import io.penguin.compiler.jdk.scanner.Token;
import lombok.Data;

@Data
public class AbstractTree {
    private Token token;
    private AbstractTree son;
    private AbstractTree brothers;
}
