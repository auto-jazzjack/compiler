package io.penguin.compiler.jdk.parser;


import io.penguin.compiler.jdk.model.AbstractTree;
import io.penguin.compiler.jdk.scanner.Scanner;
import io.penguin.compiler.jdk.scanner.Token;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

public class Parser {
    private final Scanner sc;
    private final List<Token> stack;
    private final ParseTable table;

    public Parser() throws Exception {

        sc = new Scanner();
        stack = new Stack<>();
        table = new ParseTable();
    }

    public AbstractTree createAST(String code) {

        int state_ = 0;
        AtomicInteger idx = new AtomicInteger(0);
        Integer res = 0;
        for (int i = 0; i < code.length(); i++) {
            Token tmp = sc.readToken(code, idx);
            stack.add(tmp);

            if (tmp == null) {
                System.out.println();
            }
            //지금 상태에서 token을 보고 문법을 결정.
            res = table.getNextState(state_, tmp.getTokenNumber());

            if (res == null) {

            } else {
                if (res > 0) {
                    //this is reduction
                } else if (res < 0) {
                    //this is shift
                } else {
            /*err:=errors.New(fmt.Sprintf("Cannot parse it at %d %s", idx, tmp.TokenValue))
            panic(err)*/
                }
            }

        }
        return null;
    }


}

