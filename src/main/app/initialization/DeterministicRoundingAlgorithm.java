package main.app.initialization;

import java.util.ArrayList;
import java.util.List;

public class DeterministicRoundingAlgorithm {

    public static List<Integer> lpRelaxationSolution(double[] x, double f) {
        List<Integer> S = new ArrayList<>();

        for (int j = 0; j < x.length; j++) {
            if (x[j] >= 1 / f) {
                S.add(j);
            }
        }

        return S;
    }

    public static void main(String[] args) {
        // Example usage
        double[] x = {0.8, 0.5, 0.6, 0.9, 0.3};
        double f = 2.0;

        List<Integer> feasibleS = lpRelaxationSolution(x, f);

        System.out.println("Feasible S: " + feasibleS);
    }
}

