package abc;

import abc.scp.Bee;
import abc.scp.Instance;

import java.io.IOException;
import java.util.logging.Logger;

import static abc.scp.Params.GLOBAL_MIN;
import static abc.scp.Params.GLOBAL_MINS;
import static abc.scp.Params.MAX_CYCLE;
import static abc.scp.Params.RUNTIME;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    static Bee bee = new Bee();
    static Instance instance = new Instance();

    public static void main(String[] args) {
        double mean = 0;
        GLOBAL_MINS = new double[RUNTIME];
        try {
//            File file = new File("src/main/resources/scpnre1.txt");
//            String[] fileNames = file.list();
//            for (String fileName : fileNames) {
            System.out.println("START FILE [scpnre1.txt] PROCESSING");
            instance.read("src/main/resources/scpnre1.txt");
            for (int run = 0; run < RUNTIME; run++) {
                bee.initial();
                bee.memorizeBestSource();

                for (int iter = 0; iter < MAX_CYCLE; iter++) {
                    bee.sendEmployedBees();
                    bee.calculateProbabilities2();
                    bee.sendOnlookerBees();
                    bee.sendScoutBees();
                    bee.memorizeBestSource();
                    System.out.println((iter + 1) + ".iteration:" + GLOBAL_MIN);
                }

                    /*
                    StringBuilder builder = new StringBuilder("[");
                    for (int j = 0; j < COLUMNS; j++) {
                        // System.out.println("GlobalParam[%d]: %f\n", j + 1, GLO[j]);
                        // System.out.println("GlobalParam[" + (j + 1) + "]:" + GLOBAL_PARAMS[j]);
                        builder.append(GLOBAL_PARAMS[j]).append(", ");
                    }
                    builder.append("]");
                    System.out.println("GlobalParams: ");
                    System.out.println(builder);
                    */

                //System.out.println("%d. run: %D \n", run + 1, GLOBAL_MIN);
                System.out.println((run + 1) + ".run:" + GLOBAL_MIN);
                GLOBAL_MINS[run] = GLOBAL_MIN;
                mean = mean + GLOBAL_MIN;
            }
            mean = mean / RUNTIME;
            // System.out.println("Means of %d runs: %e\n", runtime, mean);
            System.out.println("Means  of " + RUNTIME + " runs: " + mean);


//            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}