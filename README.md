# GraphiMuse

## Introduction

GraphiMuse is an API misuse detection tool based on probabilistic graphical models. By utilizing this tool, you can mine API usage patterns and detect API misuses in projects based on these patterns.

The GraphiMuse folder contains the Java source code for the tool. You can navigate to the PAMD folder, execute the command, and obtain API misuse detection results. The specific command for execution is described in the Usage section.

The GraphiMuse_DocumentPart folder contains the source code for our document information mining. You can use this code according to your needs to extract document information and improve API usage rules. The extracted document rules are saved in GraphiMuse/rules.txt.

The ExperimentData folder showcases some of our experimental data. 

## Contributors

## Usage

### Through MUBench

We run the detector in our experiments through [the benchmarking pipeline MUBench](https://github.com/stg-tud/MUBench).
The respective detector runner is [GraphiMuseRunner](./mubench/src/main/java/de/tu_darmstadt/stg/mubench/PAMDRunner.java).

### Standalone

To run the detector directly, you may invoke it with one of the following commands, depending on whether you want to provide correct usage examples for pattern mining or whether the detector should mine patterns from the target project itself:

    $> java de.tu_darmstadt.stg.mubench.[X]Runner detector_mode "1" \
          pattern_src_path "/path/to/correct/usages/src" pattern_classpath "" \
          target_src_path "/path/to/target/project/src" target_classpath "" \
          dep_classpath "target:dependency:classpath" \
          target "findings-output.yml" run_info "run-info-output.yml"
    
    $> java de.tu_darmstadt.stg.mubench.[X]Runner detector_mode "0" \
          target_src_path "/path/to/target/project/src" target_classpath "" \
          dep_classpath "target:dependency:classpath" \
          target "findings-output.yml" run_info "run-info-output.yml"



### Document Mining

To run the miner, following packages should be installed:

```
$pip install langchain bs4 jsonlines py2neo 
```

Build the declaration graph, and you should change the address of graphdb to your own

```
python Database\declarationGraph.py 
```

Build the vector database from document for matching

```
python Database\vectorDB.py 
```

Mining rules from document, and you should add your own OpenAI apikey first

```
python contraintMining.py 
```

The rules are stored in data/rule_exp.txt
# GraphieMuse
