package io.penguin.compiler.jdk;

import io.penguin.compiler.jdk.model.AbstractTree;
import io.penguin.compiler.jdk.parser.Parser;

public class CompileJavaApplication {

    public static void main(String[] args) throws Exception {
        System.out.println("hello");

        Parser parser = new Parser();
        AbstractTree ast = parser.createAST("public class MyClass{" +
                "public int ab = 99;" +
                "}");

    }

}
