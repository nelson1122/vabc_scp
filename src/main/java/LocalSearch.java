package main.java;

import main.java.localsearch.ABCSCP;
import main.java.localsearch.RowWeightedMutation;

import java.util.BitSet;

public class LocalSearch {
    private final ABCSCP abcscp;
    private final RowWeightedMutation rowWeightedMutation;

    public LocalSearch() {
        this.abcscp = new ABCSCP();
        this.rowWeightedMutation = new RowWeightedMutation();
    }

    public BitSet apply(BitSet xj) {
        // return xj;
        return this.abcscp.applyLocalSearch(xj);
        // return this.rowWeightedMutation.applyLocalSearch(xj);
    }
}
