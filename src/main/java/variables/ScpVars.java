package main.java.variables;

import java.util.List;

public class ScpVars {
    private ScpVars(){
    }
    public static int ROWS;
    public static int COLUMNS;
    public static List<Integer> COSTS;
    public static List<List<Integer>> COLUMNSCOVERINGROW;
    public static List<List<Integer>> ROWSCOVEREDBYCOLUMN;
    public static Integer getCost(int j) {
        return COSTS.get(j);
    }
    public static List<Integer> getColumnsCoveringRow(int i) {
        return COLUMNSCOVERINGROW.get(i);
    }
    public static List<Integer> getRowsCoveredByColumn(int j) {
        return ROWSCOVEREDBYCOLUMN.get(j);
    }
}
