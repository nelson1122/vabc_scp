package abc.scp;

import java.util.Arrays;
import java.util.Random;

import static abc.scp.Params.A;
import static abc.scp.Params.C;
import static abc.scp.Params.COLUMNS;
import static abc.scp.Params.COL_ADD_1;
import static abc.scp.Params.COL_ADD_2;
import static abc.scp.Params.COL_DROP_1;
import static abc.scp.Params.COL_DROP_2;
import static abc.scp.Params.FITNESS;
import static abc.scp.Params.FOODS;
import static abc.scp.Params.FOOD_NUMBER;
import static abc.scp.Params.PROB;
import static abc.scp.Params.RC_SIZE;
import static abc.scp.Params.ROWS;

public class Commons {
    public static int[] getRowsCoveredByColumn(int j) {
        int[] temp = new int[ROWS];
        int index = 0;
        for (int i = 0; i < ROWS; i++) {
            if (A[i][j] == 1) {
                temp[index] = i;
                index++;
            }
        }
        int[] rowsCovered = new int[index];
        System.arraycopy(temp, 0, rowsCovered, 0, index);
        return rowsCovered;
    }

    public static int selectRandomColumnFromRCL_G(int[] solution, int[] ai) {
        int random = randomNumber(RC_SIZE);
        int[] coveringColumns = getCoveringColumns(ai);
        return coveringColumns[random];
    }

    public static int selectRandomColumnFromRCL_R(int i) {
        int random = randomNumber(RC_SIZE);
        int[] columnsCovering = getColumnsCoveringRow(i);
        return columnsCovering[random];
    }

    public static int randomNumber(int high) {
        Random r = new Random();
        int low = 0;
        return r.nextInt(high - low) + low;
    }

    // FITNESS FUNCTIONS
    public static int calculateFitnessPrimary(int[] xj) {
        int result = 0;
        for (int j = 0; j < COLUMNS; j++)
            result += (C[j] * xj[j]);
        return result;
    }

    public static int calculateFitnessSecondary(int[] xj) {
        int count = 0;
        for (int j = 0; j < COLUMNS; j++)
            if (xj[j] != 0) {
                int[] rowsCovered = getRowsCoveredByColumn(j);
                count += rowsCovered.length;
            }
        return count;
    }

    public static int[] getUncoveredRows(int[] solution) {
        int[] temp = new int[ROWS];
        int index = 0;
        for (int i = 0; i < ROWS; i++) {
            int sum = 0;
            for (int j = 0; j < COLUMNS; j++) {
                sum += (A[i][j] * solution[j]);
            }
            if (sum < 1) {
                temp[index] = i;
                index++;
            }
        }
        int[] uncoveredRows = new int[index];
        System.arraycopy(temp, 0, uncoveredRows, 0, index);
        return uncoveredRows;
    }

    public static int selectColumnMinRatio(int[] solution, int i) {
        int[] ai = getColumnsCoveringRow(i);
        float[] ratio = new float[ai.length];

        for (int x = 0; x < ai.length; x++) {
            int j = ai[x];
            int[] uncoveredRowsCovered = getUncoveredRowsCoveredByColumn(solution, j);
            ratio[x] = (float) C[x] / uncoveredRowsCovered.length;
        }

        double ratioMin = ratio[0];
        int selectedColumn = ai[0];

        for (int x = 0; x < ratio.length; x++) {
            if (ratioMin > ratio[x]) {
                ratioMin = ratio[x];
                selectedColumn = ai[x];
            }
        }
        return selectedColumn;
    }

    public static int[] getColumnsCoveringRow(int i) {
        int[] temp = new int[COLUMNS];
        int index = 0;
        for (int j = 0; j < COLUMNS; j++) {
            if (A[i][j] == 1) {
                temp[index] = j;
                index++;
            }
        }
        int[] columnsCovering = new int[index];
        System.arraycopy(temp, 0, columnsCovering, 0, index);
        return columnsCovering;
    }

    private static int[] getUncoveredRowsCoveredByColumn(int[] solution, int j) {
        int[] coveredRows = getRowsCoveredByColumn(j);
        int[] uncoveredRows = getUncoveredRows(solution);
        int[] temp = new int[ROWS];
        int index = 0;
        for (int uncoveredRow : uncoveredRows) {
            for (int coveredRow : coveredRows) {
                if (coveredRow == uncoveredRow) {
                    temp[index] = uncoveredRow;
                    index++;
                }
            }
        }

        if (index == 0) {
            System.out.printf("REVISAR");
        }

        int[] uncoveredRowsCovered = new int[index];
        System.arraycopy(temp, 0, uncoveredRowsCovered, 0, index);
        return uncoveredRowsCovered;
    }

    public static int selectColumnMaxRatio(int[] solution) {
        int[] columnsCovering = getCoveringColumns(solution);
        int size = columnsCovering.length;
        double[] ratio = new double[size];
        for (int j = 0; j < size; j++) {
            int index = columnsCovering[j];
            int[] rowsCovered = getRowsCoveredByColumn(index);
            ratio[j] = (double) C[index] / rowsCovered.length;
        }

//        double ratioMax = ratio[ratio.length - 1];
//        int selectedColumn = 0;

//        for (int x = ratio.length - 1; x >= 0; x--) {
//            if (ratioMax > ratio[x]) {
//                ratioMax = ratio[x];
//                selectedColumn = columnsCovering[x];
//            }
//        }


        double radioMax = ratio[0];
        int selectedColumn = 0;
        for (int x = 0; x < size; x++) {
            if (ratio[x] > radioMax) {
                radioMax = ratio[x];
                selectedColumn = columnsCovering[x];
            }
        }


        return selectedColumn;
    }

    public static int selectRandomFoodSource(int i) {
        int randomFood = randomNumber(FOOD_NUMBER);
        if (i != randomFood) {
            return randomFood;
        }
        return selectRandomFoodSource(i);
    }

    public static int[] findDistinctColumns(int[] i, int[] j) {
        int index = 0;
        int[] temp = new int[COLUMNS];
        for (int x = 0; x < COLUMNS; x++) {
            if (i[x] != j[x] && j[x] == 1) {
                temp[index] = x;
                index++;
            }
        }
        int[] distinctColumns = new int[index];
        System.arraycopy(temp, 0, distinctColumns, 0, index);
        return distinctColumns;
    }

    public static int[] addColumnsToFoodSource(int[] solution, int[] distinctColumns) {
        int colAdd;
        int[] coveringColumns = getCoveringColumns(solution);

        if (coveringColumns.length > 35) {
            colAdd = COL_ADD_1;
        } else {
            colAdd = COL_ADD_2;
        }

//        int dc = distinctColumns.length;

//        if (dc < 3) {
//            colAdd = distinctColumns.length;
//        } else if (dc > 35) {
//            colAdd = COL_ADD_1;
//        } else {
//            colAdd = COL_ADD_2;
//        }

        while (colAdd >= 0) {
            int randomColumn = randomNumber(distinctColumns.length);
            if (distinctColumns[randomColumn] != -1) {
                int index = distinctColumns[randomColumn];
                solution[index] = 1;
                distinctColumns[randomColumn] = -1;
                colAdd--;
            }
        }

        return Arrays.copyOf(solution, COLUMNS);
//        if (distinctColumns.length > 2) {
//            int[] coveringColumns = getCoveringColumns(SOLUTION);
//            int n = coveringColumns.length;
//            int colAdd = COL_ADD_1;
//            if (n < 35) {
//                colAdd = COL_ADD_2;
//            }
//            for (int j = 0; j < colAdd; j++) {
//                int index = distinctColumns[j];
//                SOLUTION[index] = 1;
//            }
//        }

//        int[] coveringColumns = getCoveringColumns(SOLUTION);
//        int n = coveringColumns.length;
//        int colAdd = COL_ADD_2;
//        if (distinctColumns.length < 3) {
//            colAdd = distinctColumns.length;
//        } else if (n > 35) {
//            colAdd = COL_ADD_1;
//        }
//        for (int j = 0; j < colAdd; j++) {
//            int index = distinctColumns[j];
//            SOLUTION[index] = 1;
//        }
    }

    public static int[] getCoveringColumns(int[] row) {
        int[] temp = new int[COLUMNS];
        int index = 0;
        for (int j = 0; j < COLUMNS; j++) {
            if (row[j] == 1) {
                temp[index] = j;
                index++;
            }
        }
        int[] columns = new int[index];
        System.arraycopy(temp, 0, columns, 0, index);
        return columns;
    }

    public static int[] dropColumnsFromFoodSource(int[] solution) {
        int[] coveringColumns = getCoveringColumns(solution);
        int n = coveringColumns.length;
        // int colDrop = n > 35 ? COL_DROP_1 : COL_DROP_2;
        int colDrop;
        if (n > 35) {
            colDrop = COL_DROP_1;
        } else {
            colDrop = COL_DROP_2;
        }


        int count = 0;
        while (count < colDrop) {
            int j = selectRandomColumnToDrop(solution, n, coveringColumns);
            solution[j] = 0;
            count++;
        }
        return Arrays.copyOf(solution, COLUMNS);
    }

    private static int selectRandomColumnToDrop(int[] solution, int n, int[] coveringColumns) {
        int j = randomNumber(n);
        int index = coveringColumns[j];
        if (solution[index] == 1) {
            return index;
        }
        return selectRandomColumnToDrop(solution, n, coveringColumns);
    }

    public static int[] selectColumnsRandomFoodSource(int[] solution, int i) {
        int randomFoodS = selectRandomFoodSource(i);
        int[] distinctColumns = findDistinctColumns(solution, FOODS[randomFoodS]);
        int length = distinctColumns.length;
        if (length > 0) {
            return distinctColumns;
        }
        return selectColumnsRandomFoodSource(solution, i);
    }

    //Probabilities
    public static void calculateProbabilities() {
        double sumFitness = 0d;
        for (int i = 0; i < FOOD_NUMBER; i++) {
            sumFitness += FITNESS[i];
        }

        for (int i = 0; i < FOOD_NUMBER; i++) {
            PROB[i] = FITNESS[i] / sumFitness;
        }

    }

    public static void calculateProbabilities2() {
        double maxfit = FITNESS[0];
        for (int i = 1; i < FOOD_NUMBER; i++) {
            if (FITNESS[i] > maxfit)
                maxfit = FITNESS[i];
        }

        for (int i = 0; i < FOOD_NUMBER; i++) {
            PROB[i] = (0.9 * (FITNESS[i] / maxfit)) + 0.1;
        }
    }

    public static void calculateProbability() {
        double maxfit = FITNESS[0];
        for (int i = 1; i < FOOD_NUMBER; i++) {
            if (FITNESS[i] > maxfit)
                maxfit = FITNESS[i];
        }

        for (int i = 0; i < FOOD_NUMBER; i++) {
            PROB[i] = (0.9 * (FITNESS[i] / maxfit)) + 0.1;
        }
    }
}
