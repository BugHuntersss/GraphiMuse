package edu.iastate.cs.mudetect.mining;

import de.tu_darmstadt.stg.mudetect.aug.model.APIUsageExample;
import de.tu_darmstadt.stg.mudetect.aug.model.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Augs {
    private ArrayList<APIUsageExample> AUGs;
    private HashMap<String, HashSet<Node>> nodesOfLabel;
    private HashMap<String, HashSet<Node>> nodesOfAllLabel;

    private Augs(ArrayList<APIUsageExample> AUGs, HashMap<String, HashSet<Node>> nodesOfLabel, HashMap<String, HashSet<Node>> nodesOfAllLabel) {
        this.AUGs = AUGs;
        this.nodesOfLabel = nodesOfLabel;
        this.nodesOfAllLabel = nodesOfAllLabel;
    }
    private static Augs augs;
    public static Augs getInstance(ArrayList<APIUsageExample> AUGs, HashMap<String, HashSet<Node>> nodesOfLabel, HashMap<String, HashSet<Node>> nodesOfAllLabel) {
        if (augs == null) {
            augs = new Augs(AUGs, nodesOfLabel,  nodesOfAllLabel);
        }
        else{
            if(augs.AUGs.equals(AUGs)) {
                return augs;
            }
            else{
                augs.AUGs.addAll(AUGs);
                nodesOfLabel.forEach(
                    (key, newSet) -> augs.nodesOfLabel.merge(key, newSet, (oldSet, newSet2)->
                        {
                            oldSet.addAll(newSet2);
                            return oldSet;
                        }));
                nodesOfAllLabel.forEach(
                    (key, newSet) -> augs.nodesOfAllLabel.merge(key, newSet, (oldSet, newSet2)->
                        {
                            oldSet.addAll(newSet2);
                            return oldSet;
                        }));
            }
        }
        return augs;
    }

    public static Augs getInstance() {
        if(augs != null) return augs;
        return null;
    }

    public ArrayList<APIUsageExample> getAUGs() {
        return AUGs;
    }

    public void setAUGs(ArrayList<APIUsageExample> AUGs) {
        this.AUGs = AUGs;
    }

    public HashMap<String, HashSet<Node>> getNodesOfLabel() {
        return nodesOfLabel;
    }

    public void setNodesOfLabel(HashMap<String, HashSet<Node>> nodesOfLabel) {
        this.nodesOfLabel = nodesOfLabel;
    }

    public HashMap<String, HashSet<Node>> getNodesOfAllLabel() {
        return nodesOfAllLabel;
    }

    public void setNodesOfAllLabel(HashMap<String, HashSet<Node>> nodesOfAllLabel) {
        this.nodesOfAllLabel = nodesOfAllLabel;
    }
}
