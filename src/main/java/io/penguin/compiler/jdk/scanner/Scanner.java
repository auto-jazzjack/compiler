package io.penguin.compiler.jdk.scanner;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static io.penguin.compiler.jdk.scanner.Token.TOKEN_MAP;

public class Scanner {


    public Scanner() {

    }

    public Token readToken(String list, AtomicInteger idx) {
        return Optional.ofNullable(readWord(list, idx))
                .map(i -> {
                    if (StringUtils.isNumeric(i)) {
                        return TOKEN_MAP.get("number");
                    }
                    return TOKEN_MAP.get(i);
                })
                .orElse(TOKEN_MAP.get("identifier"));
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

