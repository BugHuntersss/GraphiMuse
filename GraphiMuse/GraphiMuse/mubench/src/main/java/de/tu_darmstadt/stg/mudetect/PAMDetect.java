package de.tu_darmstadt.stg.mudetect;

import de.tu_darmstadt.stg.mubench.PAMDStrategy;
import de.tu_darmstadt.stg.mudetect.aug.model.*;
import de.tu_darmstadt.stg.mudetect.aug.model.actions.*;
import de.tu_darmstadt.stg.mudetect.aug.model.controlflow.ExceptionHandlingEdge;
import de.tu_darmstadt.stg.mudetect.aug.model.dot.AUGDotExporter;
import de.tu_darmstadt.stg.mudetect.aug.model.dot.AUGEdgeAttributeProvider;
import de.tu_darmstadt.stg.mudetect.aug.model.dot.AUGNodeAttributeProvider;
import de.tu_darmstadt.stg.mudetect.aug.model.patterns.APIUsagePattern;
import de.tu_darmstadt.stg.mudetect.aug.model.patterns.PatternGroup;
import de.tu_darmstadt.stg.mudetect.aug.persistence.PersistenceAUGDotExporter;
import de.tu_darmstadt.stg.mudetect.aug.visitors.AUGLabelProvider;
import de.tu_darmstadt.stg.mudetect.aug.visitors.BaseAUGLabelProvider;
import de.tu_darmstadt.stg.mudetect.model.*;
import de.tu_darmstadt.stg.mudetect.aug.model.util.UnionCondition;
import edu.iastate.cs.mudetect.mining.Model;
import edu.iastate.cs.mudetect.mining.Augs;


import rule.DocumentRule;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class PAMDetect {

    public final Model model;
    private final OverlapsFinder overlapsFinder;
    private final ViolationPredicate violationPredicate;
    private final BiFunction<Overlaps, Model, List<Violation>> filterAndRankingStrategy;
    public static final HashMap<String, ArrayList<DocumentRule>> rules = PAMDStrategy.loadDocumentRule();
    public static HashMap<APIUsageExample, ArrayList<String>> violationFromDocumentCheck = new HashMap<>();
    
    public AUGLabelProvider labelProvider = new BaseAUGLabelProvider();
    public Augs augs;


    public PAMDetect(Model model,
                     OverlapsFinder overlapsFinder,
                     ViolationPredicate violationPredicate,
                     BiFunction<Overlaps, Model, List<Violation>> filterAndRankingStrategy){
        this.model = model;
        this.overlapsFinder = overlapsFinder;
        this.violationPredicate = violationPredicate;
        this.filterAndRankingStrategy = filterAndRankingStrategy;
        augs = Augs.getInstance();

    }

    public void join(APIUsagePattern pattern, HashMap<String, ArrayList<DocumentRule>> docRules){
        Set<Node> nodeSet = new HashSet<>(pattern.vertexSet());
        Iterator<Node> iterator = nodeSet.iterator();
        while(iterator.hasNext()){
            Node node = iterator.next();
            if(node instanceof MethodCallNode){
                String label = labelProvider.getLabel(node);
                ArrayList<DocumentRule> ruleList = docRules.get(label);
                if(ruleList != null){
                    for (DocumentRule rule : ruleList) {
                        rule.modifyPattern(pattern, (MethodCallNode) node);
                    }
                }
            }
        }


    }

    public void filterPatterns(Set<APIUsagePattern> patterns){
        patterns.removeIf(this::hasUnknownNode);
        Iterator<APIUsagePattern> iterator = patterns.iterator();
        APIUsagePattern pattern;
        while(iterator.hasNext()){
            pattern = iterator.next();
            if(hasSubPattern(pattern, patterns)) iterator.remove();
        }
        patterns.removeIf(pattern1 -> pattern1.vertexSet().size() > 10);
        patterns.removeIf(this::tooSmallPattern);
    }

    public boolean tooSmallPattern(APIUsagePattern pattern){
        Set<Node> nodes = pattern.vertexSet().stream().filter(node -> node instanceof ActionNode).collect(Collectors.toSet());
        if(nodes.size() > 2) return false;
        if(nodes.stream().filter(node -> node instanceof ConstructorCallNode).collect(Collectors.toSet()).size() >= 1 ) return true;
        return false;
    }

    public void processPatterns(Set<APIUsagePattern> patterns){
        for (APIUsagePattern pattern : patterns) {
            deleteReturnNode(pattern);
            deleteExceptionProcessBlock(pattern);
        }
    }

    public boolean hasUnknownNode(APIUsagePattern pattern){
        for (Node node : pattern.vertexSet()) {
            if(labelProvider.getLabel(node).contains("UNKNOWN")){
                return true;
            }
        }
        return false;
    }

    public boolean hasSubPattern(APIUsagePattern pattern, Set<APIUsagePattern> patterns){
        for (APIUsagePattern apiUsagePattern : patterns) {
            if(pattern != apiUsagePattern){
                if(pattern.contains(apiUsagePattern))
                    return true;
            }
        }
        return false;
    }

    public void deleteReturnNode(APIUsagePattern pattern) {
        Set<Node> vertexSet = new HashSet<>(pattern.vertexSet());
        for (Node node : vertexSet) {
            if (node instanceof ReturnNode) {
                Set<Edge> edgeSet = new HashSet<>(pattern.incomingEdgesOf(node));
                pattern.removeAllEdges(edgeSet);
                pattern.removeVertex(node);
            }
        }
    }

    public void deleteExceptionProcessBlock(APIUsagePattern pattern){
        Iterator<Node> nodeIterator = pattern.vertexSet().iterator();
        Node catchNode = null;
        Node handerAction = null;
        Set<Edge> edges = null;
        while(nodeIterator.hasNext()){
            Node node = nodeIterator.next();
            if(node instanceof CatchNode) {
                catchNode = node;
                break;
            }
        }
        if(catchNode != null){
            Set<Edge> edgeSet = new HashSet<>(pattern.outgoingEdgesOf(catchNode));
            Iterator<Edge> iterator = edgeSet.iterator();
            while(iterator.hasNext()){
                Edge edge = iterator.next();
                if(edge instanceof ExceptionHandlingEdge){
                    handerAction = edge.getTarget();
                    edges = new HashSet<>(pattern.incomingEdgesOf(handerAction));
                    break;
                }
            }
        }
        if(edges != null) pattern.removeAllEdges(edges);
        if(handerAction != null) pattern.removeVertex(handerAction);
    }

    public Set<PatternGroup> abstractOperation(Set<APIUsagePattern> patterns){
        Set<PatternGroup> patternGroups = new HashSet<>();
        HashMap<String, PatternGroup> patternGroupHashMap = new HashMap<>();
        
        for (APIUsagePattern pattern : patterns) {
            for (String api : pattern.getMethodCalls()) {
                if(api.equals("<init>")) continue;
                if(api.equals("<nullcheck>")) continue;
                if(patternGroupHashMap.get(api) == null){
                    PatternGroup patternGroup = new PatternGroup(api);
                    patternGroup.applyToAttend(pattern);
                    patternGroupHashMap.put(api, patternGroup);
                    patternGroups.add(patternGroup);
                }
                else{
                    PatternGroup patternGroup = patternGroupHashMap.get(api);
                    patternGroup.applyToAttend(pattern);
                }
            }
        }

        Iterator<PatternGroup> patternGroupIterator = patternGroups.iterator();

        while (patternGroupIterator.hasNext()){
            PatternGroup patternGroup = patternGroupIterator.next();
            if(patternGroup.getPatternSet().size() == 1) patternGroupIterator.remove();
        }
        return patternGroups;
    }

    public void toBayesNetwork(Set<APIUsagePattern> patterns){
        for (APIUsagePattern pattern : patterns) {
            for (Node node : pattern.vertexSet()) {
                if(pattern.incomingEdgesOf(node).size() == 0){
                    learn(node, pattern);
                }
            }
        }
    }

    public void learn(Node node, APIUsagePattern pattern){
        //visit itself
        if(node instanceof ActionNode) generateMatrix(node, pattern);
        //如果没有出边，就可以返回。
        if(pattern.outgoingEdgesOf(node).size() == 0) return;
        //visit 每个节点
        for(Node nextNode : pattern.outgoingNodesOf(node)) learn(nextNode, pattern);
    }

    public void generateMatrix(Node node, APIUsagePattern pattern) {
        if(pattern.bayesMatrix.containsKey(node)) return;
        // 获取该节点的入点
        ArrayList<Node> inN = new ArrayList<>(pattern.incomingNodesOf(node));
        if(inN.size() == 0) return ;
        ArrayList<Edge> inE = new ArrayList<>(pattern.incomingEdgesOf(node));
        Map<UnionCondition, Integer> count = new HashMap<>();
        // 存储矩阵
        Map<UnionCondition, Double> prob = new HashMap<>();
        pattern.inN.put(node, inN);

        // i每次变化表示新的一行
        for (int i = 1; i < (1 << inN.size()); i++) {
            prob.put(new UnionCondition(i, inN.size()), 1.0);
            count.put(new UnionCondition(i, inN.size()), 0);
        }

        // 生成矩阵
        Set<Node> nodes = augs.getNodesOfAllLabel().get(labelProvider.getLabel(node));

        for (Node n : nodes) {
            List<Boolean> b = new ArrayList<>();
            for (Node node1 : inN) {
                if(isNeighbor(n, labelProvider.getLabel(node1))) b.add(true);
                else b.add(false);
            }

            UnionCondition unionCondition = new UnionCondition(b);

            if(unionCondition.condition != 0) count.compute(unionCondition, (k, v) -> v + 1);
        }

        for (UnionCondition unionCondition : prob.keySet()) {
            int c = 0;
            int num = -1;
            for (int i = 0; i < unionCondition.n; i++) {
                if(unionCondition.toBooleanList().get(i) && inN.get(i) instanceof ActionNode && inN.get(i).getConfidence() <= 0){
                    num = i;
                    break;
                }
            }
            if(num != -1){
                Set<Node> nodes1 = new HashSet<>();
                if(augs.getNodesOfAllLabel().containsKey(labelProvider.getLabel(inN.get(num)))){
                    nodes1 = augs.getNodesOfAllLabel().get(labelProvider.getLabel(inN.get(num)));
                }
                for (Node node1 : nodes1) {
                    boolean flag = true;
                    for(int i = 0; i < inN.size(); i++){
                        if(i == num) {
                            continue;
                        }
                        boolean isContain = node1.getGraph().vertexLabelSet().contains(labelProvider.getLabel(inN.get(i)));
                        boolean condition = (isContain == unionCondition.booleanList.get(i));

                        flag &= condition;

                    }
                    if(flag) c++;
                }

                if(count.get(unionCondition) == 0 && c == 0)
                    prob.put(unionCondition, 0.0);
                else{
                    if(count.get(unionCondition) <= c) prob.put(unionCondition, (double) count.get(unionCondition)/ (double)c);
                    else
                        prob.put(unionCondition, 0.9);
                }
            }
            else{
                int trueNumber = unionCondition.trueNumber();
                for(int i = 0; i < trueNumber; i++){
                    prob.compute(unionCondition, (k, v) -> v * 0.8);
                }
            }

        }


        /*for (List<Boolean> booleans : prob.keySet()) {

            for(int i = 0; i < booleans.size(); i++){
                Boolean flag = booleans.get(i);
                Node n = inN.get(i);
                Edge e = pattern.getEdge(n, node);
                if(labelProvider.getLabel(node).equals("<catch>")){
                    for (Edge edge : inE) {
                        if(edge instanceof ParameterEdge){
                            Node exceptionNode = edge.getSource();
                            for (Edge edge1 : pattern.incomingEdgesOf(exceptionNode)) {
                                if(edge1 instanceof ThrowEdge){
                                    Node methodCall = edge1.getSource();
                                    pattern.APIFrequency.put("<catch>",pattern.APIFrequency.get(labelProvider.getLabel(methodCall)));
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
                if(flag){
                    if(e instanceof OrderEdge || e instanceof ThrowEdge){
                        //if()
                        double dependency = (double) pattern.APIFrequency.get(labelProvider.getLabel(n)) /
                                (double) pattern.APIFrequency.get(labelProvider.getLabel(node));
                        if(dependency > 1 ) dependency = 1 / dependency;

                        prob.put(booleans, prob.get(booleans) + dependency / orderNum * 0.8);
                    }
                    else{
                        prob.put(booleans, prob.get(booleans) + 0.2 / otherNum);
                    }
                }
                else {
                    if (e instanceof OrderEdge || e instanceof ThrowEdge) {
                        double dependency = (double) pattern.APIFrequency.get(labelProvider.getLabel(n)) /
                                (double) pattern.APIFrequency.get(labelProvider.getLabel(node));
                        if (dependency > 1) dependency = 1 / dependency;

                        prob.put(booleans, prob.get(booleans) + (1.0 - dependency) / orderNum * 0.8);
                    }
                }
            }
            if(orderNum == 0) prob.put(booleans, prob.get(booleans) /0.2);
            if(otherNum == 0) prob.put(booleans, prob.get(booleans) / 0.8);
        }*/

        pattern.addMatrix(node, prob);

    }

    public List<Violation> findViolations(Collection<APIUsageExample> targets) throws IOException, InterruptedException {
        Set<APIUsagePattern> patterns = model.getPatterns();

        PersistenceAUGDotExporter exporter = new PersistenceAUGDotExporter();
        AUGDotExporter prettyPrinter = new AUGDotExporter(new BaseAUGLabelProvider(), new AUGNodeAttributeProvider(), new AUGEdgeAttributeProvider());
        Integer name = 0;
        String dir = System.getProperty("user.dir");

        filterPatterns(patterns);
        processPatterns(patterns);

        System.out.println("begin to combine with document rule");
        HashMap<String, ArrayList<DocumentRule>> docRules = PAMDetect.rules;
        for (APIUsagePattern pattern : patterns) {
            join(pattern , docRules);
        }
        /*for(String str : docRules.keySet()){
            for(DocumentRule rule : docRules.get(str)){
                if(hash.containsKey(rule) && hash.get(rule)) continue;
                APIUsagePattern pattern = rule.genPattern();
                if (pattern != null)
                    patterns.add(pattern);
            }
        }
        filterPatterns(patterns);
        processPatterns(patterns);*/
        System.out.println("finished");

        System.out.println("begin to generate probabilistic model");
        toBayesNetwork(patterns);
        System.out.println("finished");

        for (APIUsagePattern pattern : patterns){
            exporter.toPNGFile(pattern, new File(dir + "/graph/pattern_" + name +".dot"));
            prettyPrinter.toPNGFile(pattern, new File(dir + "/persistence_graph" + "/pattern_" + name +".dot"));
            name++;
        }



        System.out.println("All patterns have been mined");
        name = 0;
        for (APIUsagePattern pattern : patterns){
            if(pattern.beModified){
                prettyPrinter.toPNGFile(pattern, new File(dir + "/modifiedPattern" + "/pattern_" + name +".dot"));
                name ++;
            }
        }

        final Overlaps overlaps = findOverlaps(targets, patterns);
        return filterAndRankingStrategy.apply(overlaps, model);
    }

    private Overlaps findOverlaps(Collection<APIUsageExample> targets, Set<APIUsagePattern> patterns) {
        /*int j = 0;
        APIUsagePattern iteratorPattern = null;
        for(APIUsagePattern pattern : patterns){
            for(Node node : pattern.vertexSet())
            {
                if(node instanceof MethodCallNode && ((MethodCallNode) node).getDeclaringTypeName().equals("Iterator")){
                    if(pattern.getNodeSize() == 3){
                        iteratorPattern = pattern;
                        break;
                    }
                }
            }
            j++;
        }*/

        Overlaps overlaps = new Overlaps();
        for (APIUsageExample target : targets) {
            for (APIUsagePattern pattern : patterns) {
                for (Overlap overlap : overlapsFinder.findOverlaps(target, pattern)) {
                    if (violationPredicate.apply(overlap).orElse(false)) {
                        int num1 = overlap.getMissingNodes()
                                .stream()
                                .filter(node -> node instanceof ActionNode )
                                .collect(Collectors.toSet()).size();
                        int num2 = overlap.getPattern().vertexSet().stream().filter(node -> node instanceof ActionNode).collect(Collectors.toSet()).size();
                        Set<Overlap> filter = overlaps.getViolations()
                                .stream()
                                .filter(overlap1 -> overlap1.getPattern() == overlap.getPattern() && overlap1.getLocation().getFilePath().equals(overlap.getLocation().getFilePath()))
                                .collect(Collectors.toSet());
                        if(num1 <= num2 / 2 && filter.size() == 0 && overlap.probability != 1.0){
                            overlaps.addViolation(overlap);
                        }
                    } else {
                        overlaps.addInstance(overlap);
                    }
                }
            }
        }
        System.out.println("all overlaps have been found");;
        return overlaps;
    }

    public Set<Overlap> filterViolation(HashMap<APIUsagePattern, Set<Overlap>> violationHashMap,
                                        HashMap<APIUsagePattern, Set<Overlap>> instanceHashMap,
                                        Set<PatternGroup> patternGroups){
        Set<Overlap> result = new HashSet<>();
        for (PatternGroup patternGroup : patternGroups) {
            result.addAll(getOverlapGroup(violationHashMap, instanceHashMap, patternGroup));
        }
        return result;
    }

    public Set<Overlap> getOverlapGroup(HashMap<APIUsagePattern, Set<Overlap>> violationHashMap,
                                        HashMap<APIUsagePattern, Set<Overlap>> instanceHashMap,
                                        PatternGroup patternGroup){
        Set<Overlap> result = new HashSet<>();
        for (APIUsagePattern pattern : patternGroup.getPatternSet()) {
            if(instanceHashMap.containsKey(pattern)) {
                return new HashSet<>();
            }
            if(violationHashMap.containsKey(pattern)) {
                result.addAll(violationHashMap.get(pattern));
            }
        }
        return result;
    }

    /*private void checkDocumentRules(APIUsageExample target){
        ArrayList<String> result = new ArrayList<>();

        for(String API : target.getAPIs()) {
            HashMap<String,ArrayList<DocumentRule>> rulesForLabel = rules.get(API);
            if(rulesForLabel!=null){
                Set<MethodCallNode> nodeSet = target.getMethodCallNode(API);
                for (MethodCallNode node : nodeSet) {
                    ArrayList<DocumentRule> rules = rulesForLabel.get(node.getMethodSignature());
                    if(rules != null){
                        for (DocumentRule rule : rules) {
                            String validationRule = rule.ValidationRule(node);
                            if( validationRule != null ){
                                ArrayList<String> vios = violationFromDocumentCheck.get(target);
                                if(vios==null){
                                    vios = new ArrayList<>();
                                    vios.add(validationRule);
                                    violationFromDocumentCheck.put(target,vios);
                                }
                                else vios.add(validationRule);
                            }
                        }
                    }

                }
            }
        }
    }*/

    public boolean isNeighbor(Node node, String label){
        APIUsageGraph g = node.getGraph();
        for (Node node1 : g.incomingNodesOf(node)) {
            if(labelProvider.getLabel(node1).equals(label)) return true;
        }
        return false;
    }

}

