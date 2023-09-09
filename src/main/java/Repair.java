package main.java;

import main.java.utils.CommonUtils;
import main.java.utils.RepairUtils;
import main.java.utils.Tuple2;
import main.java.variables.AbcVars;

import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static main.java.config.Parameters.Pa;
import static main.java.variables.ScpVars.getCost;
import static main.java.variables.ScpVars.getRowsCoveredByColumn;


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
        while (!uncoveredRows.isEmpty()) {
            int randRow = cUtils.randomNumber(uncoveredRows.size());
            int indexRowUncovered = uncoveredRows.get(randRow);
            int indexColumn;

            double r = (vr.getRANDOM().nextDouble() * 100.0) / 100.0;
            double rNum = Math.round(r * 10) / 10.0;

            if (rNum <= Pa) {
                indexColumn = rUtils.getColumnMinRatioStream(uncoveredRows, indexRowUncovered);
            } else {
                indexColumn = rUtils.selectRandomColumnFromRCL(indexRowUncovered);
            }
            solution.set(indexColumn);
            // uncoveredRows = cUtils.uncoveredRowsStream(solution);
            cUtils.updateUncoveredRows(uncoveredRows, indexColumn);
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

    private void removeRedundantColumnsRecursive(BitSet xj) {
        xj.stream()
                .boxed()
                .map(j -> {
                    List<Integer> rowsCovered = getRowsCoveredByColumn(j);
                    double ratio = getCost(j) * 1.0 / rowsCovered.size();
                    return new Tuple2<>(j, ratio);
                })
                .sorted(Collections.reverseOrder(Comparator.comparing(Tuple2::getT2)))
                .map(Tuple2::getT1)
                .forEach(columnIndex -> {
                    xj.clear(columnIndex);
                    List<Integer> uncoveredRows = cUtils.uncoveredRowsStream(xj);
                    if (!uncoveredRows.isEmpty()) {
                        xj.set(columnIndex);
                    }
                });
    }
}
