package main.java;

import main.java.utils.BeeUtils;
import main.java.utils.CommonUtils;
import main.java.variables.AbcVars;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static main.java.config.Parameters.EMPLOYED_BEES;
import static main.java.config.Parameters.FOOD_NUMBER;
import static main.java.config.Parameters.LIMIT;

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
        this.localSearch = new LocalSearch();
    }

    public void initial() {
        vr.setFOODS(new ArrayList<>());
        vr.setFITNESS(new ArrayList<>());
        vr.setTRIAL(new int[FOOD_NUMBER]);
        vr.setPROB(new ArrayList<>());
/*
        for (int i = 0; i < FOOD_NUMBER; i++) {
            BitSet newFoodSource = initialization.createSolution();
            vr.addFoodSource(newFoodSource);
            vr.addFitness(cUtils.calculateFitnessOneStream(newFoodSource));
            vr.setTrial(i, 0);
        }
*/
        IntStream.range(0, FOOD_NUMBER)
                .boxed()
                .forEach(i -> {
                    BitSet newFoodSource = initialization.createSolution();
                    vr.addFoodSource(newFoodSource);
                    vr.addFitness(cUtils.calculateFitnessOneStream(newFoodSource));
                    vr.setTrial(i, 0);
                });
        vr.setGLOBAL_MIN(vr.getFitness(0));
        vr.setGLOBAL_PARAMS(vr.getFoodSource(0));
    }

    public void sendEmployedBees() {
        IntStream.range(0, EMPLOYED_BEES)
                .boxed()
                .forEach(i -> {
                    BitSet fs = vr.getFoodSource(i);

                    int rIndex = cUtils.randomFoodSource(i);
                    BitSet rfs = vr.getFoodSource(rIndex);
                    List<Integer> distinctColumns = cUtils.distinctColumnsBitSet(fs, rfs);

                    if (!distinctColumns.isEmpty()) {
                        bUtils.addColumns(fs, distinctColumns);
                        bUtils.dropColumns(fs);
                        List<Integer> uncoveredRows = cUtils.uncoveredRowsStream(fs);
                        if (!uncoveredRows.isEmpty()) {
                            repair.applyRepairSolution(fs, uncoveredRows);
                        }
                        fs = localSearch.apply(fs);
                        memorizeSource(fs, i);

                    } else {
                        generateScoutBee(i);
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
        AtomicInteger i = new AtomicInteger(0);

        IntStream.range(0, EMPLOYED_BEES)
                .boxed()
                .forEach(t -> {
                    double randomValue = vr.getRANDOM().nextDouble() * 100.0 / 100.0;
                    double rNum = Math.round(randomValue * 10) / 10.0;

                    double cumulativeProbability = 0.0;
                    for (int fs = 0; fs < FOOD_NUMBER; fs++) {
                        cumulativeProbability += vr.getProbability(fs);
                        if (rNum <= cumulativeProbability) {
                            i.set(fs);
                            break;
                        }
                    }

                    BitSet fs = vr.getFoodSource(i.get());
                    List<Integer> distinctColumns = cUtils.getColumnsRandomFoodSource(fs, i.get());
                    bUtils.addColumns(fs, distinctColumns);
                    bUtils.dropColumns(fs);
                    List<Integer> uncoveredRows = cUtils.uncoveredRowsStream(fs);
                    if (!uncoveredRows.isEmpty()) {
                        repair.applyRepairSolution(fs, uncoveredRows);
                    }
                    fs = localSearch.apply(fs);
                    memorizeSource(fs, i.get());
                    calculateProbabilitiesOne();

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
                .filter(fs -> vr.getTrial(fs) > LIMIT)
                .forEach(i -> {
                    BitSet newFoodSource = initialization.createSolution();
                    int fitness = cUtils.calculateFitnessOneStream(newFoodSource);
                    vr.setFoodSource(i, newFoodSource);
                    vr.setFitness(i, fitness);
                    vr.setTrial(i, 0);
                });
    }


    private void memorizeSource(BitSet newFoodSource, int i) {
        int newFitness = cUtils.calculateFitnessOneStream(newFoodSource);
        int currFitness = vr.getFitness(i);

        if (currFitness > newFitness) {
            vr.setFoodSource(i, newFoodSource);
            vr.setFitness(i, newFitness);
            vr.setTrial(i, 0);
        } else if (currFitness == newFitness) {
            int newFitnessTwo = cUtils.calculateFitnessTwoStream(newFoodSource);
            int currFitnessTwo = cUtils.calculateFitnessTwoStream(vr.getFoodSource(i));

            if (currFitnessTwo > newFitnessTwo) {
                vr.setFoodSource(i, newFoodSource);
                vr.setTrial(i, 0);
//                TRIAL[i] = 0;
            } else {
//                vr.incrementTrial(i);
//                TRIAL[i]++;
            }
        } else {
            vr.incrementTrial(i);
        }
    }

    public void memorizeBestSource() {
        IntStream.range(0, FOOD_NUMBER)
                .boxed()
                .forEach(i -> {
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

    private void generateScoutBee(int i) {
        BitSet newFoodSource = initialization.createSolution();
        vr.setFoodSource(i, newFoodSource);
        vr.setFitness(i, cUtils.calculateFitnessOneStream(newFoodSource));
        vr.setTrial(i, 0);
    }


    public void calculateProbabilitiesOne() {
        double sumFitness = 0d;
        for (int i = 0; i < FOOD_NUMBER; i++) {
            sumFitness += vr.getFitness(i);
        }
        for (int i = 0; i < FOOD_NUMBER; i++) {
            double result = vr.getFitness(i) / sumFitness;
            vr.addProbability(result);
        }

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
            vr.addProbability(result);
        }
    }
}
