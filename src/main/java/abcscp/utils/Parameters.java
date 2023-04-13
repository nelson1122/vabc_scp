package abcscp.utils;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class Parameters {
    //Variables
    public static int ROWS;
    public static int COLUMNS;
    public static List<Integer> COSTS;
    public static List<List<Integer>> COLUMNSCOVERINGROW;
    public static List<List<Integer>> ROWSCOVEREDBYCOLUMN;
    public static List<BitSet> FOODS;
    public static BitSet GLOBAL_PARAMS;
    public static List<Integer> FITNESS;
    public static int[] TRIAL;
    public static List<Double> PROB;
    public static Integer GLOBAL_MIN;
    public static List<Integer> GLOBAL_MINS = new ArrayList<>();
    public static Double MEAN = 0d;

    //Params
    public static int RUNTIME = 10;
    public static int RC_SIZE = 6;
    public static int EMPLOYED_BEES = 50;
    public static int ONLOOKER_BEES = 150;
    public static int FOOD_NUMBER = 50; // Food number is equal to number of employee bee
    public static int MAX_CYCLE = 500; // 500
    public static double Pa = 0.9d;
    public static int COL_ADD_1 = 5;
    public static int COL_ADD_2 = 3;
    public static int COL_DROP_1 = 12;
    public static int COL_DROP_2 = 5;
    public static int LIMIT = 50;
}
