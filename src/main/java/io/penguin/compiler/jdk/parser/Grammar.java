package io.penguin.compiler.jdk.parser;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Grammar {

    private Version generator;

    @Data
    static public class Version {
        private Map<String, List<String>> v1;
    }

}
