package main.java;

import main.java.localsearch.ABCSCP;
import main.java.localsearch.RowWeightedMutation;
import main.java.variables.AbcVars;

import java.util.BitSet;

public class LocalSearch {
    private final AbcVars vr;
    private final ABCSCP abcscp;
    private final RowWeightedMutation rowWeightedMutation;
    private int foodNumber;

    public LocalSearch(AbcVars vr) {
        this.vr = vr;
        this.abcscp = new ABCSCP();
        this.rowWeightedMutation = new RowWeightedMutation(vr);
    }

    public BitSet apply(BitSet xj) {
        // return xj;
        // return this.abcscp.applyLocalSearch(xj);

        return this.rowWeightedMutation.apply(xj, this.foodNumber);
    }

    public void setFoodNumber(int foodNumber) {
        this.foodNumber = foodNumber;
    }

}
