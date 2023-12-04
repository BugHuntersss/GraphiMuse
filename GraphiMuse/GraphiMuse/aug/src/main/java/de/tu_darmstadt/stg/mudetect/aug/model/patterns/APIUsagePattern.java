package de.tu_darmstadt.stg.mudetect.aug.model.patterns;

import de.tu_darmstadt.stg.mudetect.aug.model.APIUsageGraph;
import de.tu_darmstadt.stg.mudetect.aug.model.Location;
import de.tu_darmstadt.stg.mudetect.aug.model.Node;
import de.tu_darmstadt.stg.mudetect.aug.model.util.UnionCondition;

import java.util.*;


public class APIUsagePattern extends APIUsageGraph {
    private final int support;
    private final Set<Location> exampleLocations;
    private final HashMap<String , ArrayList<String>> abstractNodeListForMethodCall;
    public HashMap<String , Integer> APIFrequency;
    public boolean beModified = false;
    public HashMap<Node , Map<UnionCondition, Double>> bayesMatrix = new HashMap<>();
    public HashMap<Node , List<Node>> inN = new HashMap<>();


    public APIUsagePattern(int support, Set<Location> exampleLocations, HashMap<String,ArrayList<String>> abstractNodeListForMethodCall
            , HashMap<String,Integer> APIFrequency) {
        this.support = support;
        this.exampleLocations = exampleLocations;
        this.abstractNodeListForMethodCall = abstractNodeListForMethodCall;
        this.APIFrequency = APIFrequency;
    }

    public int getSupport() {
        return support;
    }

    public double getAccuracy(){
        int max = 0;
        for(String api : APIFrequency.keySet()) {
            if(APIFrequency.get(api) > max) max = APIFrequency.get(api);
        }
        return (double) support / (double) max ;
    }

    public double getProbability(String api){
        int max = 0;
        for(String api_name : APIFrequency.keySet()) {
            if(APIFrequency.get(api_name) > max) max = APIFrequency.get(api_name);
        }
        return (double) APIFrequency.get(api) / (double) max ;
    }

    public Set<Location> getExampleLocations() {
        return exampleLocations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        APIUsagePattern pattern = (APIUsagePattern) o;
        return support == pattern.support;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), support);
    }

    public HashMap<String, ArrayList<String>> getAbstractNodeListForMethodCall() {
        return abstractNodeListForMethodCall;
    }

    public boolean contains(APIUsagePattern pattern){
        return this.vertexLabelSet().containsAll(pattern.vertexLabelSet());
    }

    public void addMatrix(Node node, Map<UnionCondition, Double> prob){
        this.bayesMatrix.put(node, prob);

    }


}
