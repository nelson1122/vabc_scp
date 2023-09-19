package main.java.localsearch;

import main.java.utils.CommonUtils;
import main.java.utils.RepairUtils;
import main.java.utils.Tuple2;
import main.java.utils.Tuple3;
import main.java.variables.AbcVars;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static main.java.variables.ScpVars.COLUMNS;
import static main.java.variables.ScpVars.ROWS;
import static main.java.variables.ScpVars.getColumnsCoveringRow;
import static main.java.variables.ScpVars.getCost;
import static main.java.variables.ScpVars.getRowsCoveredByColumn;

public class RowWeightedMutation {

    private final AbcVars vr;
    private final CommonUtils cUtils;
    private final RepairUtils rUtils;
    private Integer[] wi;
    private Double[] pj;
    private Double[] sj;
    private final int STOPCRITERIA = 50;

    public RowWeightedMutation(AbcVars vr) {
        this.vr = vr;
        this.cUtils = new CommonUtils(vr);
        this.rUtils = new RepairUtils(vr);
        this.wi = new Integer[ROWS];
        this.pj = new Double[COLUMNS];
        this.sj = new Double[COLUMNS];
    }

    public BitSet apply(BitSet xj, int foodNumber) {
        Arrays.fill(wi, 1);
        Arrays.fill(pj, 0.0);
        Arrays.fill(sj, 0.0);

        xj = applyMutationLocalSearch(xj, foodNumber);
        return xj;
    }

    public BitSet applyMutationLocalSearch(BitSet xj, int foodNumber) {
        BitSet xjMutation = (BitSet) xj.clone();
        List<Integer> uncoveredRows = cUtils.uncoveredRowsStream(xjMutation);

        calculateInitialPriority();
        calculateInitialScore(xjMutation);
        int t = 0;

        boolean improved = true;

        while (improved) {

//            if (uncoveredRows.isEmpty() && improved) {
//                break;
//            }

            while (uncoveredRows.isEmpty()) {
                double maxScore = xjMutation.stream()
                        .boxed()
                        .mapToDouble(j -> sj[j])
                        .max()
                        .getAsDouble();

                int colDrop = xjMutation.stream()
                        .filter(j -> sj[j] == maxScore)
                        .boxed()
                        .map(j -> new Tuple2<>(j, vr.getFoodBits(foodNumber)[j]))
                        .sorted(Comparator.comparing(Tuple2::getT2))
                        .map(Tuple2::getT1)
                        .collect(Collectors.toList()).get(0);

                updateSolutionDrop(foodNumber, colDrop, xjMutation);
                uncoveredRows = cUtils.uncoveredRowsStream(xjMutation);
                updateScore(colDrop, uncoveredRows);
            }

            while (!uncoveredRows.isEmpty()) {
                int randomRow = cUtils.randomNumber(uncoveredRows.size());
                int uncoveredRow = uncoveredRows.get(randomRow);
                List<Integer> cols = getColumnsCoveringRow(uncoveredRow);

                double maxScore = cols.stream()
                        .mapToDouble(j -> sj[j])
                        .max()
                        .getAsDouble();

                int colAdd = cols.stream()
                        .filter(j -> sj[j] == maxScore)
                        .map(j -> new Tuple2<>(j, vr.getFoodBits(foodNumber)[j]))
                        .sorted(Comparator.comparing(Tuple2::getT2))
                        .map(Tuple2::getT1)
                        .collect(Collectors.toList()).get(0);

                updateSolutionAdd(foodNumber, colAdd, xjMutation);
                uncoveredRows = cUtils.uncoveredRowsStream(xjMutation);
                updateScoreColumnsInSolution(xjMutation, colAdd, uncoveredRows);
                updateRowWeights(uncoveredRows);
                updateScoreColumnsNotInSolution(xjMutation, colAdd, uncoveredRows);
            }

            improved = false;
            if (solutionImproved(xj, xjMutation)) {
                xj = (BitSet) xjMutation.clone();
                improved = true;
            }

            t++;
        }

        return xj;
    }

    private void updateSolutionAdd(int foodNumber, int columnIndex, BitSet xj) {
        xj.set(columnIndex);
        vr.increaseFoodBits(foodNumber, columnIndex);
        // vr.setFitness(foodNumber, vr.getFitness(foodNumber) + getCost(columnIndex));
        sj[columnIndex] = (-1) * sj[columnIndex];
    }

    private void updateSolutionDrop(int foodNumber, int columnIndex, BitSet xj) {
        xj.clear(columnIndex);
        vr.increaseFoodBits(foodNumber, columnIndex);
        // vr.setFitness(foodNumber, vr.getFitness(foodNumber) - getCost(columnIndex));
    }

    private void calculateInitialPriority() {
        pj = IntStream.range(0, COLUMNS)
                .boxed()
                .map(j -> {
                    List<Integer> Bj = getRowsCoveredByColumn(j);
                    return (double) Bj.size() / getCost(j);
                })
                .toArray(size -> new Double[COLUMNS]);
    }

    private void calculateInitialScore(BitSet xj) {
        sj = IntStream.range(0, COLUMNS)
                .boxed()
                .map(j -> {
                    if (xj.get(j)) {
                        return (-1) * Math.round(pj[j] * 100.0) / 100.0;
                    } else {
                        return Math.round(pj[j] * 100.0) / 100.0;
                    }
                })
                .toArray(size -> new Double[COLUMNS]);
    }

    private void updateScore(int columnIndex, List<Integer> uncoveredRows) {
        List<Integer> rowsCoveredByColumn = getRowsCoveredByColumn(columnIndex);
        List<Integer> uncoveredRowsCovered =
                rUtils.getUncoveredRowsCoveredByColumn(uncoveredRows, rowsCoveredByColumn);

        double score = uncoveredRowsCovered.stream()
                .mapToDouble(j -> (double) wi[j] / getCost(j))
                .sum();
        score = Math.round(score * 100.0) / 100.0;
        sj[columnIndex] = score;
    }

    private void updateScoreColumnsInSolution(BitSet xj, int columnIndex, List<Integer> uncoveredRows) {
        BitSet xjc = (BitSet) xj.clone();
        List<Integer> rowsCoveredByColumn = getRowsCoveredByColumn(columnIndex);
        List<Integer> uncoveredRowsCovered =
                rUtils.getUncoveredRowsCoveredByColumn(uncoveredRows, rowsCoveredByColumn);

        xjc.stream()
                .boxed()
                .filter(j -> !j.equals(columnIndex))
                .forEach(j -> {
                    double score = sj[j] + uncoveredRowsCovered.stream()
                            .mapToDouble(ui -> (double) wi[ui] / getCost(ui))
                            .sum();
                    score = Math.round(score * 100.0) / 100.0;
                    sj[j] = score;
                });
    }

    private void updateRowWeights(List<Integer> uncoveredRows) {
        uncoveredRows.forEach(i -> wi[i]++);
    }

    private void updateScoreColumnsNotInSolution(BitSet xj, int columnIndex, List<Integer> uncoveredRows) {
        List<Integer> rowsCoveredByColumn = getRowsCoveredByColumn(columnIndex);
        List<Integer> uncoveredRowsCovered =
                rUtils.getUncoveredRowsCoveredByColumn(uncoveredRows, rowsCoveredByColumn);

        IntStream.range(0, COLUMNS)
                .boxed()
                .filter(j -> !xj.get(j))
                .forEach(j -> {
                    double score = sj[j] - uncoveredRowsCovered.stream()
                            .mapToDouble(ui -> (double) wi[ui] / getCost(ui))
                            .sum();
                    score = Math.round(score * 100.0) / 100.0;
                    sj[j] = score;
                });
    }


    private boolean solutionImproved(BitSet currXj, BitSet newXj) {
        int currFiness = cUtils.calculateFitnessOne(currXj);
        int newFitness = cUtils.calculateFitnessOneStream(newXj);
        return currFiness > newFitness;
    }
}
