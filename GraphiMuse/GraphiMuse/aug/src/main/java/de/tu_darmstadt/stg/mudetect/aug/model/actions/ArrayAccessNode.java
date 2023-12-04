package de.tu_darmstadt.stg.mudetect.aug.model.actions;

import de.tu_darmstadt.stg.mudetect.aug.visitors.NodeVisitor;

import java.util.ArrayList;

public class ArrayAccessNode extends MethodCallNode {
    public ArrayAccessNode(String arrayTypeName) {
        super(arrayTypeName, "arrayget()", new ArrayList<>());
    }

    public ArrayAccessNode(String arrayTypeName, int sourceLineNumber) {
        super(arrayTypeName, "arrayget()", sourceLineNumber, new ArrayList<>());
    }

    @Override
    public boolean isCoreAction() {
        return false;
    }

    @Override
    public <R> R apply(NodeVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
