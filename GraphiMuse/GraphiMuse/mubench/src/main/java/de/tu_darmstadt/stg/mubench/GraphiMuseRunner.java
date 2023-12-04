package de.tu_darmstadt.stg.mubench;

import de.tu_darmstadt.stg.mubench.cli.MuBenchRunner;;

public class PAMDRunner {
    public static void main(String[] args) throws Exception {
        long xmsMemory = Runtime.getRuntime().totalMemory()/1024/1024;
        long xmxMemory = Runtime.getRuntime().maxMemory()/1024/1024;
        System.out.println("-Xms"+xmsMemory+"M");
        System.out.println("-Xmx"+xmxMemory+"M");
        new MuBenchRunner()
                .withDetectOnlyStrategy(new ProvidedPatternsStrategy())
                .withMineAndDetectStrategy(new IntraProjectStrategy())
                .run(args);
        System.out.println("process stop");

    }


}
