package main.java.localsearch;

import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static main.java.variables.ScpVars.COLUMNS;
import static main.java.variables.ScpVars.getCost;

public class RowWeightedMutation {

    public RowWeightedMutation() {
    }

    public BitSet applyLocalSearch(BitSet xj) {
        List<Double> pjk = IntStream.range(0, COLUMNS)
                .boxed()
                .mapToDouble(j -> (double) 1 / getCost(j))
                .boxed()
                .collect(Collectors.toList());

        List<Double> sjk = IntStream.range(0, COLUMNS)
                .boxed()
                .mapToDouble(j -> {
                    if (xj.get(j)) {
                        return -Math.round(pjk.get(j) * 100.0) / 100.0;
                    } else {
                        return Math.round(pjk.get(j) * 100.0) / 100.0;
                    }
                })
                .boxed()
                .collect(Collectors.toList());

        return null;
    }

}
