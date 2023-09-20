package main.java;

import main.java.utils.BeeUtils;
import main.java.utils.CommonUtils;
import main.java.utils.Tuple2;
import main.java.variables.AbcVars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static main.java.config.Parameters.EMPLOYED_BEES;
import static main.java.config.Parameters.FOOD_NUMBER;
import static main.java.config.Parameters.LIMIT;
import static main.java.config.Parameters.ONLOOKER_BEES;
import static main.java.variables.ScpVars.COLUMNS;

public class BeeColony {
    private AbcVars vr;
    private CommonUtils cUtils;
    private BeeUtils bUtils;
    private Initialization initialization;
    private LocalSearch localSearch;
    private Repair repair;

    public BeeColony() {
    }

    public BeeColony(AbcVars v) {
        this.vr = v;
        this.cUtils = new CommonUtils(v);
        this.bUtils = new BeeUtils(v);
        this.initialization = new Initialization(v);
        this.repair = new Repair(v);
        this.localSearch = new LocalSearch(v);
    }

    public void initial() {
        vr.setFOODS(new ArrayList<>());
        vr.setFITNESS(new ArrayList<>());
        vr.setTRIAL(new int[FOOD_NUMBER]);
        vr.setPROB(Arrays.asList(new Double[FOOD_NUMBER]));
        vr.setFoodsBits(FOOD_NUMBER, COLUMNS);

        IntStream.range(0, FOOD_NUMBER)
                .boxed()
                .forEach(fs -> {
                    BitSet newFoodSource = initialization.createSolution(fs);
                    vr.addFoodSource(newFoodSource);
                    vr.addFitness(cUtils.calculateFitnessOneStream(newFoodSource));
                    vr.setTrial(fs, 0);
                });

        vr.setGLOBALMIN(vr.getFitness(0));
        vr.setGLOBALPARAMS(vr.getFoodSource(0));
    }

    public void sendEmployedBees() {
        IntStream.range(0, EMPLOYED_BEES)
                .boxed()
                .forEach(foodNumber -> {
                    BitSet fs = vr.getFoodSource(foodNumber);

                    int rIndex = cUtils.randomFoodSource(foodNumber);
                    BitSet rfs = vr.getFoodSource(rIndex);
                    List<Integer> distinctColumns = cUtils.distinctColumnsBitSet(fs, rfs);

                    if (!distinctColumns.isEmpty()) {
                        bUtils.addColumns(foodNumber, fs, distinctColumns);
                        bUtils.dropColumns(foodNumber, fs);
                        BitSet uncoveredRows = cUtils.uncoveredRowsBitset(fs);
                        if (!uncoveredRows.isEmpty()) {
                            List<Integer> uncoveredRowsList = cUtils.getBitsetIndexes(uncoveredRows);
                            repair.applyRepairSolution(foodNumber, fs, uncoveredRowsList);
                        }

                        localSearch.setFoodNumber(foodNumber);
                        fs = localSearch.apply(fs);

                        memorizeSource(fs, foodNumber);

                    } else {
                        generateScoutBee(foodNumber);
                    }
                });
/*
        for (int i = 0; i < EMPLOYED_BEES; i++) {
            BitSet nfs = vr.getFoodSource(i);

            int rIndex = cUtils.randomFoodSource(i);
            BitSet rfs = vr.getFoodSource(rIndex);
            List<Integer> distinctColumns = cUtils.distinctColumnsStream(nfs, rfs);

            if (!distinctColumns.isEmpty()) {
                bUtils.addColumns(nfs, distinctColumns);
                bUtils.dropColumns(nfs);
                List<Integer> uncoveredRows = cUtils.uncoveredRowsStream(nfs);
                if (!uncoveredRows.isEmpty()) {
                    repair.applyRepairSolution(nfs, uncoveredRows);
                }
                localSearch.apply(nfs);
                memorizeSource(nfs, i);
            } else {
                generateScoutBee(i);
            }
        }
 */
    }

    public void sendOnlookerBees() {
        AtomicInteger foodNumber = new AtomicInteger(0);

        IntStream.range(0, ONLOOKER_BEES)
                .boxed()
                .forEach(t -> {
                    double randomValue = vr.getRANDOM().nextDouble() * 100.0 / 100.0;
                    double rNum = Math.round(randomValue * 10) / 10.0;

                    double cumulativeProbability = 0.0;
                    for (int fs = 0; fs < FOOD_NUMBER; fs++) {
                        cumulativeProbability += vr.getProbabilityValue(fs);
                        double prob = Math.round(cumulativeProbability * 10) / 10.0;
                        if (rNum <= prob) {
                            foodNumber.set(vr.getProbabilityIndex(fs));
                            break;
                        }
                    }

                    BitSet fs = vr.getFoodSource(foodNumber.get());
                    List<Integer> distinctColumns = cUtils.getColumnsRandomFoodSource(fs, foodNumber.get());
                    bUtils.addColumns(foodNumber.get(), fs, distinctColumns);
                    bUtils.dropColumns(foodNumber.get(), fs);
                    BitSet uncoveredRows = cUtils.uncoveredRowsBitset(fs);
                    if (!uncoveredRows.isEmpty()) {
                        List<Integer> uncoveredRowsList = cUtils.getBitsetIndexes(uncoveredRows);
                        repair.applyRepairSolution(foodNumber.get(), fs, uncoveredRowsList);
                    }

                    localSearch.setFoodNumber(foodNumber.get());
                    fs = localSearch.apply(fs);

                    boolean improved = memorizeSource(fs, foodNumber.get());
                    if (improved) {
                        calculateProbabilitiesOne();
                    }
                });
/*
        while (t < ONLOOKER_BEES) {
            double randomValue = vr.getRANDOM().nextDouble() * 100.0 / 100.0;
            double rNum = Math.round(randomValue * 10) / 10.0;

            double cumulativeProbability = 0.0;
            for (int fs = 0; fs < FOOD_NUMBER; fs++) {
                cumulativeProbability += vr.getProbability(i);
                if (rNum <= cumulativeProbability) {
                    i = fs;
                    break;
                }
            }

            BitSet fs = vr.getFoodSource(i);
            List<Integer> distinctColumns = cUtils.getColumnsRandomFoodSource(fs, i);
            bUtils.addColumns(fs, distinctColumns);
            bUtils.dropColumns(fs);
            List<Integer> uncoveredRows = cUtils.uncoveredRowsStream(fs);
            if (!uncoveredRows.isEmpty()) {
                repair.applyRepairSolution(fs, uncoveredRows);
            }
            localSearch.apply(fs);
            memorizeSource(fs, i);
            calculateProbabilitiesOne();
            t++;

//            if (i == FOOD_NUMBER) {
//                i = 0;
//            }
        }
 */
    }

    public void sendScoutBees() {
        IntStream.range(0, FOOD_NUMBER)
                .boxed()
                .filter(foodNumber -> vr.getTrial(foodNumber) > LIMIT)
                .forEach(foodNumber -> {
                    BitSet newFoodSource = initialization.createSolution(foodNumber);
                    int fitness = cUtils.calculateFitnessOneStream(newFoodSource);
                    vr.setFoodSource(foodNumber, newFoodSource);
                    vr.setFitness(foodNumber, fitness);
                    vr.setTrial(foodNumber, 0);
                    // vr.restartFoodBits(foodNumber, COLUMNS);
                });
    }


    private boolean memorizeSource(BitSet newfs, int foodNumber) {
        int currFitness = vr.getFitness(foodNumber);
        int newFitness = cUtils.calculateFitnessOneStream(newfs);
        boolean improved = false;

        if (currFitness > newFitness) {
            vr.setFoodSource(foodNumber, (BitSet) newfs.clone());
            vr.setFitness(foodNumber, newFitness);
            vr.setTrial(foodNumber, 0);
            improved = true;
        } else if (currFitness == newFitness) {
            int newFitnessTwo = cUtils.calculateFitnessTwoStream(newfs);
            int currFitnessTwo = cUtils.calculateFitnessTwoStream(vr.getFoodSource(foodNumber));
            if (currFitnessTwo > newFitnessTwo) {
                vr.setFoodSource(foodNumber, (BitSet) newfs.clone());
                vr.setTrial(foodNumber, 0);
            } else {
                vr.incrementTrial(foodNumber);
            }
        } else {
            vr.incrementTrial(foodNumber);
        }
        return improved;
    }

    public void memorizeBestSource() {
        IntStream.range(0, FOOD_NUMBER)
                .boxed()
                .forEach(i -> {
                    int fitness = vr.getFitness(i);
                    if (vr.getGLOBALMIN() == fitness) {
                        BitSet currentFS = vr.getFoodSource(i);
                        BitSet currentBestFS = vr.getGLOBALPARAMS();
                        int f = cUtils.calculateFitnessTwoStream(currentFS);
                        int fGlobal = cUtils.calculateFitnessTwoStream(currentBestFS);
                        if (fGlobal > f) {
                            vr.setGLOBALPARAMS(vr.getFoodSource(i));
                        }
                    } else if (vr.getGLOBALMIN() > fitness) {
                        vr.setGLOBALMIN(fitness);
                        vr.setGLOBALPARAMS(vr.getFoodSource(i));
                    }
                });

        /*
        for (int i = 0; i < FOOD_NUMBER; i++) {
            int fitness = vr.getFitness(i);
            if (vr.getGLOBAL_MIN() == fitness) {
                BitSet currentFS = vr.getFoodSource(i);
                BitSet currentBestFS = vr.getGLOBAL_PARAMS();
                int f = cUtils.calculateFitnessTwoStream(currentFS);
                int fGlobal = cUtils.calculateFitnessTwoStream(currentBestFS);
                if (fGlobal > f) {
                    vr.setGLOBAL_PARAMS(vr.getFoodSource(i));
                }
            } else if (vr.getGLOBAL_MIN() > fitness) {
                vr.setGLOBAL_MIN(fitness);
                vr.setGLOBAL_PARAMS(vr.getFoodSource(i));
            }
        }*/
    }

    private void generateScoutBee(int foodNumber) {
        // vr.restartFoodBits(foodNumber, COLUMNS);

        BitSet newFoodSource = initialization.createSolution(foodNumber);
        vr.setFoodSource(foodNumber, newFoodSource);
        vr.setFitness(foodNumber, cUtils.calculateFitnessOneStream(newFoodSource));
        vr.setTrial(foodNumber, 0);
    }


    public void calculateProbabilitiesOne() {
        double sumFitness = 0d;
        for (int i = 0; i < FOOD_NUMBER; i++) {
            sumFitness += vr.getFitness(i);
        }
        for (int i = 0; i < FOOD_NUMBER; i++) {
            double result = vr.getFitness(i) / sumFitness;
            vr.setProbability(i, result);
        }

        List<Tuple2<Integer, Double>> probSorted =
                IntStream.range(0, FOOD_NUMBER)
                        .boxed()
                        .map(p -> new Tuple2<>(p, vr.getProbability(p)))
                        .sorted(Comparator.comparing(Tuple2::getT2))
                        .collect(Collectors.toList());
        vr.setPROBSRW(probSorted);

    }

    public void calculateProbabilitiesTwo() {
        double maxfit = vr.getFitness(0);
        for (int i = 0; i < FOOD_NUMBER; i++) {
            if (vr.getFitness(i) > maxfit) {
                maxfit = vr.getFitness(i);
            }
        }
        for (int i = 0; i < FOOD_NUMBER; i++) {
            double result = (0.9 * (vr.getFitness(i) / maxfit)) + 0.1;
            vr.setProbability(i, result);
        }
    }
}
