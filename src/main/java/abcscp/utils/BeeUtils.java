package abcscp.utils;

import java.util.BitSet;
import java.util.List;
import java.util.Random;

import static abcscp.utils.CommonUtils.getColumns;
import static abcscp.utils.Parameters.COL_ADD_1;
import static abcscp.utils.Parameters.COL_ADD_2;
import static abcscp.utils.Parameters.COL_DROP_1;
import static abcscp.utils.Parameters.COL_DROP_2;

public class BeeUtils {
    private static Random random = new Random();

    private BeeUtils() {
    }

    public static void addColumns(BitSet solution, List<Integer> distinctColumns) {
        int n = solution.cardinality();
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

        random.ints(0, dc)
                .distinct()
                .limit(colAdd)
                .boxed()
                .forEach(x -> {
                    int index = distinctColumns.get(x);
                    solution.set(index);
                });
/*
        while (colAdd > 0) {
            int randomNumber = randomNumber(dc);
            int j = distinctColumns.get(randomNumber);
            if (j != -1) {
                solution.set(j);
                distinctColumns.set(randomNumber, -1);
                colAdd--;
            }
        }
 */
    }

    public static void dropColumns(BitSet solution) {
        int n = solution.cardinality();
        List<Integer> columns = getColumns(solution);
        int colDrop;
        if (columns.size() < 5) {
            colDrop = columns.size();
        } else if (n > 35) {
            colDrop = COL_DROP_1;
        } else {
            colDrop = COL_DROP_2;
        }

        random.ints(0, n)
                .distinct()
                .limit(colDrop)
                .boxed()
                .forEach(x -> {
                    int index = columns.get(x);
                    solution.set(index, false);
                });
/*
        while (colDrop > 0) {
            int randomNumber = randomNumber(n);
            int j = columns.get(randomNumber);
            if (j != -1) {
                solution.set(j, false);
                columns.set(randomNumber, -1);
                colDrop--;
            }
        }
*/
    }
}
