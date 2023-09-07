package main.java;

import main.java.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static main.java.variables.ScpVars.COLUMNS;
import static main.java.variables.ScpVars.COLUMNSCOVERINGROW;
import static main.java.variables.ScpVars.COSTS;
import static main.java.variables.ScpVars.ROWS;
import static main.java.variables.ScpVars.ROWSCOVEREDBYCOLUMN;

public class Problem {
    private static final Logger logger = new Logger();

    private Problem() {
    }

    public static void read(String filePath) throws IOException {
        logger.log("Loading problem [" + filePath + "] ...");

        File file = new File(filePath);
        Scanner scanner = new Scanner(file);

        ROWS = scanner.nextInt();
        COLUMNS = scanner.nextInt();
        COSTS = new ArrayList<>();
        COLUMNSCOVERINGROW = new ArrayList<>();
        ROWSCOVEREDBYCOLUMN = new ArrayList<>();

        for (int j = 0; j < COLUMNS; j++) {
            COSTS.add(scanner.nextInt());
        }

        for (int i = 0; i < ROWS; i++) {
            int numCol = scanner.nextInt();
            List<Integer> columns = new ArrayList<>();
            for (int j = 0; j < numCol; j++) {
                int column = scanner.nextInt() - 1;
                columns.add(column);
            }
            COLUMNSCOVERINGROW.add(columns);
        }

        for (int j = 0; j < COLUMNS; j++) {
            List<Integer> rows = new ArrayList<>();
            for (int i = 0; i < ROWS; i++) {
                List<Integer> columns = COLUMNSCOVERINGROW.get(i);
                if (columns.contains(j)) {
                    rows.add(i);
                }
            }
            ROWSCOVEREDBYCOLUMN.add(rows);
        }

        scanner.close();
        logger.log("Problem [" + filePath + "] has been loaded.");
    }
}
