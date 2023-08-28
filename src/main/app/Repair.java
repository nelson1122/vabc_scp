package main.app;

import main.app.utils.CommonUtils;
import main.app.utils.RepairUtils;
import main.app.variables.AbcVars;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import static main.app.config.Parameters.Pa;


public class Repair {
    private AbcVars vr;
    private CommonUtils cUtils;
    private RepairUtils rUtils;

    public Repair(AbcVars v) {
        this.vr = v;
        this.cUtils = new CommonUtils(v);
        this.rUtils = new RepairUtils(v);
    }

    public void applyRepairSolution(BitSet solution, List<Integer> uncoveredRows) {
        makeSolutionFeasible(solution, uncoveredRows);
        removeRedundantColumnsRecursive(solution);
    }

    private void makeSolutionFeasible(BitSet solution, List<Integer> uncoveredRows) {
        double r = (vr.getRANDOM().nextDouble() * 100.0) / 100.0;
        while (!uncoveredRows.isEmpty()) {
            int indexRowUncovered = uncoveredRows.get(0);
            int indexColumn;
            if (r < Pa) {
                indexColumn = rUtils.getColumnMinRatioStream(uncoveredRows, indexRowUncovered);
            } else {
                indexColumn = rUtils.selectRandomColumnFromRCL(indexRowUncovered);
            }
            solution.set(indexColumn);
            uncoveredRows = cUtils.uncoveredRowsStream(solution);
        }
    }

    private void removeRedundantColumns(BitSet solution) {
        boolean feasible = true;
        while (feasible) {
            int columnIndex = rUtils.getColumnMaxRatio(solution);
            solution.set(columnIndex, false);
            List<Integer> uncoveredRows = cUtils.uncoveredRowsStream(solution);
            if (!uncoveredRows.isEmpty()) {
                solution.set(columnIndex);
                feasible = false;
            }
        }
    }

    private void removeRedundantColumnsRecursive(BitSet solution) {
        solution.stream()
                .boxed()
                .sorted(Collections.reverseOrder())
                .forEach(columnIndex -> {
                    solution.set(columnIndex, false);
                    List<Integer> uncoveredRows = cUtils.uncoveredRowsStream(solution);
                    if (!uncoveredRows.isEmpty()) {
                        solution.set(columnIndex);
                    }
                });
    }
}
