package main.java;

import main.java.localsearch.ABCSCP;

import java.util.BitSet;

public class LocalSearch {
    private final ABCSCP abcscp;

    public LocalSearch() {
        this.abcscp = new ABCSCP();
    }

    public BitSet apply(BitSet xj) {
        // return xj;
        return this.abcscp.applyLocalSearch(xj);
    }
}
