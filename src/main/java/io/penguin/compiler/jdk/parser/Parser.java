package io.penguin.compiler.jdk.parser;


import io.penguin.compiler.jdk.model.AbstractTree;
import io.penguin.compiler.jdk.scanner.Scanner;
import io.penguin.compiler.jdk.scanner.Token;

import java.util.ArrayList;
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

        int entry, ruleNumber, lhs;
        int currentState;

        List<Integer> stateStack = new ArrayList<>();
        List<Integer> symbolStack = new ArrayList<>();

        stateStack.add(0);
        AtomicInteger readIndex = new AtomicInteger(0);
        Token token = this.sc.readToken(code, readIndex);

        while (readIndex.get() < code.length()) {
            currentState = stateStack.get(stateStack.size() - 1);
            entry = table.getNextState(currentState, token.getTokenNumber());
            if (entry > 0) {
                //shift action
                symbolStack.add(token.getTokenNumber());
                stateStack.add(entry);
                token = this.sc.readToken(code, readIndex);
            } else if (entry < 0) { //reduce action
                ruleNumber = -entry;
                /*if (ruleNumber == GOAL_RULE) { //accept action
                    if (errcnt == 0) printf(" *** valid source ***\n");
                    else printf(" *** error in source : %d\n", errcnt);
                    break;
                }*/
                //System.out.println();
                //semantic(ruleNumber);
                //sp = sp - rightLength[ruleNumber];
                Integer s = table.rightLength(ruleNumber);
                for (int k = 0; k < s; k++) {
                    stack.remove(stateStack.size());
                }
                lhs = table.leftSymbol(ruleNumber);
                currentState = table.getNextState(stateStack.get(stateStack.size() - 1), lhs);
                symbolStack.add(lhs);
                stateStack.add(currentState);
            } else {
                throw new IllegalStateException("Cannot parse it");
            }
        }
        return null;
    }


}

