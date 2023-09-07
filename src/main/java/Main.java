package main.java;

import main.java.utils.Logger;
import main.java.utils.Tuple3;
import main.java.variables.AbcVars;

import java.util.BitSet;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static main.java.config.Parameters.MAX_CYCLE;
import static main.java.config.Parameters.RUNTIME;

public class Main {

    static String[] fileNames = {
            "scp41.txt"
//            "scpnrf5.txt",
//            "scpnrg3.txt",
//            "scpnrg4.txt",
//            "scpnrh2.txt"
    };
    static Logger logger = new Logger();

    public static void main(String[] args) {
        logger.log("Variant of the Artificial Bee Colony Algorithm ABC_SCP to solve the Set Covering Problem");
        logger.log("University of Cauca, 2023");

        runABCSCPMonoThread();
        //runABCSCPMultiThread();

        logger.log("Algorithm has finished!");
    }

    public static void runABCSCPMultiThread() {
        for (String fileName : fileNames) {
            try {
                Problem.read("src/main/resources/" + fileName);
                // Problem.read("main/resources/" + fileName);
                logger.log("Problem processing [" + fileName + "] has started!");
                logger.log();

                ForkJoinPool forkJoinPool = new ForkJoinPool(RUNTIME);
                List<ForkJoinTask<Tuple3<Integer, Integer, BitSet>>> results =
                        IntStream.range(0, RUNTIME)
                                .sorted()
                                .mapToObj(rIndex -> forkJoinPool.submit(() -> {

                                    AbcVars vr = new AbcVars();
                                    BeeColony bee = new BeeColony(vr);
                                    bee.initial();
                                    bee.memorizeBestSource();
                                    for (int iter = 0; iter < MAX_CYCLE; iter++) {
                                        bee.sendEmployedBees();
                                        bee.calculateProbabilitiesOne();
                                        bee.sendOnlookerBees();
                                        bee.memorizeBestSource();
                                        bee.sendScoutBees();
                                        logger.addProgress(rIndex);
                                        logger.setGlobalMin(rIndex, vr.getGLOBAL_MIN());
                                    }
                                    vr.addGlobalMin(vr.getGLOBAL_MIN());
                                    return new Tuple3<>(rIndex, vr.getGLOBAL_MIN(), vr.getGLOBAL_PARAMS());

                                }))
                                .collect(Collectors.toList());
                forkJoinPool.shutdown();
                logger.start(forkJoinPool);
                results.stream()
                        .map(ForkJoinTask::join)
                        .mapToInt(x -> {
                            logger.log(x);
                            return x.getT2();
                        })
                        .average()
                        .ifPresent(average -> logger.log("Runs average: " + average + "\n"));

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void runABCSCPMonoThread() {
        try {
            Problem.read("src/main/resources/scp41.txt");
            AbcVars vr = new AbcVars();
            BeeColony bee = new BeeColony(vr);
            bee.initial();
            bee.memorizeBestSource();
            for (int iter = 0; iter < MAX_CYCLE; iter++) {
                bee.sendEmployedBees();
                bee.calculateProbabilitiesOne();
                bee.sendOnlookerBees();
                bee.memorizeBestSource();
                bee.sendScoutBees();
                logger.addProgress(0);
                logger.setGlobalMin(0, vr.getGLOBAL_MIN());
                logger.printLog(0);
            }
            vr.addGlobalMin(vr.getGLOBAL_MIN());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
