package io.penguin.compiler.jdk.parser;


import io.penguin.compiler.jdk.parser.generator.ParseTableGenerator;
import io.penguin.compiler.jdk.scanner.Token;
import lombok.Builder;
import lombok.Data;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static io.penguin.compiler.jdk.scanner.Token.TOKEN_MAP;

public class ParseTable {

    //grammar do not need to be changed.
    private Map<Integer/*state*/, Map<Token/*token*/, Integer/*next state*/>> table;
    private Map<Integer/*state*/, Set<CacheKey>> ruleMap;
    private final Map<CacheKey, Integer> stateCache;
    private static final Map<String, Integer> NON_TERMINAL_MAP = new HashMap<>();

    private final AtomicInteger maxState;

    @Data
    @Builder
    static class CacheKey {
        private List<String> rhs;
        private Integer readIdx;
        private String lhs;

        public Token nextTokenOrNonTerminal() {

            String s = rhs.get(readIdx);
            if (s.startsWith("%")) {
                NON_TERMINAL_MAP.putIfAbsent(s, NON_TERMINAL_MAP.values().stream()
                        .min(Integer::compareTo)
                        .orElse(0) - 1
                );
                return new Token(NON_TERMINAL_MAP.get(s), s, s);
                //NON_TERMINAL_MAP.get(s);//-cached.get(cacheKey);
            } else {
                return TOKEN_MAP.get(s);
            }
        }

        public CacheKey increaseIdxWithDeepCopy() {

            if (readIdx + 1 > rhs.size()) {
                return null;
            }

            return CacheKey.builder()
                    .lhs(lhs)
                    .readIdx(readIdx + 1)
                    .rhs(rhs)
                    .build();
        }
    }

    public ParseTable() throws Exception {
        maxState = new AtomicInteger(0);
        stateCache = new HashMap<>();
        init();
    }


    public void updateTableUntilNoChange() {
        AtomicBoolean fin = new AtomicBoolean(true);

        while (fin.get()) {
            fin.set(false);
            for (Map.Entry<Integer, Set<CacheKey>> entry : ruleMap.entrySet()) {
                table.putIfAbsent(entry.getKey(), new HashMap<>());

                for (CacheKey current : entry.getValue()) {
                    CacheKey next = current.increaseIdxWithDeepCopy();

                    if (next == null) {
                        continue;
                    }
                    stateCache.computeIfAbsent(next, (k) -> maxState.incrementAndGet());
                    ruleMap.computeIfAbsent(stateCache.get(next), (k) -> {
                                fin.set(true);
                                return new HashSet<>();
                            })
                            .add(next);

                    table.get(entry.getKey())
                            .put(current.nextTokenOrNonTerminal(), stateCache.get(next));
                    if (fin.get()) {
                        break;
                    }
                }
                if (fin.get()) {
                    break;
                }
            }

        }

        System.out.println();
    }

    public void init() throws Exception {
        table = new HashMap<>();
        ruleMap = new HashMap<>();

        Grammar grammars = ParseTableGenerator.readFile("");

        for (Map.Entry<String, List<String>> grammar : grammars.getGenerator().getV1().entrySet()) {

            grammar.getValue().forEach(j -> {
                CacheKey cacheKey = CacheKey.builder()
                        .readIdx(0)
                        .lhs(grammar.getKey())
                        .rhs(Arrays.stream(j.split(" ")).collect(Collectors.toList()))
                        .build();

                stateCache.put(cacheKey, 0);
                ruleMap.computeIfAbsent(0, (k) -> new HashSet<>()).add(cacheKey);
            });


        }
        updateTableUntilNoChange();
    }


    //input : current state/token
    //output : next state
    public Integer getNextState(Integer state, Token token) {
        try {
            return table.get(state).get(token);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer leftSymbol(Integer ruleNumber) {
        return null;
    }

    public Integer rightLength(Integer ruleNumber) {
        return null;
    }

}

