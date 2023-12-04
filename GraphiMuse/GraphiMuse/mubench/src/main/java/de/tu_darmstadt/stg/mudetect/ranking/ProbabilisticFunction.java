package de.tu_darmstadt.stg.mudetect.ranking;

import de.tu_darmstadt.stg.mudetect.model.Overlap;
import de.tu_darmstadt.stg.mudetect.model.Overlaps;
import edu.iastate.cs.mudetect.mining.Model;

public class ProbabilisticFunction implements ViolationWeightFunction{
    @Override
    public double getWeight(Overlap violation, Overlaps overlaps, Model model) {
        return violation.probability;
    }

    @Override
    public String getFormula(Overlap violation, Overlaps overlaps, Model model) {
        return "";
    }

    @Override
    public String getId() {
        return "probabilistic";
    }
}
