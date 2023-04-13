package abcscp;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static abcscp.utils.CommonUtils.uncoveredRowsStream;
import static abcscp.utils.Parameters.Pa;
import static abcscp.utils.RepairUtils.getColumnMaxRatio;
import static abcscp.utils.RepairUtils.getColumnMinRatioStream;
import static abcscp.utils.RepairUtils.selectRandomColumnFromRCL;


public class Repair {
    private static long seed = System.currentTimeMillis();
    private static Random random = new Random(seed);

    private Repair() {

    }

    public static void applyRepairSolution(BitSet solution, List<Integer> uncoveredRows) {
        makeSolutionFeasible(solution, uncoveredRows);
        removeRedundantColumnsRecursive(solution);
    }

    private static void makeSolutionFeasible(BitSet solution, List<Integer> uncoveredRows) {
//        double r = (Math.random() * 32767 / ((double) 32767 + (double) (1)));
        double r = (random.nextDouble() * 100.0) / 100.0;
        while (!uncoveredRows.isEmpty()) {
            int indexRowUncovered = uncoveredRows.get(0);
            int indexColumn;
            if (r < Pa) {
                indexColumn = getColumnMinRatioStream(uncoveredRows, indexRowUncovered);
            } else {
                indexColumn = selectRandomColumnFromRCL(indexRowUncovered);
            }
            solution.set(indexColumn);
            uncoveredRows = uncoveredRowsStream(solution);
        }
    }

    private static void removeRedundantColumns(BitSet solution) {
        boolean feasible = true;
        while (feasible) {
            int columnIndex = getColumnMaxRatio(solution);
            solution.set(columnIndex, false);
            List<Integer> uncoveredRows = uncoveredRowsStream(solution);
            if (!uncoveredRows.isEmpty()) {
                solution.set(columnIndex);
                feasible = false;
            }
        }
    }

    private static void removeRedundantColumnsRecursive(BitSet solution) {
        solution.stream()
                .boxed()
                .sorted(Collections.reverseOrder())
                .forEach(columnIndex -> {
                    solution.set(columnIndex, false);
                    List<Integer> uncoveredRows = uncoveredRowsStream(solution);
                    if (!uncoveredRows.isEmpty()) {
                        solution.set(columnIndex);
                    }
                });
    }
}
