package abcscp;

import abcscp.config.Variables;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class Problem {

    private Variables vr;

    public Problem() {
    }

    public Problem(Variables v) {
        this.vr = v;
    }

    static final Logger LOGGER = Logger.getLogger(Problem.class.getName());

    public void read(String filePath) throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        System.out.println(format.format(new Date()) + " ==> Loading file [" + filePath + "] ...");

        File file = new File(filePath);
        Scanner scanner = new Scanner(file);
        vr.setROWS(scanner.nextInt());
        vr.setCOLUMNS(scanner.nextInt());

        vr.setCOSTS(new ArrayList<>());
        vr.setCOLUMNSCOVERINGROW(new ArrayList<>());
        vr.setROWSCOVEREDBYCOLUMN(new ArrayList<>());

        for (int j = 0; j < vr.getCOLUMNS(); j++) {
            vr.addCosts(scanner.nextInt());
        }

        for (int i = 0; i < vr.getROWS(); i++) {
            int numCol = scanner.nextInt();
            List<Integer> columns = new ArrayList<>();
            for (int j = 0; j < numCol; j++) {
                int column = scanner.nextInt() - 1;
                columns.add(column);
            }
            vr.addColumnsCoveringRow(columns);
        }

        for (int j = 0; j < vr.getCOLUMNS(); j++) {
            List<Integer> rows = new ArrayList<>();
            for (int i = 0; i < vr.getROWS(); i++) {
                List<Integer> columns = vr.getColumnsCoveringRow(i);
                if (columns.contains(j)) {
                    rows.add(i);
                }
            }
            vr.addRowsCoveredByColumn(rows);
        }

        scanner.close();
        System.out.println(format.format(new Date()) + " ==> File [" + filePath + "] loaded!...");
    }
}
