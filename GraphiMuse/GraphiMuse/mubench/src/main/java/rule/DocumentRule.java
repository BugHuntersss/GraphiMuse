package rule;

import de.tu_darmstadt.stg.mudetect.aug.model.actions.MethodCallNode;
import de.tu_darmstadt.stg.mudetect.aug.model.patterns.APIUsagePattern;
import de.tu_darmstadt.stg.mudetect.model.Overlap;

import java.util.regex.*;

public interface DocumentRule {

    public String getAllName();

    public String getClassName();

    public String getMethodSignature();

    void modifyPattern(APIUsagePattern pattern, MethodCallNode node);

    APIUsagePattern genPattern();

}
