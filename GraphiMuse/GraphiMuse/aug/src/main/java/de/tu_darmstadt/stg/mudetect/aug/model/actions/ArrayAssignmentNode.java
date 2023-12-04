package de.tu_darmstadt.stg.mudetect.aug.model.actions;

import de.tu_darmstadt.stg.mudetect.aug.visitors.NodeVisitor;

import java.util.ArrayList;

public class ArrayAssignmentNode extends MethodCallNode {
    public ArrayAssignmentNode(String arrayTypeName) {
        super(arrayTypeName, "arrayset()", new ArrayList<>());
    }

    public ArrayAssignmentNode(String arrayTypeName, int sourceLineNumber) {
        super(arrayTypeName, "arrayset()", sourceLineNumber, new ArrayList<>());
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
