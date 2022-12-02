package io.penguin.compiler.jdk.parser;


import io.penguin.compiler.jdk.parser.generator.ParseTableGenerator;
import io.penguin.compiler.jdk.scanner.Token;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static io.penguin.compiler.jdk.scanner.Token.TOKEN_MAP;

public class ParseTable {

    //grammar do not need to be changed.
    private Map<Integer/*state*/, Map<Token/*token*/, Integer/*next state*/>> table;
    private Map<Integer/*state*/, Set<CacheKey>> ruleSetByState;
    private static final Map<String, Integer> NON_TERMINAL_MAP = new HashMap<>();

    private final AtomicInteger maxState;
    private Grammar grammars;

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

        public CacheKey deepCopy() {
            return CacheKey.builder()
                    .readIdx(this.readIdx)
                    .lhs(this.lhs)
                    .rhs(new ArrayList<>(this.rhs))
                    .build();
        }

        public List<CacheKey> getAllGrammarIfNonTerminal(Grammar grammars) {
            Token token = this.nextTokenOrNonTerminal();
            /**
             * This is for exclude % in Nonterminal
             * */
            String key = token.getTokenValue().substring(1);

            return grammars.getGenerator()
                    .getV1()
                    .get(key)
                    .stream()
                    .map(i -> {
                        String[] s = i.split(" ");
                        return CacheKey.builder()
                                .rhs(Arrays.stream(s).collect(Collectors.toList()))
                                .lhs(token.getTokenValue())
                                .readIdx(0)
                                .build();
                    })
                    .collect(Collectors.toList());


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
        init();
    }


    public void updateTableUntilNoChange() {

        int befSize;
        LinkedBlockingQueue<Pair<Integer, CacheKey>> queue = ruleSetByState.get(0)
                .stream()
                .map(i -> Pair.of(0, i.deepCopy()))
                .distinct()
                .collect(Collectors.toCollection(LinkedBlockingQueue::new));


        do {
            befSize = sizeof();
            while (!queue.isEmpty()) {
                Pair<Integer, CacheKey> current = queue.poll();

                CacheKey next = current.getValue().increaseIdxWithDeepCopy();

                ruleSetByState.computeIfAbsent(current.getKey(), (k) -> new HashSet<>())
                        .add(current.getValue());

                if (next == null) {
                    continue;
                }

                Integer nextState = Optional.ofNullable(table.get(current.getKey()))
                        .map(i -> i.get(current.getValue().nextTokenOrNonTerminal()))
                        .orElseGet(maxState::incrementAndGet);

                Token token = current.getValue().nextTokenOrNonTerminal();
                table.computeIfAbsent(current.getKey(), (k) -> new HashMap<>())
                        .put(token, nextState);

                if (token.getTokenNumber() < 0) {
                    ruleSetByState.computeIfAbsent(nextState, (k) -> new HashSet<>())
                            .addAll(current.getValue().getAllGrammarIfNonTerminal(grammars));
                }

                queue.add(Pair.of(nextState, next));
            }
            if (befSize < sizeof()) {
                break;
            }


        } while (befSize < sizeof());

        System.out.println();
    }

    int sizeof() {
        int retv = 0;
        for (Map.Entry<Integer, Map<Token/*token*/, Integer/*next state*/>> i : this.table.entrySet()) {
            retv = retv + Optional.ofNullable(i.getValue())
                    .map(j -> j.size())
                    .orElse(0);
        }
        return retv;
    }

    public void init() throws Exception {
        table = new HashMap<>();
        ruleSetByState = new HashMap<>();

        grammars = ParseTableGenerator.readFile("");

        for (Map.Entry<String, List<String>> grammar : grammars.getGenerator().getV1().entrySet()) {

            grammar.getValue().forEach(j -> {
                CacheKey cacheKey = CacheKey.builder()
                        .readIdx(0)
                        .lhs(grammar.getKey())
                        .rhs(Arrays.stream(j.split(" ")).collect(Collectors.toList()))
                        .build();

                ruleSetByState.computeIfAbsent(0, (k) -> new HashSet<>()).add(cacheKey);
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

