package de.tu_darmstadt.stg.mubench;

import de.tu_darmstadt.stg.mubench.cli.DetectorArgs;
import de.tu_darmstadt.stg.mubench.cli.DetectorOutput;
import de.tu_darmstadt.stg.mudetect.*;
import de.tu_darmstadt.stg.mudetect.aug.model.APIUsageExample;
import de.tu_darmstadt.stg.mudetect.overlapsfinder.AlternativeMappingsOverlapsFinder;
import de.tu_darmstadt.stg.mudetect.ranking.ProbabilisticFunction;
import de.tu_darmstadt.stg.mudetect.ranking.ProductWeightFunction;
import de.tu_darmstadt.stg.mudetect.ranking.WeightRankingStrategy;
import edu.iastate.cs.egroum.aug.AUGBuilder;
import edu.iastate.cs.mudetect.mining.*;

import java.io.IOException;
import java.util.Collection;

public class SingleApiStrategy  extends PAMDStrategy{
    final String api;

    public SingleApiStrategy(String api){
        this.api = api;
    }

    protected Collection<APIUsageExample> loadTrainingExamples(DetectorArgs args, DetectorOutput.Builder output) throws IOException {
        return new AUGBuilder(new DefaultAUGConfiguration())
                .build(args.getTargetSrcPaths(), args.getDependencyClassPath());
    }

    @Override
    protected AUGMiner createMiner() {
        return new SingleAPIMiner(new DefaultMiningConfiguration(), api);
    }

    @Override
    protected PAMDetect createDetector(Model model) {
        return new PAMDetect(
                new MinPatternActionsModel(model, 2),
                new AlternativeMappingsOverlapsFinder(new DefaultOverlapFinderConfig(new DefaultMiningConfiguration())),
                new FirstDecisionViolationPredicate(
                        new MissingDefPrefixNoViolationPredicate(),
                        new OnlyDefPrefixNoViolationPredicate(),
                        new MissingCatchNoViolationPredicate(),
                        new MissingAssignmentNoViolationPredicate(),
                        new MissingElementViolationPredicate()),
                new DefaultFilterAndRankingStrategy(new WeightRankingStrategy(
                        new ProductWeightFunction(
                                new ProbabilisticFunction()
                        ))));
    }
}
