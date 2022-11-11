package io.penguin.compiler.jdk.parser;


import io.penguin.compiler.jdk.parser.generator.ParseTableGenerator;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static io.penguin.compiler.jdk.scanner.Token.TOKEN_MAP;

public class ParseTable {

    //grammar do not need to be changed.
    private Map<Integer/*state*/, Map<Integer/*token*/, Integer/*next state*/>> table;
    private Map<CacheKey, Integer> cached;
    private static Map<String, Integer> NON_TERMINAL_MAP = new HashMap<>();

    private AtomicInteger maxState;
    private AtomicInteger maxNonTerminalValue;

    @Data
    @Builder
    static class CacheKey {
        private List<String> gramValue;
        private Integer readIdx;
        private String gramKey;


        public Integer getTokenOrNonTerminal(Map<CacheKey, Integer> cached, AtomicInteger maxState) {

            String s = gramValue.get(readIdx);
            if (s.startsWith("%")) {
                NON_TERMINAL_MAP.putIfAbsent(s,
                        NON_TERMINAL_MAP.values().stream()
                                .min(Integer::compareTo)
                                .orElse(0) - 1
                );
                //This is nonterminal node
                CacheKey cacheKey = increaseIdxWithDeepCopy();
                if (!cached.containsKey(cacheKey)) {
                    cached.put(cacheKey, maxState.incrementAndGet());
                }
                return NON_TERMINAL_MAP.get(s);//-cached.get(cacheKey);
            } else {
                return TOKEN_MAP.get(gramValue.get(readIdx)).getTokenNumber();
            }
        }

        public CacheKey increaseIdxWithDeepCopy() {

            if (readIdx + 1 > gramValue.size()) {
                return null;
            }

            return CacheKey.builder()
                    .gramKey(gramKey)
                    .readIdx(readIdx + 1)
                    .gramValue(gramValue)
                    .build();
        }
    }

    public ParseTable() throws Exception {
        maxState = new AtomicInteger(0);
        cached = new HashMap<>();
        maxNonTerminalValue = new AtomicInteger(0);
        init();
    }

    public void updateTableUntilNoChange() {

        int befSize;

        do {

            befSize = cached.size();
            for (Map.Entry<CacheKey, Integer> entry : cached.entrySet()) {
                CacheKey k = entry.getKey();

                CacheKey cacheKey = k.increaseIdxWithDeepCopy();
                if (cacheKey == null) {
                    continue;
                }

                if (!cached.containsKey(cacheKey)) {
                    cached.put(cacheKey, maxState.incrementAndGet());
                    table.putIfAbsent(entry.getValue(), new HashMap<>());
                    table.get(entry.getValue()).put(entry.getKey().getTokenOrNonTerminal(cached, maxState),
                            maxState.get()
                    );
                    break;
                } else {
                    Integer state = cached.get(cacheKey);
                    table.putIfAbsent(entry.getValue(), new HashMap<>());
                    table.get(entry.getValue()).put(
                            entry.getKey().getTokenOrNonTerminal(cached, maxState),
                            state
                    );
                }


            }
        } while (befSize != cached.size());
        System.out.println();
    }

    public void init() throws Exception {
        table = new HashMap<>();

        Grammar grammars = ParseTableGenerator.readFile("");
        for (Map.Entry<String, List<String>> grammar : grammars.getGenerator().getV1().entrySet()) {

            grammar.getValue().forEach(j -> cached.put(CacheKey.builder()
                    .readIdx(0)
                    .gramKey(grammar.getKey())
                    .gramValue(Arrays.stream(j.split(" ")).collect(Collectors.toList()))
                    .build(), 0));

        }
        updateTableUntilNoChange();
    }


    //input : current state/token
    //output : next state
    public Integer getNextState(Integer state, int token) {
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

