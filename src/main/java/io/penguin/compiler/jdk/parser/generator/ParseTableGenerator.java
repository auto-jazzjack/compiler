package io.penguin.compiler.jdk.parser.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.penguin.compiler.jdk.parser.Grammar;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;

public class ParseTableGenerator {

    static final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    public static Grammar readFile(String path) throws Exception {
        String target = Optional.ofNullable(path)
                .filter(i -> !i.isEmpty())
                .orElse("./src/grammar.yaml");

        File file = new File(target);
        FileInputStream fileInputStream = new FileInputStream(file);

        return objectMapper.readValue(fileInputStream.readAllBytes(), Grammar.class);
    }
}
/*


type Grammar struct {
	Generator Version `yaml:"generator"`
}

type Version struct {
	V1 map[string][]string `yaml:"v1"`
}

func ReadFile(path string) Grammar {
	var target = path
	if len(target) == 0 {
		target = "./grammar.yaml"
	}
	yamlFile, err := ioutil.ReadFile(target)
	retv := Grammar{}
	err = yaml.Unmarshal(yamlFile, &retv)

	fmt.Println(retv)
	if err != nil {
		panic(err)
	}

	return retv
}
*/
