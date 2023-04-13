package abcscp;

import java.util.BitSet;
import java.util.List;

import static abcscp.utils.CommonUtils.getColumns;
import static abcscp.utils.CommonUtils.randomNumber;
import static abcscp.utils.Parameters.COLUMNS;
import static abcscp.utils.Parameters.COLUMNSCOVERINGROW;
import static abcscp.utils.Parameters.RC_SIZE;
import static abcscp.utils.Parameters.ROWS;
import static abcscp.utils.Parameters.ROWSCOVEREDBYCOLUMN;


public class Solution {

    private Solution() {
    }

    public static BitSet createSolution() {
        BitSet solution = new BitSet(COLUMNS);
        int[] U = new int[ROWS];
        generateSolution(solution, U);
        removeRedundantColumns(solution, U);
        return solution;
    }

    private static void generateSolution(BitSet solution, int[] U) {
        for (int i = 0; i < ROWS; i++) {
            List<Integer> ai = COLUMNSCOVERINGROW.get(i);

            int randomRC = randomNumber(RC_SIZE);
            int j = ai.get(randomRC);

            if (!solution.get(j)) {
                solution.set(j);
                List<Integer> Bj = ROWSCOVEREDBYCOLUMN.get(j);
                for (int index : Bj) {
                    U[index]++;
                }
            }
        }
    }

    private static void removeRedundantColumns(BitSet solution, int[] U) {
        int t = solution.cardinality();
        while (t > 0) {
            List<Integer> columns = getColumns(solution);
            int j = columns.get(randomNumber(t));
            List<Integer> Bj = ROWSCOVEREDBYCOLUMN.get(j);

            boolean flag = true;
            for (int index : Bj) {
                if (U[index] < 2) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                solution.set(j, false);
                for (int index : Bj) {
                    U[index]--;
                }
            }
            t--;
        }
    }

}
