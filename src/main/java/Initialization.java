package main.java;

import main.java.initialization.ABCSCP;
import main.java.initialization.IterativeConstruction;
import main.java.initialization.RandomHeuristic;
import main.java.initialization.RandomMethod;
import main.java.variables.AbcVars;

import java.util.BitSet;

public class Initialization {
    private final ABCSCP abcscp;
    private final RandomHeuristic randomHeuristic;
    private final RandomMethod randomMethod;
    private final IterativeConstruction iConstruction;

    public Initialization(AbcVars v) {
        this.abcscp = new ABCSCP(v);
        this.randomHeuristic = new RandomHeuristic(v);
        this.randomMethod = new RandomMethod(v);
        this.iConstruction = new IterativeConstruction(v);
    }

    public BitSet createSolution() {
        // return this.abcscp.createSolution();
        // return this.randomMethod.createSolution();
        // return this.randomHeuristic.createSolution();
        return this.iConstruction.createSolution();
    }
}
