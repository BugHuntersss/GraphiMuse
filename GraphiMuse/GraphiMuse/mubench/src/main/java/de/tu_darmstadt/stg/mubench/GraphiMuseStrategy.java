package de.tu_darmstadt.stg.mubench;

import com.google.common.collect.Multiset;
import de.tu_darmstadt.stg.mubench.cli.DetectionStrategy;
import de.tu_darmstadt.stg.mubench.cli.DetectorArgs;
import de.tu_darmstadt.stg.mubench.cli.DetectorOutput;
import de.tu_darmstadt.stg.mudetect.PAMDetect;
import de.tu_darmstadt.stg.mudetect.aug.model.APIUsageExample;
import de.tu_darmstadt.stg.mudetect.aug.model.patterns.APIUsagePattern;
import edu.iastate.cs.mudetect.mining.AUGMiner;
import edu.iastate.cs.mudetect.mining.Model;
import de.tu_darmstadt.stg.mudetect.model.Violation;
import de.tu_darmstadt.stg.mudetect.overlapsfinder.AlternativeMappingsOverlapsFinder;
import de.tu_darmstadt.stg.mustudies.UsageUtils;
import de.tu_darmstadt.stg.yaml.YamlObject;
import edu.iastate.cs.egroum.aug.AUGBuilder;
import rule.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public abstract class PAMDStrategy implements DetectionStrategy {
    static int i = 0;

    protected abstract Collection<APIUsageExample> loadTrainingExamples(DetectorArgs args, DetectorOutput.Builder output) throws IOException;

    protected abstract AUGMiner createMiner();

    protected Collection<APIUsageExample> loadDetectionTargets(DetectorArgs args) throws IOException {
        return new AUGBuilder(new DefaultAUGConfiguration())
                .build(args.getTargetSrcPaths(), args.getDependencyClassPath());
    }

    protected abstract PAMDetect createDetector(Model model);

    /**
     * load document rules from data.txt
     * @return
     */
    public static HashMap<String, ArrayList<DocumentRule>> loadDocumentRule(){
        BufferedReader reader = null;
        HashMap<String,ArrayList<DocumentRule>> documentRules = new HashMap<>();
        try {
            reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/rules.txt"));
            String line = reader.readLine();
            int i = 0;
            while (line != null) {
                i++;
                if(i % 100 == 0)
                    System.out.println();
                DocumentRule rule = createDocumentRule(line);
                if(rule != null){
                    String label = rule.getClassName() + "." + rule.getMethodSignature();
                    documentRules.computeIfAbsent(label, k -> new ArrayList<>());
                    documentRules.get(label).add(rule);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return documentRules;
    }

    /**
     * generate DocumentRule according to a line in rules.txt
     * @param line
     * @return
     */
    public static DocumentRule createDocumentRule(String line){
        String[] split = line.split(";");
        String[] args = Arrays.stream(split)
                              .map(PAMDStrategy::processString)
                              .toArray(String[]::new);
        if(line.equals("[javax.crypto.Cipher.init();Cipher.init();try-catch;InvalidKeyException]"))
            System.out.println();
        if(args[2] == null) return null;

        switch(args[2]){
            case "precedes":
                if((args[1] + "()").split("\\.").length >= 2) return new PrecedeRule(args[0],args[1] + "()");
                else return null;
            case "try-catch":
                return new TryCatchRule(args[0],args[3]);
            case "!=":
                if(args[3].equals("null")){
                    if(args[1].matches("param.*")) return new NullCheckRule(args[0],args[1]);
                    else return null;
                }
            case ">":
            case "<":
            case ">=":
            case "<=":
                if(args[1].matches("param.*")) return new ValueCheckRule(args[0], args[1], args[3], args[2]);
            default :
                return null;
        }
    }

    /**
     * Process the string and remove the characters '[', ']', and "'" from it.
     * @param str
     * @return
     */
    public static String processString(String str){
        return str.replace("[","")
                .replace("]","")
                .replace("\'","")
                .replace("\"","");
    }

    public static void findMisuse(File dir, FileWriter output){
        for(File file : dir.listFiles()){
            if(file.isDirectory()){
                findMisuse(file,output);
            }
            else if(file.isFile()){
                String pattern = ".*misuse.yml.*";
                if(!Pattern.matches(pattern,file.getName())) continue;

                try{
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = null;
                    String ls = System.getProperty("line.separator");
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                        stringBuilder.append(ls);
                    }
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                    reader.close();

                    String content = stringBuilder.toString();
                    content = content.split("violations")[0];
                    content = content.split("-")[1];

                    output.write(i + ":");
                    output.write(ls);
                    output.write("  " + content);
                    i++;

                }catch (IOException e){
                    System.out.println(e);
                }

            }
        }
    }

    @Override
    public DetectorOutput detectViolations(DetectorArgs args, DetectorOutput.Builder output) throws Exception {
        long startTime = System.currentTimeMillis();
        Collection<APIUsageExample> trainingExamples = loadTrainingExamples(args, output);
        long endTrainingLoadTime = System.currentTimeMillis();
        output.withRunInfo("trainingLoadTime", endTrainingLoadTime - startTime);
        output.withRunInfo("numberOfTrainingExamples", trainingExamples.size());
        output.withRunInfo("numberOfUsagesInTrainingExamples", getTypeUsageCounts(trainingExamples));
        System.out.println("All AUGs have been converted");

        System.out.println("Begin to mine pattern");
        Model model = createMiner().mine(trainingExamples);
        long endTrainingTime = System.currentTimeMillis();
        output.withRunInfo("trainingTime", endTrainingTime - endTrainingLoadTime);
        output.withRunInfo("numberOfPatterns", model.getPatterns().size());
        output.withRunInfo("maxPatternSupport", model.getMaxPatternSupport());


        System.out.println("All patterns have been mined");

        System.out.println("Begin to load target");
        Collection<APIUsageExample> targets = loadDetectionTargets(args);
        long endDetectionLoadTime = System.currentTimeMillis();
        output.withRunInfo("detectionLoadTime", endDetectionLoadTime - endTrainingTime);
        output.withRunInfo("numberOfTargets", targets.size());
        System.out.println("Targets have been loaded");

        System.out.println("Begin to find violations");


        List<Violation> violations = createDetector(model).findViolations(targets);
        long endDetectionTime = System.currentTimeMillis();
        output.withRunInfo("detectionTime", endDetectionTime - endDetectionLoadTime);
        output.withRunInfo("numberOfViolations", violations.size());
        output.withRunInfo("numberOfExploredAlternatives", AlternativeMappingsOverlapsFinder.numberOfExploredAlternatives);
        System.out.println("program has stopped");

        return output.withFindings(violations, ViolationUtils::toFinding);
    }

    private boolean hasSubPattern(APIUsagePattern pattern , Model model){
        for (APIUsagePattern modelPattern : model.getPatterns()) {
            if(pattern.vertexSet().containsAll(modelPattern.vertexSet()) && pattern.edgeSet().containsAll(modelPattern.edgeSet()))
                return true;
        }
        return false;
    }


    private YamlObject getTypeUsageCounts(Collection<APIUsageExample> targets) {
        YamlObject object = new YamlObject();
        for (Multiset.Entry<String> entry : UsageUtils.countNumberOfUsagesPerType(targets).entrySet()) {
            object.put(entry.getElement(), entry.getCount());
        }
        return object;
    }


}
