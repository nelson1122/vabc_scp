package abc.scp;

import java.util.Arrays;
import java.util.Random;

import static abc.scp.Commons.addColumnsToFoodSource;
import static abc.scp.Commons.calculateFitnessPrimary;
import static abc.scp.Commons.calculateFitnessSecondary;
import static abc.scp.Commons.dropColumnsFromFoodSource;
import static abc.scp.Commons.findDistinctColumns;
import static abc.scp.Commons.getUncoveredRows;
import static abc.scp.Commons.selectColumnsRandomFoodSource;
import static abc.scp.Commons.selectRandomFoodSource;
import static abc.scp.Operator.applyRepairOperator;
import static abc.scp.Params.COLUMNS;
import static abc.scp.Params.EMPLOYED_BEES;
import static abc.scp.Params.FITNESS;
import static abc.scp.Params.FITNESS_SOL;
import static abc.scp.Params.FOODS;
import static abc.scp.Params.FOOD_NUMBER;
import static abc.scp.Params.GLOBAL_MIN;
import static abc.scp.Params.GLOBAL_PARAMS;
import static abc.scp.Params.LIMIT;
import static abc.scp.Params.ONLOOKER_BEES;
import static abc.scp.Params.PROB;
import static abc.scp.Params.TRIAL;


public class Bee {
    public Bee() {
    }

    public void initial() {
        FOODS = new int[FOOD_NUMBER][COLUMNS];
        GLOBAL_PARAMS = new int[COLUMNS];
        FITNESS = new double[FOOD_NUMBER];
        TRIAL = new double[FOOD_NUMBER];
        PROB = new double[FOOD_NUMBER];
        for (int i = 0; i < FOOD_NUMBER; i++) {
            int[] solution = Operator.generateSolution();
            FOODS[i] = Arrays.copyOf(solution, solution.length);
            FITNESS[i] = calculateFitnessPrimary(FOODS[i]);
            TRIAL[i] = 0;
        }
        GLOBAL_MIN = FITNESS[0];
        GLOBAL_PARAMS = FOODS[0];
    }

    public void sendEmployedBees() {
        for (int i = 0; i < EMPLOYED_BEES; i++) {
            int[] foodSource = Arrays.copyOf(FOODS[i], COLUMNS);

            int randomIndex = selectRandomFoodSource(i);
            int[] randomFoodSource = Arrays.copyOf(FOODS[randomIndex], COLUMNS);

            int[] distinctColumns = findDistinctColumns(foodSource, randomFoodSource);
            int[] newFoodSource;

            if (distinctColumns.length > 0) {
                newFoodSource = addColumnsToFoodSource(foodSource, distinctColumns);
                newFoodSource = dropColumnsFromFoodSource(newFoodSource);
                int[] uncoveredRows = getUncoveredRows(newFoodSource);
                if (uncoveredRows.length > 0) {
                    newFoodSource = applyRepairOperator(newFoodSource, uncoveredRows);
                }

                FITNESS_SOL = calculateFitnessPrimary(newFoodSource);
                if (FITNESS_SOL < FITNESS[i]) {
                    FOODS[i] = Arrays.copyOf(newFoodSource, COLUMNS);
                    FITNESS[i] = FITNESS_SOL;
                    TRIAL[i] = 0;
                } else {
                    TRIAL[i]++;
                }

            } else {
                int[] solution = Operator.generateSolution();
                FOODS[i] = Arrays.copyOf(solution, COLUMNS);
                FITNESS[i] = calculateFitnessPrimary(FOODS[i]);
                TRIAL[i] = 0;
            }
        }
    }

    public void sendOnlookerBees() {
        int i = 0, t = 0;
        while (t < ONLOOKER_BEES) {
            double r = (new Random().nextDouble() * 100.0) / 100.0;
            if (r < PROB[i]) {
                t++;

                int[] foodSource = Arrays.copyOf(FOODS[i], COLUMNS);
                int[] distinctColumns = selectColumnsRandomFoodSource(foodSource, i);
                int[] newFoodSource;

                newFoodSource = addColumnsToFoodSource(foodSource, distinctColumns);
                newFoodSource = dropColumnsFromFoodSource(newFoodSource);
                int[] uncoveredRows = getUncoveredRows(newFoodSource);
                if (uncoveredRows.length > 0) {
                    newFoodSource = applyRepairOperator(newFoodSource, uncoveredRows);
                }

                FITNESS_SOL = calculateFitnessPrimary(newFoodSource);
                if (FITNESS_SOL < FITNESS[i]) {
                    FOODS[i] = Arrays.copyOf(newFoodSource, COLUMNS);
                    FITNESS[i] = FITNESS_SOL;
                    TRIAL[i] = 0;
                } else {
                    TRIAL[i]++;
                }
            }
            i++;
            if (i == FOOD_NUMBER) {
                i = 0;
            }
        }
    }

    public void sendScoutBees() {
        int maxtrialindex, i;
        maxtrialindex = 0;
        for (i = 0; i < FOOD_NUMBER; i++) {
            if (TRIAL[i] > TRIAL[maxtrialindex])
                maxtrialindex = i;
        }
        if (TRIAL[maxtrialindex] > LIMIT) {
            int[] newFoodSource = Operator.generateSolution();
            FOODS[maxtrialindex] = Arrays.copyOf(newFoodSource, COLUMNS);
            FITNESS[maxtrialindex] = calculateFitnessPrimary(FOODS[maxtrialindex]);
            TRIAL[maxtrialindex] = 0;
        }
    }

    public void memorizeBestSource() {
        for (int i = 0; i < FOOD_NUMBER; i++) {
            if (FITNESS[i] == GLOBAL_MIN) {
                int f = calculateFitnessSecondary(FOODS[i]);
                int fGlobal = calculateFitnessSecondary(GLOBAL_PARAMS);
                if (f < fGlobal) {
                    GLOBAL_PARAMS = Arrays.copyOf(FOODS[i], COLUMNS);
                }
            } else if (FITNESS[i] < GLOBAL_MIN) {
                GLOBAL_MIN = FITNESS[i];
                GLOBAL_PARAMS = Arrays.copyOf(FOODS[i], COLUMNS);
            }
        }
    }

    public void calculateProbabilities() {
        Commons.calculateProbabilities();
    }

    public void calculateProbabilities2() {
        Commons.calculateProbabilities2();
    }
}
