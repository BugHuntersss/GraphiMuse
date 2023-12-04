package de.tu_darmstadt.stg.mudetect.aug.model.actions;

import de.tu_darmstadt.stg.mudetect.aug.model.ActionNode;
import de.tu_darmstadt.stg.mudetect.aug.model.BaseNode;
import de.tu_darmstadt.stg.mudetect.aug.visitors.NodeVisitor;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MethodCallNode extends BaseNode implements ActionNode {
    private final String declaringTypeName;
    private final String methodSignature;
    private final String returnType;
    private final List<String> parameters;


    public MethodCallNode(String declaringTypeName, String methodSignature, String returnType, List<String> parameters) {
        this.declaringTypeName = declaringTypeName;
        this.methodSignature = methodSignature;
        this.returnType = returnType;
        this.parameters = parameters;
    }

    public MethodCallNode(String declaringTypeName, String methodSignature, int sourceLineNumber, String returnType, List<String> parameters) {
        super(sourceLineNumber);
        this.declaringTypeName = declaringTypeName;
        this.methodSignature = methodSignature;
        this.returnType = returnType;
        this.parameters = parameters;
    }

    public MethodCallNode(String declaringTypeName, String methodSignature, List<String> parameters) {
        this.declaringTypeName = declaringTypeName;
        this.methodSignature = methodSignature;
        this.returnType = null;
        this.parameters = parameters;
    }

    public MethodCallNode(String declaringTypeName, String methodSignature, int sourceLineNumber, List<String> parameters) {
        super(sourceLineNumber);
        this.declaringTypeName = declaringTypeName;
        this.methodSignature = methodSignature;
        this.returnType = null;
        this.parameters = parameters;
    }


    @Override
    public boolean isCoreAction() {
        return !getMethodSignature().startsWith("get");
    }

    @Override
    public Optional<String> getAPI() {
        String declaringType = getDeclaringTypeName();
        if (!declaringType.isEmpty() && !declaringType.endsWith("[]"))
            return Optional.of(declaringType);
        else
            return Optional.empty();
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public String getDeclaringTypeName() {
        return declaringTypeName;
    }

    @Override
    public <R> R apply(NodeVisitor<R> visitor) {
        return visitor.visit(this);
    }



    public String getReturnType() {
        return returnType;
    }

    public List<String> getParameters() {
        return parameters;
    }

}
