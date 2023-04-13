package abc.scp;

public class Params {
    /* SCP Variables */
    public static int ROWS;
    public static int COLUMNS;
    public static int[][] A;
    public static int[] C;
    public static int[] SOLUTION;
    public static int[] U;

    /* ABC_SCP Params*/
    public static int RUNTIME = 10;
    public static int MAX_CYCLE = 50; // 500
    public static int LIMIT = 50;
    public static int RC_SIZE = 6;
    public static double Pa = 0.9d;
    public static int COL_ADD_1 = 5;
    public static int COL_ADD_2 = 3;
    public static int COL_DROP_1 = 12;
    public static int COL_DROP_2 = 5;
    public static int EMPLOYED_BEES = 50;
    public static int ONLOOKER_BEES = 150;

    public static int FOOD_NUMBER = 200;
    public static int FOODS[][];
    public static double FITNESS[];

    public static double GLOBAL_MIN;                       /*Optimum solution obtained by ABC algorithm*/
    public static int GLOBAL_PARAMS[];

    public static double[] GLOBAL_MINS;
    public static double TRIAL[];

    public static int FITNESS_SOL;
    public static double[] PROB;

}
