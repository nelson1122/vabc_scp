package main.java.localsearch;

import main.java.utils.CommonUtils;
import main.java.utils.RepairUtils;
import main.java.utils.Tuple2;
import main.java.variables.AbcVars;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static main.java.config.Parameters.FOOD_NUMBER;
import static main.java.variables.ScpVars.COLUMNS;
import static main.java.variables.ScpVars.ROWS;
import static main.java.variables.ScpVars.getColumnsCoveringRow;
import static main.java.variables.ScpVars.getCost;
import static main.java.variables.ScpVars.getRowsCoveredByColumn;
import static main.java.variables.ScpVars.getRowsCoveredByColumnBitset;

public class RowWeightedMutation {

    private final AbcVars vr;
    private final CommonUtils cUtils;
    private final RepairUtils rUtils;
    private int[] w;
    private double[] p;
    private double[] s;
    private final int STOPCRITERIA = 50;

    public RowWeightedMutation(AbcVars vr) {
        this.vr = vr;
        this.cUtils = new CommonUtils(vr);
        this.rUtils = new RepairUtils(vr);
        this.w = new int[ROWS];
        this.p = new double[COLUMNS];
        this.s = new double[COLUMNS];
    }

    public BitSet apply(BitSet xj, int foodNumber) {
        Arrays.fill(w, 1);
        Arrays.fill(p, 0.0);
        Arrays.fill(s, 0.0);

        xj = applyMutationLocalSearch(xj, foodNumber);
        return xj;
    }

    public BitSet applyMutationLocalSearch(BitSet xj, int foodNumber) {
        BitSet xjMutation = (BitSet) xj.clone();
        List<Integer> uncoveredRows = cUtils.uncoveredRowsStream(xjMutation);

        int randomFood = cUtils.randomNumber(FOOD_NUMBER);
        BitSet rfs = vr.getFoodSource(randomFood);

        calculateInitialPriority();
        calculateInitialScore(rfs);
        int colDrop = 0;
        int colAdd = 0;

        boolean improved = true;

        while (improved) {
            improved = false;

            while (uncoveredRows.isEmpty()) {
                double maxScore = xjMutation.stream()
                        .boxed()
                        .mapToDouble(j -> s[j])
                        .max()
                        .getAsDouble();

                colDrop = xjMutation.stream()
                        .filter(j -> s[j] == maxScore)
                        .boxed()
                        .map(j -> new Tuple2<>(j, vr.getFoodBits(foodNumber)[j]))
                        .sorted(Comparator.comparing(Tuple2::getT2))
                        .map(Tuple2::getT1)
                        .toList().get(0);

                updateSolutionDrop(foodNumber, colDrop, xjMutation);
                uncoveredRows = cUtils.uncoveredRowsStream(xjMutation);
                updateScore(colDrop, uncoveredRows);
            }

            while (!uncoveredRows.isEmpty()) {
                int randomRow = cUtils.randomNumber(uncoveredRows.size());
                int uncoveredRow = uncoveredRows.get(randomRow);
                List<Integer> cols = getColumnsCoveringRow(uncoveredRow);

                double maxScore = cols.stream()
                        .mapToDouble(j -> s[j])
                        .max()
                        .getAsDouble();

                colAdd = cols.stream()
                        .filter(j -> s[j] == maxScore)
                        .map(j -> new Tuple2<>(j, vr.getFoodBits(foodNumber)[j]))
                        .sorted(Comparator.comparing(Tuple2::getT2))
                        .map(Tuple2::getT1)
                        .toList().get(0);

                updateSolutionAdd(foodNumber, colAdd, xjMutation);
                uncoveredRows = cUtils.uncoveredRowsStream(xjMutation);
                updateScoreColumnsInSolution(xjMutation, colAdd);
                updateRowWeights(uncoveredRows);
                updateScoreColumnsNotInSolution(xjMutation, colAdd);
            }

            if (solutionImproved(xj, xjMutation)) {
                xj = (BitSet) xjMutation.clone();
                improved = true;
            }
        }

        return xj;
    }

    private void updateSolutionAdd(int foodNumber, int columnIndex, BitSet xj) {
        xj.set(columnIndex);
        vr.increaseFoodBits(foodNumber, columnIndex);
        s[columnIndex] = (-1) * s[columnIndex];
    }

    private void updateSolutionDrop(int foodNumber, int columnIndex, BitSet xj) {
        xj.clear(columnIndex);
        vr.increaseFoodBits(foodNumber, columnIndex);
    }

    private void calculateInitialPriority() {
        IntStream.range(0, COLUMNS)
                .boxed()
                .forEach(j -> {
                    List<Integer> Bj = getRowsCoveredByColumn(j);
                    double priority = (double) Bj.size() / getCost(j);
                    p[j] = priority;
                });
    }

    private void calculateInitialScore(BitSet xj) {
        IntStream.range(0, COLUMNS)
                .boxed()
                .forEach(j -> {
                    double score;
                    if (xj.get(j)) {
                        score = (-1) * Math.round(p[j] * 100.0) / 100.0;
                    } else {
                        score = Math.round(p[j] * 100.0) / 100.0;
                    }
                    s[j] = score;
                });
    }

    private void updateScore(int columnIndex, List<Integer> uncoveredRows) {
        List<Integer> rowsCoveredByColumn = getRowsCoveredByColumn(columnIndex);
        List<Integer> uncoveredRowsCovered =
                rUtils.getUncoveredRowsCoveredByColumn(uncoveredRows, rowsCoveredByColumn);

        double score = uncoveredRowsCovered.stream()
                .mapToDouble(j -> (double) w[j] / getCost(columnIndex))
                .sum();
        score = Math.round(score * 100.0) / 100.0;
        s[columnIndex] = score;
    }

    private void updateScoreColumnsInSolution(BitSet xj, int columnIndex) {
        BitSet xjc = (BitSet) xj.clone();
        BitSet Bj = getRowsCoveredByColumnBitset(columnIndex);

        xjc.stream()
                .boxed()
                .filter(j -> !j.equals(columnIndex))
                .forEach(h -> {
                    BitSet Bh = getRowsCoveredByColumnBitset(h);
                    Bh.and(Bj);

                    double sum = 0.0;
                    for (int i : Bh.stream().boxed().toList()) {
                        sum += ((double) w[i] / getCost(h));
                    }

                    double score = s[h] + sum;

                    score = Math.round(score * 100.0) / 100.0;
                    s[h] = score;
                });
    }

    private void updateRowWeights(List<Integer> uncoveredRows) {
        uncoveredRows.forEach(i -> w[i]++);
    }

    private void updateScoreColumnsNotInSolution(BitSet xj, int columnIndex) {
        BitSet Bj = getRowsCoveredByColumnBitset(columnIndex);

        IntStream.range(0, COLUMNS)
                .boxed()
                .filter(j -> !xj.get(j))
                .forEach(h -> {
                    BitSet Bh = getRowsCoveredByColumnBitset(h);
                    Bh.and(Bj);

                    double sum = 0.0;
                    for (int i : Bh.stream().boxed().toList()) {
                        sum += ((double) w[i] / getCost(h));
                    }

                    double score = s[h] - sum;

                    score = Math.round(score * 100.0) / 100.0;
                    s[h] = score;
                });
    }


    private boolean solutionImproved(BitSet currXj, BitSet newXj) {
        int currFiness = cUtils.calculateFitnessOne(currXj);
        int newFitness = cUtils.calculateFitnessOneStream(newXj);
        return currFiness > newFitness;
    }
}
