package abcscp.utils;

import abcscp.config.Variables;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static abcscp.config.Parameters.MAX_CYCLE;

public class PrintUtils {

    private Variables vr;
    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

    public PrintUtils() {
    }

    public PrintUtils(Variables v) {
        this.vr = v;
    }

    public void printProgress(int run, int iter) {
        iter++;
        double value = ((double) (iter) / MAX_CYCLE) * 100;
        String percentage = String.format("%.1f", value);
        System.out.print("[ run:" + ++run + " | iter:" + iter + " ] => " + percentage + "%\r");
    }

    public void printIterationResults(int run) {
        System.out.println(format.format(new Date()) + " ==> " + (run + 1) + ".run:" + vr.getGLOBAL_MIN());
        List<String> indexes = new ArrayList<>();
        for (int j = 0; j < vr.getCOLUMNS(); j++) {
            if (vr.getGlobalParamsColumnValue(j)) {
                indexes.add(String.valueOf(j));
            }
        }
        System.out.println(format.format(new Date()) + " ==>  GLOBALPARAMS => {" + String.join(", ", indexes) + "}");
    }
}
