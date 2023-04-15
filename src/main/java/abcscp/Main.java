package abcscp;

import abcscp.config.Variables;
import abcscp.utils.Logger;
import abcscp.utils.Tuple3;

import java.io.File;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static abcscp.config.Parameters.MAX_CYCLE;
import static abcscp.config.Parameters.RUNTIME;

public class Main {
    static Logger logger = new Logger();

    public static void main(String[] args) {
        //runABCSCPMonoThread();
        runABCSCPMultiThread();
    }



    public static void runABCSCPMultiThread() {
        File file = new File("src/main/resources/");
        List<String> fileNames =
                Arrays.stream(Objects.requireNonNull(file.list()))
                        //.sorted(Comparator.reverseOrder())
                        .collect(Collectors.toList());

        ForkJoinPool forkJoinPool = new ForkJoinPool(10);
        for (String fileName : fileNames) {
            try {
                Problem.read("src/main/resources/" + fileName);
                AtomicReference<Integer> progress = new AtomicReference<>(0);
                logger.log("Processing file [" + fileName + "] has started!");
                int[] RUNS = new int[RUNTIME];

                List<ForkJoinTask<Tuple3<Integer, Integer, BitSet>>> results =
                        IntStream.range(0, RUNTIME)
                                .sorted()
                                .mapToObj(rIndex -> forkJoinPool.submit(() -> {
                                    Variables vr = new Variables();
                                    BeeColony bee = new BeeColony(vr);
                                    bee.initial();
                                    bee.memorizeBestSource();
                                    for (int iter = 0; iter < MAX_CYCLE; iter++) {
                                        bee.sendEmployedBees();
                                        bee.calculateProbabilitiesTwo();
                                        bee.sendOnlookerBees();
                                        bee.memorizeBestSource();
                                        bee.sendScoutBees();
                                        RUNS[rIndex]++;
                                        logger.log(RUNS);
                                    }
                                    vr.addGlobalMin(vr.getGLOBAL_MIN());
                                    return new Tuple3<>(rIndex, vr.getGLOBAL_MIN(), vr.getGLOBAL_PARAMS());
                                }))
                                .collect(Collectors.toList());

                results.stream()
                        .map(ForkJoinTask::join)
                        .mapToInt(x -> {
                            logger.log(x);
                            return x.getT2();
                        })
                        .average()
                        .ifPresent(average -> logger.log("run average: " + average));

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
