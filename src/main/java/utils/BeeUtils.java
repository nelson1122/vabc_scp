package main.java.utils;

import main.java.variables.AbcVars;

import java.util.BitSet;
import java.util.List;

import static main.java.config.Parameters.COL_ADD_1;
import static main.java.config.Parameters.COL_ADD_2;
import static main.java.config.Parameters.COL_DROP_1;
import static main.java.config.Parameters.COL_DROP_2;


public class BeeUtils {
    private final AbcVars vr;
    private final CommonUtils cUtils;

    public BeeUtils(AbcVars v) {
        this.vr = v;
        this.cUtils = new CommonUtils(v);
    }

    public void addColumns(BitSet xj, List<Integer> distinctColumns) {
        int n = xj.cardinality();
        int dc = distinctColumns.size();
        int colAdd;
        if (n > 35) {
            colAdd = COL_ADD_1;
            if (dc < COL_ADD_1) {
                colAdd = dc;
            }
        } else {
            colAdd = COL_ADD_2;
            if (dc < COL_ADD_2) {
                colAdd = dc;
            }
        }

        vr.getRANDOM().ints(0, dc)
                .distinct()
                .limit(colAdd)
                .boxed()
                .forEach(x -> {
                    int index = distinctColumns.get(x);
                    xj.set(index);
                });
    }

    public void dropColumns(BitSet solution) {
        int n = solution.cardinality();
        List<Integer> columns = cUtils.getColumns(solution);
        int colDrop;
        if (n < 5) {
            colDrop = n;
        } else if (n > 35) {
            colDrop = COL_DROP_1;
        } else {
            colDrop = COL_DROP_2;
        }

        vr.getRANDOM().ints(0, n)
                .distinct()
                .limit(colDrop)
                .boxed()
                .forEach(x -> {
                    int index = columns.get(x);
                    solution.clear(index);
                });
    }
}
