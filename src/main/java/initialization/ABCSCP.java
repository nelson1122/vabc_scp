package main.java.initialization;

import main.java.utils.CommonUtils;
import main.java.variables.AbcVars;

import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static main.java.config.Parameters.RC_SIZE;
import static main.java.variables.ScpVars.COLUMNS;
import static main.java.variables.ScpVars.ROWS;
import static main.java.variables.ScpVars.getColumnsCoveringRow;
import static main.java.variables.ScpVars.getRowsCoveredByColumn;


public class ABCSCP {
    private final AbcVars vr;
    private final CommonUtils cUtils;

    public ABCSCP(AbcVars v) {
        this.vr = v;
        this.cUtils = new CommonUtils(v);
    }

    public BitSet createSolution(int foodNumber) {
        BitSet xj = new BitSet(COLUMNS);
        int[] U = new int[ROWS];

        generateSolution(foodNumber, xj, U);
        removeRedundantColumns(foodNumber, xj, U);

        return xj;
    }

    private void generateSolution(int foodNumber, BitSet xj, int[] U) {
        IntStream.range(0, ROWS)
                .boxed()
                .forEach(i -> {
                    List<Integer> ai = getColumnsCoveringRow(i);

                    int randomRC = cUtils.randomNumber(RC_SIZE);
                    int j = ai.get(randomRC);

                    if (!xj.get(j)) {
                        xj.set(j);
                        vr.increaseFoodBits(foodNumber, j);
                        List<Integer> Bj = getRowsCoveredByColumn(j);
                        Bj.forEach(idx -> U[idx]++);
                    }
                });
        /*
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
         */
    }

    private void removeRedundantColumns(int foodNumber, BitSet xj, int[] U) {
        int numColumns = xj.cardinality() + 1;
        IntStream.range(1, numColumns)
                .boxed()
                .sorted(Comparator.reverseOrder())
                .forEach(t -> {
                    List<Integer> columns = cUtils.getBitsetIndexes(xj);
                    int j = columns.get(cUtils.randomNumber(t));
                    List<Integer> Bj = getRowsCoveredByColumn(j);

                    List<Integer> rowsCoveredByOneColumn =
                            Bj.stream().filter(i -> U[i] < 2)
                                    .collect(Collectors.toList());

                    if (rowsCoveredByOneColumn.isEmpty()) {
                        xj.clear(j);
                        vr.increaseFoodBits(foodNumber, j);
                        Bj.forEach(idx -> U[idx]--);
                    }
                });

/*
                    boolean flag = true;
                    for (int index : Bj) {
                        if (U[index] < 2) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        xj.clear(j);
                        for (int index : Bj) {
                            U[index]--;
                        }
                    }
                });

 */
        /*
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
         */
    }

}