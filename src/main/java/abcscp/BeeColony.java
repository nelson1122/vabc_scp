package abcscp;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static abcscp.Repair.applyRepairSolution;
import static abcscp.Solution.createSolution;
import static abcscp.utils.BeeUtils.addColumns;
import static abcscp.utils.BeeUtils.dropColumns;
import static abcscp.utils.CommonUtils.calculateFitnessOneStream;
import static abcscp.utils.CommonUtils.calculateFitnessTwoStream;
import static abcscp.utils.CommonUtils.distinctColumnsStream;
import static abcscp.utils.CommonUtils.getColumnsRandomFoodSource;
import static abcscp.utils.CommonUtils.randomFoodSource;
import static abcscp.utils.CommonUtils.uncoveredRowsStream;
import static abcscp.utils.Parameters.EMPLOYED_BEES;
import static abcscp.utils.Parameters.FITNESS;
import static abcscp.utils.Parameters.FOODS;
import static abcscp.utils.Parameters.FOOD_NUMBER;
import static abcscp.utils.Parameters.GLOBAL_MIN;
import static abcscp.utils.Parameters.GLOBAL_PARAMS;
import static abcscp.utils.Parameters.LIMIT;
import static abcscp.utils.Parameters.ONLOOKER_BEES;
import static abcscp.utils.Parameters.PROB;
import static abcscp.utils.Parameters.ROWS;
import static abcscp.utils.Parameters.TRIAL;


public class BeeColony {
    public void initial() {
        FOODS = new ArrayList<>();
        FITNESS = new ArrayList<>();
        TRIAL = new int[FOOD_NUMBER];
        PROB = new ArrayList<>();

        for (int i = 0; i < FOOD_NUMBER; i++) {
            BitSet solution = createSolution();
            FOODS.add(solution);
            FITNESS.add(calculateFitnessOneStream(solution));
            TRIAL[i] = 0;
        }
        GLOBAL_MIN = FITNESS.get(0);
        GLOBAL_PARAMS = (BitSet) FOODS.get(0).clone();
    }

    public void sendEmployedBees() {
        for (int i = 0; i < EMPLOYED_BEES; i++) {
            BitSet fs = (BitSet) FOODS.get(i).clone();

            int rIndex = randomFoodSource(i);
            BitSet rfs = (BitSet) FOODS.get(rIndex).clone();
            List<Integer> distinctColumns = distinctColumnsStream(fs, rfs);

            if (!distinctColumns.isEmpty()) {
                addColumns(fs, distinctColumns);
                dropColumns(fs);
                List<Integer> uncoveredRows = uncoveredRowsStream(fs);
                if (!uncoveredRows.isEmpty()) {
                    applyRepairSolution(fs, uncoveredRows);
                }
                memorizeSource(fs, i);
            } else {
                generateScoutBee(i);
            }
        }
    }

    public void sendOnlookerBees() {
        int i = 0, t = 0;
        double r = (Math.random() * 32767 / ((double) (32767) + (double) (1)));
        while (t < ONLOOKER_BEES) {
            if (r < PROB.get(i)) {
                t++;
                BitSet fs = (BitSet) FOODS.get(i).clone();
                List<Integer> distinctColumns = getColumnsRandomFoodSource(fs, i);
                addColumns(fs, distinctColumns);
                dropColumns(fs);
                List<Integer> uncoveredRows = uncoveredRowsStream(fs);
                if (!uncoveredRows.isEmpty()) {
                    applyRepairSolution(fs, uncoveredRows);
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
                .filter(fs -> TRIAL[fs] > LIMIT)
                .forEach(fs -> {
                    BitSet newFoodSource = createSolution();
                    int fitness = calculateFitnessOneStream(newFoodSource);
                    FOODS.set(fs, (BitSet) newFoodSource.clone());
                    FITNESS.set(fs, fitness);
                    TRIAL[fs] = 0;
                });

//        for (int i = 0; i < FOOD_NUMBER; i++) {
//            if (TRIAL[i] > LIMIT) {
//                BitSet newFoodSource = createSolution();
//                int fitness = calculateFitnessOneStream(newFoodSource);
//                FOODS.set(i, (BitSet) newFoodSource.clone());
//                FITNESS.set(i, fitness);
//                TRIAL[i] = 0;
//            }
//        }
    }


    private void memorizeSource(BitSet newFoodSource, int i) {
        int newFitness = calculateFitnessOneStream(newFoodSource);
        int currFitness = FITNESS.get(i);

        if (currFitness > newFitness) {
            FOODS.set(i, (BitSet) newFoodSource.clone());
            FITNESS.set(i, newFitness);
            TRIAL[i] = 0;
        } else if (currFitness == newFitness) {
            int newFitnessTwo = calculateFitnessTwoStream(newFoodSource);
            int currFitnessTwo = calculateFitnessTwoStream(FOODS.get(i));

            if (currFitnessTwo > newFitnessTwo) {
                FOODS.set(i, (BitSet) newFoodSource.clone());
//                TRIAL[i] = 0;
            } else {
//                TRIAL[i]++;
            }
        } else {
            TRIAL[i]++;
        }
    }

    public void memorizeBestSource() {
        for (int i = 0; i < FOOD_NUMBER; i++) {
            int fitness = FITNESS.get(i);
            if (GLOBAL_MIN.equals(fitness)) {
                int f = calculateFitnessTwoStream(FOODS.get(i));
                int fGlobal = calculateFitnessTwoStream(GLOBAL_PARAMS);
                if (fGlobal > f) {
                    GLOBAL_PARAMS = (BitSet) FOODS.get(i).clone();
                }
            } else if (GLOBAL_MIN > fitness) {
                GLOBAL_MIN = fitness;
                GLOBAL_PARAMS = (BitSet) FOODS.get(i).clone();
            }
        }
    }

    private void generateScoutBee(int i) {
        BitSet solution = createSolution();
        FOODS.set(i, (BitSet) solution.clone());
        FITNESS.set(i, calculateFitnessOneStream(solution));
        TRIAL[i] = 0;
    }


    public void calculateProbabilitiesOne() {
        double sumFitness = 0d;
        for (int i = 0; i < FOOD_NUMBER; i++) {
            sumFitness += FITNESS.get(i);
        }

        for (int i = 0; i < FOOD_NUMBER; i++) {
            double probability = FITNESS.get(i) / sumFitness;
            PROB.add(probability);
        }

    }

    public void calculateProbabilitiesTwo() {
        double maxfit = FITNESS.get(0);
        for (int i = 0; i < FOOD_NUMBER; i++) {
            if (FITNESS.get(i) > maxfit) {
                maxfit = FITNESS.get(i);
            }
        }
        for (int i = 0; i < FOOD_NUMBER; i++) {
            double result = (0.9 * (FITNESS.get(i) / maxfit)) + 0.1;
            PROB.add(result);
        }
    }
}
