package rule;

import de.tu_darmstadt.stg.mudetect.aug.model.APIUsageGraph;
import de.tu_darmstadt.stg.mudetect.aug.model.Edge;
import de.tu_darmstadt.stg.mudetect.aug.model.Node;
import de.tu_darmstadt.stg.mudetect.aug.model.actions.CatchNode;
import de.tu_darmstadt.stg.mudetect.aug.model.actions.MethodCallNode;
import de.tu_darmstadt.stg.mudetect.aug.model.actions.NullCheckNode;
import de.tu_darmstadt.stg.mudetect.aug.model.controlflow.ThrowEdge;
import de.tu_darmstadt.stg.mudetect.aug.model.data.ExceptionNode;
import de.tu_darmstadt.stg.mudetect.aug.model.patterns.APIUsagePattern;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class TryCatchRule implements DocumentRule{
    private String allName;
    private String className;
    private String methodSignature;
    private String exception;
    private Set<Node> exceptionNode = new HashSet<>();
    public TryCatchRule(String apiName, String exception){
        this.allName = apiName;

        String[] splitName = apiName.split("\\.");
        for(int i = splitName.length - 1 ; i >= 0 ; i--){
            if(Pattern.matches(".*\\)" , splitName[i])){
                this.methodSignature = splitName[i];
                this.className = splitName[i - 1];
                break;
            }
        }
        this.exception = exception.trim();
    }

    public String getLabel(){
        return className + "." + methodSignature;
    }

    public String getAllName() {
        return allName;
    }

    public void setAllName(String allName) {
        this.allName = allName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    @Override
    public void modifyPattern(APIUsagePattern pattern, MethodCallNode node) {
        boolean hasCatchException = false;
        for (Node n : pattern.outgoingNodesOf(node)){
            if(n instanceof ExceptionNode && ((ExceptionNode) n).getType().equals(exception)){
                n.improveConfidence();
                hasCatchException = true;
                break;
            }
            else if(n instanceof ExceptionNode && !((ExceptionNode) n).getType().equals(exception)){
                pattern.removeVertex(n);
                Node exp = new ExceptionNode(exception, exception);
                pattern.addVertex(exp);
                Edge throwEdge = new ThrowEdge(node, exp);
                pattern.addEdge(node, exp, throwEdge);
                hasCatchException = true;
                exp.improveConfidence();
                break;
            }
        }
        if(!hasCatchException){
            Node exp = new ExceptionNode(exception, exception);
            pattern.addVertex(exp);
            Edge throwEdge = new ThrowEdge(node, exp);
            pattern.addEdge(node, exp, throwEdge);
            exp.improveConfidence();
        }


    }

    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    @Override
    public APIUsagePattern genPattern(){return null;}
}
