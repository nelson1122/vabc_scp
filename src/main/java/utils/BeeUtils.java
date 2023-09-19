package main.java.utils;

import main.java.variables.AbcVars;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import static main.java.config.Parameters.COL_ADD_1;
import static main.java.config.Parameters.COL_ADD_2;
import static main.java.config.Parameters.COL_DROP_1;
import static main.java.config.Parameters.COL_DROP_2;


public class BeeUtils {
    private final AbcVars vr;
    private final CommonUtils cUtils;
    private List<Integer> addedColumns;

    public BeeUtils(AbcVars v) {
        this.vr = v;
        this.cUtils = new CommonUtils(v);
    }

    public void addColumns(int foodNumber, BitSet xj, List<Integer> distinctColumns) {
        addedColumns = new ArrayList<>();

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
                .map(distinctColumns::get)
                .distinct()
                .limit(colAdd)
                .boxed()
                .forEach(j -> {
                    addedColumns.add(j);
                    vr.increaseFoodBits(foodNumber, j);
                    xj.set(j);
                });
    }

    public void dropColumns(int foodNumber, BitSet xj) {
        int n = xj.cardinality();
        List<Integer> columns = cUtils.getBitsetIndexes(xj);
        int colDrop;

        if (n < 5) {
            colDrop = n;
        } else if (n > 35) {
            colDrop = COL_DROP_1;
        } else {
            colDrop = COL_DROP_2;
        }

        vr.getRANDOM().ints(0, n)
                .map(columns::get)
                .filter(j -> !addedColumns.contains(j))
                .distinct()
                .limit(colDrop)
                .boxed()
                .forEach(j -> {
                    vr.increaseFoodBits(foodNumber, j);
                    xj.clear(j);
                });
    }
}
