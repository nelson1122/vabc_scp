package main.app.initialization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomGreedyInitialization {

    private int[][] A;
    private int[] c;

    public RandomGreedyInitialization(int[][] A, int[] c) {
        this.A = A;
        this.c = c;
    }

    private List<Integer> createMap() {
        List<Integer> map = new ArrayList<>();
        for (int j = 0; j < A[0].length; j++) {
            map.add(j);
        }
        return map;
    }

    private int selectColumn(List<Integer> cols, double[] probabilities) {
        double rand = Math.random();
        double cumulativeProbability = 0.0;
        for (int i = 0; i < cols.size(); i++) {
            cumulativeProbability += probabilities[i];
            if (rand <= cumulativeProbability) {
                return cols.get(i);
            }
        }
        return cols.get(cols.size() - 1);
    }

    public List<Integer> randomGreedy() {
        List<Integer> x = new ArrayList<>(Collections.nCopies(A[0].length, 0));
        List<Integer> map = createMap();
        double[] f = new double[A[0].length];

        while (!map.isEmpty()) {
            List<Integer> cols = new ArrayList<>(map);
            for (int j : cols) {
                f[j] = Math.random(); // Random function, can be modified based on specific criteria

            }

            int j = 0;
            if (Math.random() <= 1.0 / cols.size()) {
                double total = 0.0;
                for (int col : cols) {
                    total += -f[col];
                }

                double[] probabilities = new double[cols.size()];
                for (int i = 0; i < cols.size(); i++) {
                    probabilities[i] = -f[cols.get(i)] / total;
                }

                j = selectColumn(cols, probabilities);
            } else {
                double min = Double.POSITIVE_INFINITY;
                for (int col : cols) {
                    if (f[col] < min) {
                        min = f[col];
                        j = col;
                    }
                }
            }

            int finalJ = j;
            map.removeIf(element -> A[element][finalJ] == 1);
            x.set(j, 1);
        }

        return x;
    }

    public static void main(String[] args) {
        int[][] A = {
                {1, 0, 1, 0, 0},
                {1, 1, 0, 1, 0},
                {0, 1, 0, 0, 1},
                {0, 0, 1, 1, 0},
                {1, 1, 1, 0, 1}
        };

        int[] c = {2, 3, 1, 2, 1};

        RandomGreedyInitialization algorithm = new RandomGreedyInitialization(A, c);
        List<Integer> solution = algorithm.randomGreedy();
        System.out.println(solution);
    }
}

