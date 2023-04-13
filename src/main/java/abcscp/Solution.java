package abcscp;

import abcscp.config.Variables;
import abcscp.utils.CommonUtils;

import java.util.BitSet;
import java.util.List;

import static abcscp.config.Parameters.RC_SIZE;


public class Solution {

    private Variables var;
    private CommonUtils cUtils;

    public Solution(Variables v) {
        this.var = v;
        this.cUtils = new CommonUtils(v);
    }

    public BitSet createSolution() {
        BitSet solution = new BitSet(var.getCOLUMNS());
        int[] U = new int[var.getROWS()];
        generateSolution(solution, U);
        removeRedundantColumns(solution, U);
        return solution;
    }

    private void generateSolution(BitSet solution, int[] U) {
        for (int i = 0; i < var.getROWS(); i++) {
            List<Integer> ai = var.getColumnsCoveringRow(i);

            int randomRC = cUtils.randomNumber(RC_SIZE);
            int j = ai.get(randomRC);

            if (!solution.get(j)) {
                solution.set(j);
                List<Integer> Bj = var.getRowsCoveredByColumn(j);
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
            List<Integer> Bj = var.getRowsCoveredByColumn(j);

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
