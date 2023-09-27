package main.java.tests;

import main.java.utils.CommonUtils;
import main.java.utils.RepairUtils;
import main.java.utils.Tuple2;
import main.java.variables.AbcVars;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static main.java.variables.ScpVars.COLUMNS;
import static main.java.variables.ScpVars.ROWS;
import static main.java.variables.ScpVars.getColumnsCoveringRow;
import static main.java.variables.ScpVars.getCost;
import static main.java.variables.ScpVars.getRowsCoveredByColumn;
import static main.java.variables.ScpVars.getRowsCoveredByColumnBitset;

public class RowWeightedMutationTest {

    private final AbcVars vr;
    private final CommonUtils cUtils;
    private final RepairUtils rUtils;
    private int[] w;
    private double[] p;
    private double[] s;
    private final int STOPCRITERIA = 50000;

    public RowWeightedMutationTest(AbcVars vr) {
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

        calculateInitialPriority();
        calculateInitialScore(xjMutation);
        int colDrop1 = 0;
        int colDrop2 = 0;
        int colAdd = 0;


        int t = 0;

        while (t < STOPCRITERIA) {
            colDrop1 = -1;
            colDrop2 = -1;
            colAdd = -1;
            while (uncoveredRows.isEmpty()) {
                if (solutionImproved(xj, xjMutation)) {
                    xj = (BitSet) xjMutation.clone();
                    return xj;
                } else {
                    xjMutation = (BitSet) xj.clone();
                }

                double maxScore1 = xjMutation.stream()
                        .boxed()
                        .mapToDouble(j -> s[j])
                        .max()
                        //.min()
                        .getAsDouble();

                colDrop1 = xjMutation.stream()
                        .filter(j -> s[j] == maxScore1)
                        .boxed()
                        .map(j -> new Tuple2<>(j, vr.getFoodBits(foodNumber)[j]))
                        //.sorted(Collections.reverseOrder(Comparator.comparing(Tuple2::getT2)))
                        .sorted(Comparator.comparing(Tuple2::getT2))
                        .map(Tuple2::getT1)
                        .toList().get(0);

                updateSolutionDrop(foodNumber, colDrop1, xjMutation);
                uncoveredRows = cUtils.uncoveredRowsStream(xjMutation);
                updateScore(colDrop1, uncoveredRows);

                // execute line 9-16 again?

                double maxScore2 = xjMutation.stream()
                        .boxed()
                        .mapToDouble(j -> s[j])
                        .max()
                        //.min()
                        .getAsDouble();

                colDrop2 = xjMutation.stream()
                        .filter(j -> s[j] == maxScore2)
                        .boxed()
                        .map(j -> new Tuple2<>(j, vr.getFoodBits(foodNumber)[j]))
                        //.sorted(Collections.reverseOrder(Comparator.comparing(Tuple2::getT2)))
                        .sorted(Comparator.comparing(Tuple2::getT2))
                        .map(Tuple2::getT1)
                        .toList().get(0);

                updateSolutionDrop(foodNumber, colDrop2, xjMutation);
                uncoveredRows = cUtils.uncoveredRowsStream(xjMutation);
                updateScore(colDrop2, uncoveredRows);
            }

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
                    .sorted(Collections.reverseOrder(Comparator.comparing(Tuple2::getT2)))
                    //.sorted(Comparator.comparing(Tuple2::getT2))
                    .map(Tuple2::getT1)
                    .toList().get(0);

            updateSolutionAdd(foodNumber, colAdd, xjMutation);
            uncoveredRows = cUtils.uncoveredRowsStream(xjMutation);
            updateScoreColumnsInSolution(xjMutation, colAdd);
            updateRowWeights(uncoveredRows);
            updateScoreColumnsNotInSolution(xjMutation, colAdd);

            t++;
        }
        System.out.println(t);

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
                .mapToDouble(i -> (double) w[i] / getCost(columnIndex))
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
        int currFiness = cUtils.calculateFitnessOneStream(currXj);
        int newFitness = cUtils.calculateFitnessOneStream(newXj);
        if (currFiness > newFitness) {
            System.out.println("solution improved LS ==> old [" + currFiness + "], new[" + newFitness + "]");
            return true;
        }
//        else if (currFiness == newFitness) {
//            int currFinessTwo = cUtils.calculateFitnessTwoStream(currXj);
//            int newFitnessTwo = cUtils.calculateFitnessTwoStream(newXj);
//            if (currFinessTwo > newFitnessTwo) {
//                System.out.println("solution improved LS ==> old [" + currFiness + "], new[" + newFitness + "]");
//                return true;
//            }
//        }
        return false;
    }
}
