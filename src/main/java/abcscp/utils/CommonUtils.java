package abcscp.utils;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static abcscp.utils.Parameters.COLUMNS;
import static abcscp.utils.Parameters.COLUMNSCOVERINGROW;
import static abcscp.utils.Parameters.COSTS;
import static abcscp.utils.Parameters.FOODS;
import static abcscp.utils.Parameters.FOOD_NUMBER;
import static abcscp.utils.Parameters.ROWS;
import static abcscp.utils.Parameters.ROWSCOVEREDBYCOLUMN;

public class CommonUtils {

    private CommonUtils() {
    }

    public static int randomNumber(int high) {
        Random r = new Random();
        int low = 0;
        return r.nextInt((high - low)) + low;
    }

    public static int calculateFitnessOneStream(BitSet xj) {
        return xj.stream()
                .boxed()
                .map(x -> COSTS.get(x))
                .reduce((i, j) -> i + j).get();

    }

    public static int calculateFitnessOne(BitSet xj) {
        int fitness = 0;
        for (int j = 0; j < COLUMNS; j++) {
            if (xj.get(j)) {
                fitness += COSTS.get(j);
            }
        }
        return fitness;
    }

    public static int calculateFitnessTwoStream(BitSet xj) {
        return xj.stream()
                .boxed()
                .map(ROWSCOVEREDBYCOLUMN::get)
                .map(List::size).reduce(Integer::sum)
                .get();
    }

    public static int calculateFitnessTwo(BitSet xj) {
        int count = 0;
        for (int j = 0; j < COLUMNS; j++)
            if (xj.get(j)) {
                count += ROWSCOVEREDBYCOLUMN.get(j).size();
            }
        return count;
    }

    public static int randomFoodSource(int i) {
        int randomFood = randomNumber(FOOD_NUMBER);
        if (i != randomFood) {
            return randomFood;
        }
        return randomFoodSource(i);
    }

    public static List<Integer> distinctColumnsStream(BitSet i, BitSet j) {
        List<Integer> s1 = i.stream()
                .boxed()
                .collect(Collectors.toList());

        return j.stream()
                .boxed()
                .filter(cIndex -> !s1.contains(cIndex))
                .collect(Collectors.toList());
    }

    public static List<Integer> distinctColumns(BitSet i, BitSet j) {
        List<Integer> distinctColumns = new ArrayList<>();
        for (int x = 0; x < COLUMNS; x++) {
            if (!i.get(x) && j.get(x)) {
                distinctColumns.add(x);
            }
        }
        return distinctColumns;
    }

    public static List<Integer> uncoveredRows(BitSet solution) {
        int[] w = new int[ROWS];
        List<Integer> uncoveredRows = new ArrayList<>();
        for (int i = 0; i < ROWS; i++) {
            List<Integer> ai = COLUMNSCOVERINGROW.get(i);
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

    public static List<Integer> uncoveredRowsStream(BitSet solution) {
        List<Integer> coveredRows =
                solution.stream()
                        .boxed()
                        .map(ROWSCOVEREDBYCOLUMN::get)
                        .flatMap(Collection::stream)
                        .distinct()
                        .collect(Collectors.toList());
        return IntStream
                .range(0, ROWS)
                .boxed()
                .filter(x -> !coveredRows.contains(x))
                .collect(Collectors.toList());
    }

    public static List<Integer> getColumns(BitSet solution) {
        return solution.stream()
                .boxed()
                .collect(Collectors.toList());
    }

    public static List<Integer> getColumnsRandomFoodSource(BitSet solution, int i) {
        int randomFoodS = randomFoodSource(i);
        List<Integer> distinctColumns = distinctColumnsStream(solution, FOODS.get(randomFoodS));
        if (!distinctColumns.isEmpty()) {
            return distinctColumns;
        }
        return getColumnsRandomFoodSource(solution, i);
    }
}
