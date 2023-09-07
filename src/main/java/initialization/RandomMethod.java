package main.java.initialization;

import main.java.variables.AbcVars;

import java.util.BitSet;
import java.util.stream.IntStream;

import static main.java.variables.ScpVars.COLUMNS;

public class RandomMethod {
    private AbcVars vr;
    private final double RATIO = 0.5;

    public RandomMethod(AbcVars v) {
        this.vr = v;
    }

    public BitSet createSolution() {
        BitSet xj = new BitSet(COLUMNS);
        IntStream.range(0, COLUMNS)
                .boxed()
                .forEach(j -> {
                    double r = vr.getRANDOM().nextDouble() * 100.0 / 100.0;
                    double rNum = Math.round(r * 10) / 10.0;
                    xj.set(j);
                    if (rNum <= RATIO) {
                        xj.clear(j);
                    }
                });
        return xj;
    }

}
