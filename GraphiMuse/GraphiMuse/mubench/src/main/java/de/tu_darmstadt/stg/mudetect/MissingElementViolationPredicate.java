package de.tu_darmstadt.stg.mudetect;

import de.tu_darmstadt.stg.mudetect.aug.model.ActionNode;
import de.tu_darmstadt.stg.mudetect.aug.model.Edge;
import de.tu_darmstadt.stg.mudetect.aug.model.Node;
import de.tu_darmstadt.stg.mudetect.aug.model.actions.CatchNode;
import de.tu_darmstadt.stg.mudetect.aug.model.controlflow.OrderEdge;
import de.tu_darmstadt.stg.mudetect.aug.model.controlflow.RepetitionEdge;
import de.tu_darmstadt.stg.mudetect.aug.model.controlflow.ThrowEdge;
import de.tu_darmstadt.stg.mudetect.aug.model.patterns.APIUsagePattern;
import de.tu_darmstadt.stg.mudetect.model.Overlap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MissingElementViolationPredicate implements ViolationPredicate {
    @Override
    public Optional<Boolean> apply(Overlap overlap) {
        return isMissingElement(overlap) ? Optional.of(true) : Optional.empty();
    }

    private boolean isMissingElement(Overlap overlap) {
        APIUsagePattern pattern = overlap.getPattern();

        Set<Node> missingNodes = overlap.getMissingNodes();
        Set<Edge> missingEdges = overlap.getMissingEdges();

        if(overlap.getNodeSize() == pattern.getNodeSize() && overlap.getEdgeSize() == pattern.getEdgeSize())
            return false;

        Iterator<Node> nodeIterator = missingNodes.iterator();
        while(nodeIterator.hasNext()){
            Node missingNode = nodeIterator.next();
            Set<Edge> incomingEdges = new HashSet<>(pattern.incomingEdgesOf(missingNode));
            boolean flag = false;
            for (Edge incomingEdge : incomingEdges) {
                if(incomingEdge instanceof OrderEdge && ((OrderEdge) incomingEdge).isPrecede){
                    flag = true;
                    break;
                }
            }
            if(flag){
                missingEdges.removeIf(edge -> edge.getTarget() == missingNode);
                nodeIterator.remove();
            }
        }

        Iterator<Edge> edgeIterator = missingEdges.iterator();
        while(edgeIterator.hasNext()){
            Edge edge = edgeIterator.next();
            if(edge instanceof OrderEdge && ((OrderEdge) edge).isPrecede()
                    || edge instanceof RepetitionEdge){
                edgeIterator.remove();
            }
        }

        return missingNodes.size() != 0;
    }

}
