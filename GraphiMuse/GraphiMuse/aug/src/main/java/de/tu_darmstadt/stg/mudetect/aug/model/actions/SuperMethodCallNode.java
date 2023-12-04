package de.tu_darmstadt.stg.mudetect.aug.model.actions;

import de.tu_darmstadt.stg.mudetect.aug.visitors.NodeVisitor;

import java.util.ArrayList;
import java.util.List;

public class SuperMethodCallNode extends MethodCallNode {

    public SuperMethodCallNode(String declaringTypeName, String methodSignature, String returnType, List<String> parameters) {
        super(declaringTypeName, methodSignature, returnType, parameters);
    }

    public SuperMethodCallNode(String declaringTypeName, String methodSignature, int sourceLineNumber, String returnType, List<String> parameters) {
        super(declaringTypeName, methodSignature, sourceLineNumber, returnType, parameters);
    }

    @Override
    public <R> R apply(NodeVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
