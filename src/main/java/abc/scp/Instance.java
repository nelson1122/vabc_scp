package abc.scp;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static abc.scp.Params.A;
import static abc.scp.Params.C;
import static abc.scp.Params.COLUMNS;
import static abc.scp.Params.ROWS;

public class Instance {

    public void read(String filePath) throws IOException {
        File file = new File(filePath);
        Scanner scanner = new Scanner(file);
        ROWS = scanner.nextInt();
        COLUMNS = scanner.nextInt();
        C = new int[COLUMNS];
        A = new int[ROWS][COLUMNS];

        for (int j = 0; j < COLUMNS; j++) {
            C[j] = scanner.nextInt();
        }

        for (int i = 0; i < ROWS; i++) {
            int num = scanner.nextInt();
            for (int j = 0; j < num; j++) {
                int index = scanner.nextInt() - 1;
                A[i][index] = 1;
            }
        }
        scanner.close();
    }

}
