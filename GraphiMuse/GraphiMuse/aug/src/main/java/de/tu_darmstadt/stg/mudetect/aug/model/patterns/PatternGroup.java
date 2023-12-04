package de.tu_darmstadt.stg.mudetect.aug.model.patterns;

import de.tu_darmstadt.stg.mudetect.aug.visitors.AUGLabelProvider;

import java.util.HashSet;
import java.util.Set;

public class PatternGroup {
    private String baseAPI;
    private Set<APIUsagePattern> patternSet = new HashSet<>();
    public AUGLabelProvider labelProvider;

    public PatternGroup(String api){
        this.baseAPI = api;
    }

    public boolean applyToAttend(APIUsagePattern newPattern){
        if(!patternSet.contains(newPattern) && newPattern.getMethodCalls().contains(baseAPI)){
            patternSet.add(newPattern);
            return true;
        }

        return false;
    }

    public String getBaseAPI(){
        return baseAPI;
    }

    public Set<APIUsagePattern> getPatternSet() {
        return patternSet;
    }
}
