package rule;

import de.tu_darmstadt.stg.mudetect.aug.model.actions.MethodCallNode;
import de.tu_darmstadt.stg.mudetect.aug.model.patterns.APIUsagePattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValueCheckRule implements DocumentRule{
    private String allName;
    private String className;
    private String methodSignature;
    private String value;
    private String variableType;
    private String relation;
    public ValueCheckRule(String apiName, String paramPosition, String value, String relation){
        this.allName = apiName;
        int paramPos = Integer.parseInt(paramPosition.split(" ")[1]);
        ArrayList<String> parameters = new ArrayList<>();

        String[] splitName = apiName.split("\\.");
        for(int i = splitName.length - 1 ; i >= 0 ; i--){
            if(Pattern.matches(".*\\)" , splitName[i])){
                String[] strings = splitName[i].split("\\(");
                if( strings.length > 1 ) parameters = new ArrayList<>(Arrays.asList(splitName[i].split("\\(")[1].split("\\)")[0].split(",")));
                String regex = "\\(.*\\)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(splitName[i]);
                if(matcher.find()){
                    splitName[i] = matcher.replaceFirst("()");
                }
                this.methodSignature = splitName[i];
                this.className = splitName[i - 1];
                break;
            }
        }

        if(parameters.size() >=  paramPos){
            this.variableType = parameters.get(paramPos - 1);
        }

        this.value = value;
        this.relation = relation.trim();
    }

    public String getLabel(){
        return className + "." + methodSignature;
    }

    @Override
    public String getAllName() {
        return allName;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getMethodSignature() {
        return methodSignature;
    }

    @Override
    public void modifyPattern(APIUsagePattern pattern, MethodCallNode node) {
        return ;
    }


    public String getValue() {
        return value;
    }

    @Override
    public APIUsagePattern genPattern(){return null;}
}
