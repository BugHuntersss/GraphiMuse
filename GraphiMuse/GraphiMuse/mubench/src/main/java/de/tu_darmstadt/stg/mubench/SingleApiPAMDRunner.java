package de.tu_darmstadt.stg.mubench;

import de.tu_darmstadt.stg.mubench.cli.MuBenchRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SingleApiPAMDRunner {
    public static void main(String[] args) throws Exception {
        long xmsMemory = Runtime.getRuntime().totalMemory()/1024/1024;
        long xmxMemory = Runtime.getRuntime().maxMemory()/1024/1024;
        System.out.println("-Xms"+xmsMemory+"M");
        System.out.println("-Xmx"+xmxMemory+"M");
        String api = null;
        String[] new_args = new String[0];
        for(int i = 0; i < args.length; i += 2) {
            String arg = args[i];
            String next_arg = args[i + 1];
            if(arg.equals("api_name")){
                api = next_arg;
                List<String> argsList = new ArrayList<>(Arrays.asList(args));
                argsList.remove("api_name");
                argsList.remove(api);
                new_args = argsList.toArray(new String[0]);
            }
        }
        if(api == null){
            throw new RuntimeException("Lack of api name");
        }
        new MuBenchRunner()
                .withDetectOnlyStrategy(new ProvidedPatternsStrategy())
                .withMineAndDetectStrategy(new SingleApiStrategy(api))
                .run(new_args);
        System.out.println("process stop");

    }
}
