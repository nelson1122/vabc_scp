package main.app.initialization;

import java.util.ArrayList;
import java.util.List;

public class GreedyApproximationAlgorithm {

    public static List<Integer> setCoveringSolution(double[][] beta, double[] w) {
        List<Integer> S = new ArrayList<>();
        double[] betaPrime = new double[beta[0].length];

        for (int j = 0; j < beta[0].length; j++) {
            betaPrime[j] = beta[0][j];
        }

        while (!isUniverseEmpty(betaPrime)) {
            int l = findMinimumRatioColumn(beta, w, betaPrime);
            S.add(l);

            for (int j = 0; j < beta[0].length; j++) {
                betaPrime[j] -= beta[l][j];
            }

            betaPrime[l] = 0;
        }

        return S;
    }

    public static boolean isUniverseEmpty(double[] betaPrime) {
        for (double val : betaPrime) {
            if (val > 0) {
                return false;
            }
        }
        return true;
    }

    public static int findMinimumRatioColumn(double[][] beta, double[] w, double[] betaPrime) {
        int minRatioColumn = -1;
        double minRatio = Double.POSITIVE_INFINITY;

        for (int j = 0; j < beta[0].length; j++) {
            if (betaPrime[j] > 0) {
                double ratio = w[j] / betaPrime[j];
                if (ratio < minRatio) {
                    minRatio = ratio;
                    minRatioColumn = j;
                }
            }
        }

        return minRatioColumn;
    }

    public static void main(String[] args) {
        // Example usage
        double[][] beta = {
                {1, 0, 1},
                {0, 1, 1},
                {1, 1, 0},
                {0, 1, 0},
        };
        double[] w = {2, 3, 4};

        List<Integer> feasibleS = setCoveringSolution(beta, w);

        System.out.println("Feasible S: " + feasibleS);
    }
}
