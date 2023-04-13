package abcscp.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static abcscp.config.Parameters.MAX_CYCLE;
import static abcscp.config.Variables.COLUMNS;
import static abcscp.config.Variables.GLOBAL_MIN;
import static abcscp.config.Variables.GLOBAL_PARAMS;

public class PrintUtils {

    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

    private PrintUtils() {
    }

    public static void printProgress(int run, int iter) {
        iter++;
        double value = ((double) (iter) / MAX_CYCLE) * 100;
        String percentage = String.format("%.1f", value);
        System.out.print("[ run:" + ++run + " | iter:" + iter + " ] => " + percentage + "%\r");
    }

    public static void printIterationResults(int run) {
        System.out.println(format.format(new Date()) + " ==> " + (run + 1) + ".run:" + GLOBAL_MIN);
        List<String> indexes = new ArrayList<>();
        for (int j = 0; j < COLUMNS; j++) {
            if (GLOBAL_PARAMS.get(j)) {
                indexes.add(String.valueOf(j));
            }
        }
        System.out.println(format.format(new Date()) + " ==>  GLOBALPARAMS => {" + String.join(", ", indexes) + "}");
    }
}
