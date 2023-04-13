package abcscp;

import abcscp.config.Variables;
import abcscp.utils.PrintUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static abcscp.config.Parameters.MAX_CYCLE;
import static abcscp.config.Parameters.RUNTIME;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

    public static void main2(String[] args) {
//        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "50");
//        System.out.println("getParallelism=" + ForkJoinPool.commonPool().getParallelism());
//
//        double mean = 0;
//        try {
//            problem.read("src/main/resources/scpnrg3.txt");
//            for (int run = 0; run < RUNTIME; run++) {
//                bee.initial();
//                bee.memorizeBestSource();
//
//                int finalRun = run;
//
//                ForkJoinPool forkJoinPool = new ForkJoinPool(250);
//
//                List<ForkJoinTask<Tuple2<Integer, Integer>>> results =
//                        IntStream.range(0, MAX_CYCLE)
//                                .sorted()
//                                .mapToObj(iter -> forkJoinPool.submit(() -> {
//                                    bee.sendEmployedBees();
//                                    bee.calculateProbabilitiesTwo();
//                                    bee.sendOnlookerBees();
//                                    bee.memorizeBestSource();
//                                    bee.sendScoutBees();
//                                    return new Tuple2<>(iter, GLOBAL_MIN);
//                                })).collect(Collectors.toList());
//                results.stream().map(ForkJoinTask::join).collect(Collectors.toList());
//                GLOBAL_MINS.add(GLOBAL_MIN);
//                mean = mean + GLOBAL_MIN;
//
//                printIterationResults(run);
//
//            }
//            mean = mean / RUNTIME;
//            System.out.println("Means  of " + RUNTIME + " runs: " + mean);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

    /*
        public static void mainF(String[] args) {
            double mean = 0;
            try {
                problem.read("src/main/resources/scpnrg3.txt");
                for (int run = 0; run < RUNTIME; run++) {
                    bee.initial();
                    bee.memorizeBestSource();
                    for (int iter = 0; iter < MAX_CYCLE; iter++) {
                        bee.sendEmployedBees();
                        bee.calculateProbabilitiesTwo();
                        bee.sendOnlookerBees();
                        bee.memorizeBestSource();
                        bee.sendScoutBees();
                        printProgress(run, iter);
                    }
                    GLOBAL_MINS.add(GLOBAL_MIN);
                    mean = mean + GLOBAL_MIN;

                    printIterationResults(run);
                }
                mean = mean / RUNTIME;
                System.out.printf("Means  of %d runs: %s%n", RUNTIME, mean);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    */
    public static void main(String[] args) {

        Variables v = new Variables();
        Problem problem = new Problem(v);
        BeeColony bee = new BeeColony(v);
        PrintUtils pUtils = new PrintUtils(v);

        File file = new File("src/main/resources/");
        List<String> fileNames =
                Arrays.stream(Objects.requireNonNull(file.list()))
                        .sorted(Comparator.reverseOrder())
                        .collect(Collectors.toList());

        fileNames.forEach(fileName -> {
            double mean = 0;
            try {
                problem.read("src/main/resources/" + fileName);
                System.out.println(format.format(new Date()) + " ==> Processing has started!");

                for (int run = 0; run < RUNTIME; run++) {
                    bee.initial();
                    bee.memorizeBestSource();
                    for (int iter = 0; iter < MAX_CYCLE; iter++) {
                        bee.sendEmployedBees();
                        bee.calculateProbabilitiesTwo();
                        bee.sendOnlookerBees();
                        bee.memorizeBestSource();
                        bee.sendScoutBees();
                        pUtils.printProgress(run, iter);
                    }
                    v.addGlobalMin(v.getGLOBAL_MIN());
                    mean = mean + v.getGLOBAL_MIN();

                    pUtils.printIterationResults(run);
                }
                mean = mean / RUNTIME;
                System.out.println("Means  of " + RUNTIME + " runs: " + mean);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

/*
    public static void main3(String[] args) {

        IntStream.range(0, RUNTIME)
                .boxed()
                .parallel()
                .forEachOrdered(run -> {
                    try {
                        problem.read("src/main/resources/scpnre1.txt");
                        bee.initial();
                        bee.memorizeBestSource();

                        for (int iter = 0; iter < MAX_CYCLE; iter++) {
                            bee.sendEmployedBees();
                            bee.calculateProbabilitiesTwo();
                            bee.sendOnlookerBees();
                            bee.memorizeBestSource();
                            bee.sendScoutBees();

                            printProgress(run, iter);
                        }
                        GLOBAL_MINS.add(GLOBAL_MIN);
                        MEAN = MEAN + GLOBAL_MIN;

                        printIterationResults(run);


                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
        MEAN = MEAN / RUNTIME;
        System.out.println("Means  of " + RUNTIME + " runs: " + MEAN);

    }
*/
}
