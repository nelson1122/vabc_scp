package main.app;

import main.app.utils.CommonUtils;
import main.app.variables.AbcVars;

import java.util.BitSet;
import java.util.List;

import static main.app.config.Parameters.RC_SIZE;
import static main.app.variables.ScpVars.COLUMNS;
import static main.app.variables.ScpVars.ROWS;
import static main.app.variables.ScpVars.getColumnsCoveringRow;
import static main.app.variables.ScpVars.getRowsCoveredByColumn;


public class Solution {
    private CommonUtils cUtils;

    public Solution(AbcVars v) {
        this.cUtils = new CommonUtils(v);
    }

    public BitSet createSolution() {
        BitSet solution = new BitSet(COLUMNS);
        int[] U = new int[ROWS];
        generateSolution(solution, U);
        removeRedundantColumns(solution, U);
        return solution;
    }

    private void generateSolution(BitSet solution, int[] U) {
        for (int i = 0; i < ROWS; i++) {
            List<Integer> ai = getColumnsCoveringRow(i);

            int randomRC = cUtils.randomNumber(RC_SIZE);
            int j = ai.get(randomRC);

            if (!solution.get(j)) {
                solution.set(j);
                List<Integer> Bj = getRowsCoveredByColumn(j);
                for (int index : Bj) {
                    U[index]++;
                }
            }
        }
    }

    private void removeRedundantColumns(BitSet solution, int[] U) {
        int t = solution.cardinality();
        while (t > 0) {
            List<Integer> columns = cUtils.getColumns(solution);
            int j = columns.get(cUtils.randomNumber(t));
            List<Integer> Bj = getRowsCoveredByColumn(j);

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
