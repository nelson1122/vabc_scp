package abcscp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import static abcscp.utils.Parameters.COLUMNS;
import static abcscp.utils.Parameters.COLUMNSCOVERINGROW;
import static abcscp.utils.Parameters.COSTS;
import static abcscp.utils.Parameters.ROWS;
import static abcscp.utils.Parameters.ROWSCOVEREDBYCOLUMN;

public class Problem {

    static final Logger LOGGER = Logger.getLogger(Problem.class.getName());

    public void read(String filePath) throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        System.out.println(format.format(new Date()) + " ==> Loading file [" + filePath + "] ...");

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
        System.out.println(format.format(new Date()) + " ==> File [" + filePath + "] loaded!...");
    }
}
