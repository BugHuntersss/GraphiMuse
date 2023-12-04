package rule;

import de.tu_darmstadt.stg.mudetect.aug.model.APIUsageGraph;
import de.tu_darmstadt.stg.mudetect.aug.model.Edge;
import de.tu_darmstadt.stg.mudetect.aug.model.Node;
import de.tu_darmstadt.stg.mudetect.aug.model.actions.MethodCallNode;
import de.tu_darmstadt.stg.mudetect.aug.model.actions.NullCheckNode;
import de.tu_darmstadt.stg.mudetect.aug.model.controlflow.OrderEdge;
import de.tu_darmstadt.stg.mudetect.aug.model.controlflow.ThrowEdge;
import de.tu_darmstadt.stg.mudetect.aug.model.data.VariableNode;
import de.tu_darmstadt.stg.mudetect.aug.model.dataflow.ParameterEdge;
import de.tu_darmstadt.stg.mudetect.aug.model.patterns.APIUsagePattern;
import de.tu_darmstadt.stg.mudetect.aug.visitors.AUGLabelProvider;
import de.tu_darmstadt.stg.mudetect.aug.visitors.BaseAUGLabelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PrecedeRule implements DocumentRule{
    private String allName;
    private String className;
    private String methodSignature;
    private String subject;
    private String subClassName;
    private String subMethodSignature;
    public AUGLabelProvider labelProvider = new BaseAUGLabelProvider();
    public PrecedeRule(String apiName, String subject){
        this.allName = apiName;

        String[] splitName = apiName.split("\\.");
        for(int i = splitName.length - 1 ; i >= 0 ; i--){
            if(Pattern.matches(".*\\)" , splitName[i])){
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

        this.subject = subject;
        String[] s = subject.split("\\.");
        if(s.length >= 2){
            String temp = s[s.length - 1];
            this.subClassName = s[s.length - 2];
            this.subMethodSignature = temp.split("\\(")[0] + "()";
        }


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
    public void modifyPattern(APIUsagePattern pattern, MethodCallNode node) {
        boolean conditionExists = false;
        for (Node n : pattern.incomingNodesOf(node)) {
            if(n instanceof MethodCallNode && ((MethodCallNode) n).getMethodSignature().equals(subMethodSignature)){
                n.improveConfidence();
                conditionExists = true;
                break;
            }
        }

        if(!conditionExists){
            MethodCallNode node1 = new MethodCallNode(this.subClassName, this.subMethodSignature, null, null);
            pattern.addVertex(node1);
            Edge edge = new OrderEdge(node1, node);
            pattern.addEdge(node1, node, edge);
            node1.improveConfidence();
        }
        pattern.beModified = true;
    }

    public String getSubject() {
        return subject;
    }

    @Override
    public APIUsagePattern genPattern(){
        APIUsagePattern pattern = new APIUsagePattern(0, null, null, null);
        MethodCallNode node1 = new MethodCallNode(this.subClassName, this.subMethodSignature, null, null);
        pattern.addVertex(node1);
        MethodCallNode node2 = new MethodCallNode(this.className, this.methodSignature, null, null);
        pattern.addVertex(node2);

        Edge orderEdge = new OrderEdge(node1, node2);

        pattern.addEdge(node1,node2,orderEdge);
        return pattern;
    }

}
