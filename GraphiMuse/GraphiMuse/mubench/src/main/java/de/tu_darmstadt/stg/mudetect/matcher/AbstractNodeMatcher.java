package de.tu_darmstadt.stg.mudetect.matcher;

import de.tu_darmstadt.stg.mudetect.aug.model.Node;
import de.tu_darmstadt.stg.mudetect.aug.model.patterns.APIUsagePattern;
import de.tu_darmstadt.stg.mudetect.aug.visitors.AUGLabelProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class AbstractNodeMatcher implements NodeMatcher {
    private final Function<Node, String> getLabel;

    public AbstractNodeMatcher(AUGLabelProvider labelProvider) {
        this.getLabel = labelProvider::getLabel;
    }

    @Override
    public boolean test(Node targetNode, Node patternNode) {
        return false;
    }
}
