package de.tu_darmstadt.stg.mudetect.matcher;

import de.tu_darmstadt.stg.mudetect.aug.model.DataNode;
import de.tu_darmstadt.stg.mudetect.aug.model.Node;
import de.tu_darmstadt.stg.mudetect.aug.model.patterns.APIUsagePattern;
import de.tu_darmstadt.stg.mudetect.typehierarchy.TypeHierarchy;

public class SubtypeDataNodeMatcher implements NodeMatcher {

    private TypeHierarchy typeHierarchy;

    public SubtypeDataNodeMatcher(TypeHierarchy typeHierarchy) {
        this.typeHierarchy = typeHierarchy;
    }

    @Override
    public boolean test(Node targetNode, Node patternNode) {
        return targetNode instanceof DataNode && patternNode instanceof DataNode
                && typeHierarchy.isA(((DataNode) targetNode).getType(), ((DataNode) patternNode).getType());
    }

    public boolean test(Node node1, Node node2, APIUsagePattern pattern){
        return false;
    }
}
