package io.penguin.compiler.jdk.parser;


import io.penguin.compiler.jdk.parser.generator.ParseTableGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseTable {

    //grammar do not need to be changed.
    private Map<Integer, Map<Integer, Integer>> grammars;

    static class CacheKey {
        List<String> gram;
        int read;
        String gramKey;
    }

    public ParseTable() throws Exception {

        grammars = new HashMap<>();
        Grammar grammar = ParseTableGenerator.readFile("");
		/*gr = ParseTableGenerator.ReadFile("");
		var tmp = ParseTable{}
		grammars := make(map[int]map[int]int)

		for grammarKey, element := range gr.Generator.V1 {

			for _, el := range element {

				gram := strings.Split(el, " ")
				cached.Put(CacheKey{
					gram:    gram,
							read:    0,
							gramKey: grammarKey,
				}, 0)
			}
		}*/

    }

    //input : current state/token
//output : next state
    public Integer getNextState(int state, int tokenNumber) {
        return grammars.computeIfAbsent(state, (k) -> new HashMap<>())
                .get(tokenNumber);
    }

}

