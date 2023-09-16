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

//    static String groupName = "I";
//    static String[] fileNames = {
//            "scpnre1.txt",
//            "scpnre2.txt",
//            "scpnre3.txt",
//            "scpnre4.txt",
//            "scpnre5.txt",
//            "scpnrg1.txt",
//            "scpnrg2.txt",
//            "scpnrg3.txt",
//            "scpnrg4.txt",
//            "scpnrg5.txt",
//    };


    static String groupName = "II";
    static String[] fileNames = {
//            "scpnrf1.txt",
//            "scpnrf2.txt",
//            "scpnrf3.txt",
//            "scpnrf4.txt",
//            "scpnrf5.txt",
            "scpnrh1.txt",
            "scpnrh2.txt",
            "scpnrh3.txt",
            "scpnrh4.txt",
            "scpnrh5.txt",
    };

    static int seed = 50;
    static Logger logger = new Logger();

    public static void main(String[] args) {
        logger.log("Variant of the Artificial Bee Colony Algorithm ABC_SCP to solve the Set Covering Problem");
        logger.log("University of Cauca, 2023");
        logger.log("Problems Group: [ " + groupName + " ]");

        runABCSCPMonoThread();
//        runABCSCPMonoThread2();
//        runABCSCPMultiThread();

        logger.log("Algorithm has finished!");
    }

    public static void runABCSCPMultiThread() {
        for (String fileName : fileNames) {
            seed = 50;
            try {
                // Problem.read("src/main/resources/" + fileName);
                Problem.read("main/resources/" + fileName);
                logger.log("Problem processing [" + fileName + "] has started!");
                logger.log();

                ForkJoinPool forkJoinPool = new ForkJoinPool(RUNTIME);
                List<ForkJoinTask<Tuple3<Integer, Integer, BitSet>>> results =
                        IntStream.range(0, RUNTIME)
                                .sorted()
                                .mapToObj(rIndex -> forkJoinPool.submit(() -> {
                                    seed = seed + 50;
                                    logger.setSEED(rIndex, seed);
                                    AbcVars vr = new AbcVars(seed);
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
//            Problem.read("main/resources/scpnrg1.txt");
            Problem.read("src/main/resources/scpnrg3.txt");
            AbcVars vr = new AbcVars(550);
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
            logger.printSolution(vr.getGLOBAL_PARAMS());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void runABCSCPMonoThread2() {

        for (int run = 0; run < RUNTIME; run++) {
            try {
                Problem.read("main/resources/scpnre1.txt");
                System.out.println();
                seed = seed + 10;
                logger.setSEED(run, seed);
                AbcVars vr = new AbcVars(seed);
                BeeColony bee = new BeeColony(vr);
                bee.initial();
                bee.memorizeBestSource();
                for (int iter = 0; iter < MAX_CYCLE; iter++) {
                    bee.sendEmployedBees();
                    bee.calculateProbabilitiesOne();
                    bee.sendOnlookerBees();
                    bee.memorizeBestSource();
                    bee.sendScoutBees();
                    logger.addProgress(run);
                    logger.setGlobalMin(run, vr.getGLOBAL_MIN());
                    logger.printLog2(run);
                }
                vr.addGlobalMin(vr.getGLOBAL_MIN());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.println();
        }

    }
}
