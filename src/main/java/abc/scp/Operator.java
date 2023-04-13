package abc.scp;

import java.util.Arrays;

import static abc.scp.Commons.getColumnsCoveringRow;
import static abc.scp.Commons.getCoveringColumns;
import static abc.scp.Commons.getRowsCoveredByColumn;
import static abc.scp.Commons.getUncoveredRows;
import static abc.scp.Commons.randomNumber;
import static abc.scp.Commons.selectColumnMaxRatio;
import static abc.scp.Commons.selectColumnMinRatio;
import static abc.scp.Commons.selectRandomColumnFromRCL_G;
import static abc.scp.Commons.selectRandomColumnFromRCL_R;
import static abc.scp.Params.A;
import static abc.scp.Params.COLUMNS;
import static abc.scp.Params.Pa;
import static abc.scp.Params.ROWS;

public class Operator {

    private Operator() {
    }

    // INITIALIZATION
    public static int[] generateSolution() {
        int[] solution = new int[COLUMNS];
        int[] U = new int[ROWS];
        generateRandomSolution(solution, U);
        removeRedundantColumnsG(solution, U);
        return solution;
    }

    private static int[] generateRandomSolution(int[] solution, int[] U) {
        for (int i = 0; i < ROWS; i++) {
            int randomColumn = selectRandomColumnFromRCL_G(solution, A[i]);
            if (solution[randomColumn] == 0) {
                solution[randomColumn] = 1;
                int[] Bj = getRowsCoveredByColumn(randomColumn);
                for (int index : Bj) {
                    U[index]++;
                }
            }
        }
        return Arrays.copyOf(solution, COLUMNS);
    }

    private static int[] removeRedundantColumnsG(int[] solution, int[] U) {
        int[] coveringColumns = getCoveringColumns(solution);
        int t = coveringColumns.length;

        while (t > 0) {
            int index = randomNumber(t);
            int randomColumn = coveringColumns[index];
            int[] Bj = getRowsCoveredByColumn(randomColumn);
            boolean flag = true;
            for (int i : Bj) {
                if (U[i] < 2) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                solution[randomColumn] = 0;
                for (int i : Bj) {
                    U[i]--;
                }
                coveringColumns = getCoveringColumns(solution);
            }
            t--;
        }
        return Arrays.copyOf(solution, COLUMNS);

/*
        int t = COLUMNS;

        while (t > 0) {
            int randomColumn = selectRandomColumn(t);
            int[] Bj = getRowsCoveredByColumn(randomColumn);
            boolean flag = true;
            for (int i : Bj) {
                if (U[i] < 2) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                solution[randomColumn] = 0;
                for (int i : Bj) {
                    U[i]--;
                }
            }
            t--;
        }
        return solution;
*/
    }

    // REPAIR OPERATOR
    public static int[] applyRepairOperator(int[] solution, int[] uncoveredRows) {
        makeSolutionFeasible(solution, uncoveredRows);
//        makeSolutionFeasibleBeasley(solution);
        removeRedundantColumnsR(solution);
        return Arrays.copyOf(solution, COLUMNS);
    }

    private static int[] makeSolutionFeasible(int[] solution, int[] uncoveredRows) {
        while (uncoveredRows.length > 0) {
            int i = 0;
            int selectedCol;
            double r = (Math.random() * 32767 / ((double) 32767 + (double) (1)));
            if (r > Pa) {
                selectedCol = selectColumnMinRatio(solution, uncoveredRows[i]);
            } else {
                selectedCol = selectRandomColumnFromRCL_R(uncoveredRows[i]);
            }
            solution[selectedCol] = 1;

            uncoveredRows = getUncoveredRows(solution);
        }
        return Arrays.copyOf(solution, COLUMNS);
    }

    private static int[] makeSolutionFeasibleBeasley(int[] solution) {
        int[] wi = new int[ROWS];
        int[] U = getUncoveredColumnsBeasley(solution, wi);
        while (U.length > 0) {
            int i = U[0];
            int selectedColumn = selectColumnMinRatio(solution, i);
            solution[selectedColumn] = 1;
            U = getUncoveredColumnsBeasley(solution, wi);
        }
        return Arrays.copyOf(solution, COLUMNS);
    }

    public static int[] getUncoveredColumnsBeasley(int[] solution, int[] wi) {
        int[] coveringColumns = getCoveringColumns(solution);
        for (int i = 0; i < ROWS; i++) {
            int[] columnsCoveringRow = getColumnsCoveringRow(i);
            for (int ai : columnsCoveringRow) {
                for (int xj : coveringColumns) {
                    if (ai == xj) {
                        wi[i]++;
                    }
                }
            }
        }

        int index = 0;
        int[] temp = new int[ROWS];
        for (int i = 0; i < wi.length; i++) {
            if (wi[i] == 0) {
                temp[index] = i;
                index++;
            }
        }
        return Arrays.copyOf(temp, index);
    }

    private static int[] removeRedundantColumnsR(int[] solution) {
        boolean feasible = true;
        while (feasible) {
            int j = selectColumnMaxRatio(solution);
            solution[j] = 0;
            int[] uncoveredRows = getUncoveredRows(solution);
            if (uncoveredRows.length > 0) {
                solution[j] = 1;
                feasible = false;
            }
        }
        return Arrays.copyOf(solution, COLUMNS);
    }
}
