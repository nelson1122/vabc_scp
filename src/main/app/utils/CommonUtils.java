package main.app.utils;

import main.app.variables.AbcVars;
import main.app.variables.ScpVars;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static main.app.config.Parameters.FOOD_NUMBER;
import static main.app.variables.ScpVars.COLUMNS;
import static main.app.variables.ScpVars.ROWS;
import static main.app.variables.ScpVars.getColumnsCoveringRow;
import static main.app.variables.ScpVars.getCost;
import static main.app.variables.ScpVars.getRowsCoveredByColumn;


public class CommonUtils {
    private AbcVars vr;

    public CommonUtils(AbcVars v) {
        this.vr = v;
    }

    public int randomNumber(int high) {
        int low = 0;
        return vr.getRANDOM().nextInt((high - low)) + low;
    }

    public int calculateFitnessOneStream(BitSet xj) {
        return xj.stream()
                .boxed()
                .map(ScpVars::getCost)
                .reduce(Integer::sum)
                .get();
    }

    public int calculateFitnessOne(BitSet xj) {
        int fitness = 0;
        for (int j = 0; j < COLUMNS; j++) {
            if (xj.get(j)) {
                fitness += getCost(j);
            }
        }
        return fitness;
    }

    public int calculateFitnessTwoStream(BitSet xj) {
        return xj.stream()
                .boxed()
                .map(ScpVars::getRowsCoveredByColumn)
                .map(List::size).reduce(Integer::sum)
                .get();
    }

    public int calculateFitnessTwo(BitSet xj) {
        int count = 0;
        for (int j = 0; j < COLUMNS; j++)
            if (xj.get(j)) {
                count += getRowsCoveredByColumn(j).size();
            }
        return count;
    }

    public int randomFoodSource(int i) {
        int randomFood = randomNumber(FOOD_NUMBER);
        if (i != randomFood) {
            return randomFood;
        }
        return randomFoodSource(i);
    }

    public List<Integer> distinctColumnsStream(BitSet i, BitSet j) {
        List<Integer> s1 = i.stream()
                .boxed()
                .collect(Collectors.toList());

        return j.stream()
                .boxed()
                .filter(cIndex -> !s1.contains(cIndex))
                .collect(Collectors.toList());
    }

    public List<Integer> distinctColumns(BitSet i, BitSet j) {
        List<Integer> distinctColumns = new ArrayList<>();
        for (int x = 0; x < COLUMNS; x++) {
            if (!i.get(x) && j.get(x)) {
                distinctColumns.add(x);
            }
        }
        return distinctColumns;
    }

    public List<Integer> uncoveredRows(BitSet solution) {
        int[] w = new int[ROWS];
        List<Integer> uncoveredRows = new ArrayList<>();
        for (int i = 0; i < ROWS; i++) {
            List<Integer> ai = getColumnsCoveringRow(i);
            for (int j = 0; j < COLUMNS; j++) {
                if (solution.get(j) && ai.contains(j)) {
                    w[i]++;
                }
            }
        }
        for (int i = 0; i < ROWS; i++) {
            if (w[i] == 0) {
                uncoveredRows.add(i);
            }
        }
        return uncoveredRows;
    }

    public List<Integer> uncoveredRowsStream(BitSet solution) {
        List<Integer> coveredRows =
                solution.stream()
                        .boxed()
                        .map(ScpVars::getRowsCoveredByColumn)
                        .flatMap(Collection::stream)
                        .distinct()
                        .collect(Collectors.toList());
        return IntStream
                .range(0, ROWS)
                .boxed()
                .filter(x -> !coveredRows.contains(x))
                .collect(Collectors.toList());
    }

    public List<Integer> getColumns(BitSet solution) {
        return solution.stream()
                .boxed()
                .collect(Collectors.toList());
    }

    public List<Integer> getColumnsRandomFoodSource(BitSet solution, int i) {
        int randomFoodS = randomFoodSource(i);
        List<Integer> distinctColumns = distinctColumnsStream(solution, vr.getFOODS().get(randomFoodS));
        if (!distinctColumns.isEmpty()) {
            return distinctColumns;
        }
        return getColumnsRandomFoodSource(solution, i);
    }
}
