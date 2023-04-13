package abcscp.config;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public class Variables {
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
    public static Random RANDOM = new Random();
}
