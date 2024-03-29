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

    public void applyRepairSolution(BitSet xj, List<Integer> uncoveredRows) {
        makeSolutionFeasible(xj, uncoveredRows);
        removeRedundantColumnsStream(xj);
    }

    private void makeSolutionFeasible(BitSet xj, List<Integer> uncoveredRows) {
        while (!uncoveredRows.isEmpty()) {
            int indexRowUncovered = uncoveredRows.get(0);
            int indexColumn;

            double r = (vr.getRANDOM().nextDouble() * 100.0) / 100.0;
            double rNum = Math.round(r * 1000) / 1000.0;

            if (rNum <= Pa) {
                indexColumn = rUtils.getColumnMinRatioBitSet(uncoveredRows, indexRowUncovered);
            } else {
                indexColumn = rUtils.selectRandomColumnFromRCL(indexRowUncovered);
            }

            xj.set(indexColumn);
            // uncoveredRows = cUtils.uncoveredRowsStream(xj);
            uncoveredRows = cUtils.uncoveredRowsBitSet(uncoveredRows, indexColumn);
        }
    }

    /*
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
    */
    private void removeRedundantColumnsStream(BitSet xj) {
        BitSet xjc = (BitSet) xj.clone();
        xjc.stream()
                .boxed()
                .map(j -> {
                    List<Integer> rowsCovered = getRowsCoveredByColumn(j);
                    double ratio = (double) getCost(j) / rowsCovered.size();
                    return new Tuple2<>(j, ratio);
                })
                .sorted(Collections.reverseOrder(Comparator.comparing(Tuple2::getT2)))
                .map(Tuple2::getT1)
                .forEach(columnIndex -> {
                    xj.clear(columnIndex);
                    BitSet uncoveredRows = cUtils.uncoveredRowsBitset(xj);
                    if (!uncoveredRows.isEmpty()) {
                        xj.set(columnIndex);
                    }
                });
    }
}
