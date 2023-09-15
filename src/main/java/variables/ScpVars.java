package main.java.variables;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

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
        return new ArrayList<>(COLUMNSCOVERINGROW.get(i));
    }
    public static List<Integer> getRowsCoveredByColumn(int j) {
        return new ArrayList<>(ROWSCOVEREDBYCOLUMN.get(j));
    }

    public static BitSet getColumnsCoveringRowBitset(int i) {
        return COLUMNSCOVERINGROW.get(i).stream()
                .collect(BitSet::new, BitSet::set,BitSet::or);
    }
    public static BitSet getRowsCoveredByColumnBitset(int j) {
        return ROWSCOVEREDBYCOLUMN.get(j).stream()
                .collect(BitSet::new, BitSet::set,BitSet::or);
    }
}
