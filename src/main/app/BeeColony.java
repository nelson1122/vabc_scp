package main.app;

import main.app.utils.BeeUtils;
import main.app.utils.CommonUtils;
import main.app.variables.AbcVars;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.IntStream;

import static main.app.localsearch.ABCSCPLocalSearch.applyLocalSearch;
import static main.app.config.Parameters.EMPLOYED_BEES;
import static main.app.config.Parameters.FOOD_NUMBER;
import static main.app.config.Parameters.LIMIT;
import static main.app.config.Parameters.ONLOOKER_BEES;

public class BeeColony {
    private AbcVars vr;
    private CommonUtils cUtils;
    private BeeUtils bUtils;
    private Solution solution;
    private Repair repair;

    public BeeColony() {
    }

    public BeeColony(AbcVars v) {
        this.vr = v;
        this.cUtils = new CommonUtils(v);
        this.bUtils = new BeeUtils(v);
        this.solution = new Solution(v);
        this.repair = new Repair(v);
    }

    public void initial() {
        vr.setFOODS(new ArrayList<>());
        vr.setFITNESS(new ArrayList<>());
        vr.setTRIAL(new int[FOOD_NUMBER]);
        vr.setPROB(new ArrayList<>());
        for (int i = 0; i < FOOD_NUMBER; i++) {
            BitSet newFoodSource = solution.createSolution();
            vr.addFoodSource(newFoodSource);
            vr.addFitness(cUtils.calculateFitnessOneStream(newFoodSource));
            vr.setTrial(i, 0);
        }
        vr.setGLOBAL_MIN(vr.getFitness(0));
        vr.setGLOBAL_PARAMS(vr.getFoodSource(0));
    }

    public void sendEmployedBees() {
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
                applyLocalSearch(nfs);
                memorizeSource(nfs, i);
            } else {
                generateScoutBee(i);
            }
        }
    }

    public void sendOnlookerBees() {
        int i = 0;
        int t = 0;
        double r = vr.getRANDOM().nextDouble() * 100.0 / 100.0;
        while (t < ONLOOKER_BEES) {
            if (r < vr.getProbability(i)) {
                t++;
                BitSet fs = vr.getFoodSource(i);
                List<Integer> distinctColumns = cUtils.getColumnsRandomFoodSource(fs, i);
                bUtils.addColumns(fs, distinctColumns);
                bUtils.dropColumns(fs);
                List<Integer> uncoveredRows = cUtils.uncoveredRowsStream(fs);
                if (!uncoveredRows.isEmpty()) {
                    repair.applyRepairSolution(fs, uncoveredRows);
                }
                memorizeSource(fs, i);
            }
            i++;
            if (i == FOOD_NUMBER) {
                i = 0;
            }
        }
    }

    public void sendScoutBees() {
        IntStream.range(0, FOOD_NUMBER)
                .boxed()
                .filter(fs -> vr.getTrial(fs) > LIMIT)
                .forEach(i -> {
                    BitSet newFoodSource = solution.createSolution();
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
//                TRIAL[i] = 0;
            } else {
//                TRIAL[i]++;
            }
        } else {
            vr.incrementTrial(i);
        }
    }

    public void memorizeBestSource() {
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
        }
    }

    private void generateScoutBee(int i) {
        BitSet newFoodSource = solution.createSolution();
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
