package abcscp.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static abcscp.config.Parameters.MAX_CYCLE;

public class Logger {
    public SimpleDateFormat FORMAT =
            new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

    public void log(Tuple3<Integer, Integer, BitSet> result) {
        log((result.getT1() + 1) + " run: " + result.getT2());
        List<String> indexes = result.getT3().stream()
                .mapToObj(String::valueOf)
                .collect(Collectors.toList());
        log("GLOBALPARAMS => {" + String.join(", ", indexes) + "}");
    }

    public void log(String message) {
        System.out.printf("%s ==> %s%n", FORMAT.format(new Date()), message);
    }

//    public void log(int count) {
//        double progress = ((double) (count * 100) / (RUNTIME * MAX_CYCLE));
//        System.out.print("[" + progress + "]%\r");
//    }

    public void printProgress(int run, int iter) {
        iter++;
        double value = ((double) (iter) / MAX_CYCLE) * 100;
        String percentage = String.format("%.1f", value);
        System.out.print("[ run:" + ++run + " | iter:" + iter + " ] => " + percentage + "%\r");
    }

    public void log(int run, int globalMin, BitSet globalParams) {
        log((run + 1) + ".run:" + globalMin);
        List<String> indexes = globalParams.stream()
                .mapToObj(String::valueOf)
                .collect(Collectors.toList());
        log("GLOBALPARAMS => {" + String.join(", ", indexes) + "}");
    }

    public void log(int[] runs) {
        List<String> logs = new ArrayList<>();
        for (int x = 0; x < runs.length; x++) {
            String progress = "";
            for (int y = 0; y <= runs[x]; y++) {
                progress = progress.concat("▓");
            }
            progress = "[ run " + (x + 1) + " | iter: " + runs[x] + "] ==> " + progress.concat(((double) runs[x] / MAX_CYCLE) * 100 + "%\n");
            logs.add(progress);
        }
        System.out.print(String.format("\033[%dA", 10));
        System.out.print(String.join("", logs));
    }

}
