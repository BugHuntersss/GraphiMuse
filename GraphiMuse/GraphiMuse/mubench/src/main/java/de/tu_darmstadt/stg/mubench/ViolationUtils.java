package de.tu_darmstadt.stg.mubench;

import de.tu_darmstadt.stg.mubench.cli.DetectorFinding;
import de.tu_darmstadt.stg.mudetect.aug.model.APIUsageExample;
import de.tu_darmstadt.stg.mudetect.aug.model.Location;
import de.tu_darmstadt.stg.mudetect.dot.ViolationDotExporter;
import de.tu_darmstadt.stg.mudetect.model.Violation;
import de.tu_darmstadt.stg.mudetect.PAMDetect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ViolationUtils {
    private static final ViolationDotExporter violationDotExporter = new ViolationDotExporter();
    public static final String CHECKOUTS_PATH_SUFFIX = "checkouts/";
    public static final int UNKNOWN_LINE = -1;

    public static DetectorFinding toFinding(Violation violation) {
        if(violation.getOverlap() != null){
            Location location = violation.getLocation();
            DetectorFinding finding = new DetectorFinding(location.getFilePath(), location.getMethodSignature());
            finding.put("pattern_violation", violationDotExporter.toDotGraph(violation));
            finding.put("target_environment_mapping", violationDotExporter.toTargetEnvironmentDotGraph(violation));
            finding.put("confidence", violation.getConfidence());
            finding.put("pattern_support", violation.getOverlap().getPattern().getSupport());
            finding.put("probability", violation.getOverlap().probability);
            finding.put("pattern_examples", getPatternInstanceLocations(violation));
            finding.put("startline", getStartLine(violation));
            finding.put("violation_from_doc" , getViolationFromDocument(violation.getOverlap().getTarget()));
            return finding;
        }
        Location location = violation.getTarget().getLocation();
        DetectorFinding finding = new DetectorFinding(location.getFilePath(), location.getMethodSignature());
        finding.put("violation_from_doc", violation.violations);

        return finding;

    }

    private static Set<String> getPatternInstanceLocations(Violation violation) {
        return violation.getOverlap().getPattern().getExampleLocations().stream()
                .map(ViolationUtils::getLocationString).distinct().limit(5)
                .collect(Collectors.toSet());
    }

    private static Set<String> getViolationFromDocument(APIUsageExample target){
        ArrayList<String> result = PAMDetect.violationFromDocumentCheck.get(target);
        Set<String> ret = new HashSet<>();
        if(result == null) return ret;
        for(String str : result){
            ret.add(str);
        }
        return ret;
    }

    private static String getLocationString(Location loc) {
        String filePath = loc.getFilePath();
        int startOfCheckoutsSubPath = filePath.indexOf(CHECKOUTS_PATH_SUFFIX);
        if (startOfCheckoutsSubPath > -1) {
            startOfCheckoutsSubPath += CHECKOUTS_PATH_SUFFIX.length() - 1;
        }
        return filePath.substring(startOfCheckoutsSubPath + 1) + "#" + loc.getMethodSignature();
    }

    private static int getStartLine(Violation violation) {
        APIUsageExample target = violation.getOverlap().getTarget();
        int startLine = violation.getOverlap().getMappedTargetNodes().stream()
                .mapToInt(node -> target.getSourceLineNumber(node).orElse(Integer.MAX_VALUE))
                .min().orElse(UNKNOWN_LINE);
        return (startLine == Integer.MAX_VALUE) ? UNKNOWN_LINE : startLine;
    }

}
