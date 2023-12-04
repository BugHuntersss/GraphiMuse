package rule;

import de.tu_darmstadt.stg.mudetect.aug.model.APIUsageGraph;
import de.tu_darmstadt.stg.mudetect.aug.model.DataNode;
import de.tu_darmstadt.stg.mudetect.aug.model.Edge;
import de.tu_darmstadt.stg.mudetect.aug.model.Node;
import de.tu_darmstadt.stg.mudetect.aug.model.actions.MethodCallNode;
import de.tu_darmstadt.stg.mudetect.aug.model.actions.NullCheckNode;
import de.tu_darmstadt.stg.mudetect.aug.model.controlflow.OrderEdge;
import de.tu_darmstadt.stg.mudetect.aug.model.data.VariableNode;
import de.tu_darmstadt.stg.mudetect.aug.model.dataflow.ParameterEdge;
import de.tu_darmstadt.stg.mudetect.aug.model.patterns.APIUsagePattern;
import de.tu_darmstadt.stg.mudetect.aug.model.patterns.AggregateDataNode;
import de.tu_darmstadt.stg.mudetect.aug.visitors.EdgeVisitor;
import jdk.internal.org.objectweb.asm.tree.ParameterNode;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NullCheckRule implements DocumentRule{
    private String allName;
    private String className;
    private String methodSignature;
    private String variableType;
    private List<String> parameters;
    public NullCheckRule(String apiName, String paramPosition){
        this.allName = apiName;
        String[] param = paramPosition.split(" ");
        if(param.length < 2)
            System.out.println();
        int paramPos = Integer.parseInt(paramPosition.split(" ")[1]);
        ArrayList<String> parameters = new ArrayList<>();

        String[] splitName = apiName.split("\\.");
        for(int i = splitName.length - 1 ; i >= 0 ; i--){
            if(Pattern.matches(".*\\)" , splitName[i])){
                String[] strings = splitName[i].split("\\(");
                if( strings.length > 1 ) parameters = new ArrayList<>(Arrays.asList(splitName[i].split("\\(")[1].split("\\)")[0].split(",")));
                String regex = "\\(.*\\)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(splitName[i]);
                if(matcher.find()){
                    splitName[i] = matcher.replaceFirst("()");
                }
                this.methodSignature = splitName[i];
                this.className = splitName[i - 1];
                break;
            }
        }
        if(parameters.size() >=  paramPos){
            this.variableType = parameters.get(paramPos - 1);
        }
        this.parameters = parameters;
    }

    public String getLabel(){
        return className + "." + methodSignature;
    }
    @Override
    public String getAllName() {
        return allName;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getMethodSignature() {
        return methodSignature;
    }

    @Override
    /**
     * modify the pattern which is mined from data, according to the rule extracted from documents.
     */
    public void modifyPattern(APIUsagePattern pattern, MethodCallNode node) {
        boolean paraExists = false;
        for (Node incomingNode : pattern.incomingNodesOf(node)) {
            if(incomingNode instanceof DataNode && ((DataNode) incomingNode).getType().equals(variableType)){
                boolean nullCheckExists =  false;
                for (Node node1 : pattern.outgoingNodesOf(incomingNode)) {
                    if(node1 instanceof NullCheckNode){
                        node1.improveConfidence();
                        nullCheckExists = true;
                        paraExists = true;
                        break;
                    }
                }
                if(!nullCheckExists){
                    NullCheckNode nullCheckNode = new NullCheckNode();
                    pattern.addVertex(nullCheckNode);
                    Edge paraEdge = new ParameterEdge(incomingNode, nullCheckNode);
                    pattern.addEdge(incomingNode, nullCheckNode, paraEdge);
                    Edge orderEdge = new OrderEdge(nullCheckNode, node);
                    pattern.addEdge(nullCheckNode, node, orderEdge);
                    nullCheckNode.improveConfidence();
                    paraExists = true;
                }
                break;
            }
        }
        if(!paraExists){
            Node paraNode = new VariableNode(this.variableType, this.variableType);
            NullCheckNode nullCheckNode = new NullCheckNode();
            pattern.addVertex(paraNode);
            pattern.addVertex(nullCheckNode);
            Edge paraToNullCheck = new ParameterEdge(paraNode, nullCheckNode);
            Edge paraToNode = new ParameterEdge(paraNode, node);
            Edge orderEdge = new OrderEdge(nullCheckNode, node);
            pattern.addEdge(paraNode, nullCheckNode, paraToNullCheck);
            pattern.addEdge(paraNode, node, paraToNode);
            pattern.addEdge(nullCheckNode, node, orderEdge);
            nullCheckNode.improveConfidence();
        }

    }

    public String getVariableType() {
        return variableType;
    }

    @Override
    public APIUsagePattern genPattern(){
        APIUsagePattern pattern = new APIUsagePattern(0, null, null, null);
        Node paraNode = new VariableNode(this.variableType, this.variableType);
        NullCheckNode nullCheckNode = new NullCheckNode();
        MethodCallNode methodCallNode = new MethodCallNode(className, methodSignature, parameters);
        pattern.addVertex(paraNode);
        pattern.addVertex(nullCheckNode);
        pattern.addVertex(methodCallNode);
        Edge orderEdge = new OrderEdge(nullCheckNode, methodCallNode);
        Edge paraToNullCheck = new ParameterEdge(paraNode, nullCheckNode);
        Edge paraToNode = new ParameterEdge(paraNode, methodCallNode);
        pattern.addEdge(paraNode,nullCheckNode,paraToNullCheck);
        pattern.addEdge(paraNode,methodCallNode,paraToNode);
        pattern.addEdge(nullCheckNode,methodCallNode,orderEdge);
        return pattern;
    }

}
