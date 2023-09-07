package main.java.initialization;

import main.java.utils.CommonUtils;
import main.java.utils.RepairUtils;
import main.java.utils.Tuple2;
import main.java.variables.AbcVars;
import main.java.variables.ScpVars;

import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static main.java.variables.ScpVars.COLUMNS;
import static main.java.variables.ScpVars.ROWS;
import static main.java.variables.ScpVars.getColumnsCoveringRow;
import static main.java.variables.ScpVars.getCost;
import static main.java.variables.ScpVars.getRowsCoveredByColumn;

public class RandomHeuristic {
    private final CommonUtils cUtils;
    private final RepairUtils rUtils;

    public RandomHeuristic(AbcVars v) {
        this.cUtils = new CommonUtils(v);
        this.rUtils = new RepairUtils(v);
    }

    public BitSet createSolution() {
        BitSet xj = new BitSet(COLUMNS);
        int randomColumn = cUtils.randomNumber(COLUMNS);
        xj.set(randomColumn);

        List<Integer> uncoveredRows = cUtils.uncoveredRowsStream(xj);
        while (!uncoveredRows.isEmpty()) {
            int j = applyHeuristic(xj);
            xj.set(j);
            uncoveredRows = cUtils.uncoveredRowsStream(xj);
        }
        removeRepeatedColumns(xj);
        return xj;
    }

    private int applyHeuristic(BitSet xj) {
        List<Integer> uncoveredRows = cUtils.uncoveredRowsStream(xj);
        List<Integer> lRows = uncoveredRows.stream()
                .map(i -> {
                    int Li = getColumnsCoveringRow(i).size();
                    return new Tuple2<>(i, 1.0 / Li);
                })
                //.sorted(Comparator.comparingDouble(Tuple2::getT2))
                .sorted(Collections.reverseOrder(Comparator.comparingDouble(Tuple2::getT2)))
                .limit(10)
                .map(Tuple2::getT1)
                .collect(Collectors.toList());

        List<Integer> lColumns = lRows.stream()
                .map(ScpVars::getColumnsCoveringRow)
                .flatMap(Collection::stream)
                .distinct()
                .map(j -> {
                    List<Integer> Mj = getRowsCoveredByColumn(j);
                    List<Integer> uncoveredRowsCovered = rUtils.getUncoveredRowsCoveredByColumn(uncoveredRows, Mj);
                    return new Tuple2<>(j, getCost(j) * 1.0 / uncoveredRowsCovered.size());
                })
                .sorted(Comparator.comparingDouble(Tuple2::getT2))
                //.sorted(Collections.reverseOrder(Comparator.comparingDouble(Tuple2::getT2)))
                .limit(5)
                .map(Tuple2::getT1)
                .collect(Collectors.toList());

        int randomColumn = cUtils.randomNumber(5);
        return lColumns.get(randomColumn);
    }

    private void removeRepeatedColumns(BitSet xj) {
        int[] u = new int[ROWS];
        for (int i = 0; i < ROWS; i++) {
            List<Integer> ai = getColumnsCoveringRow(i);
            for (int j = 0; j < COLUMNS; j++) {
                if (xj.get(j) && ai.contains(j)) {
                    u[i]++;
                }
            }
        }
        AtomicBoolean repeatedColumn = new AtomicBoolean(true);
        xj.stream()
                .boxed()
                .sorted(Collections.reverseOrder())
                .forEach(j -> {
                    List<Integer> Bj = getRowsCoveredByColumn(j);
                    for (int column : Bj) {
                        if (u[column] < 2) {
                            repeatedColumn.set(false);
                        }
                    }
                    if (repeatedColumn.get()) {
                        xj.clear(j);
                        for (int index : Bj) {
                            u[index]--;
                        }
                    }
                });

    }
}
