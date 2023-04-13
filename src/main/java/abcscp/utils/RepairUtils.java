package abcscp.utils;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static abcscp.utils.CommonUtils.getColumns;
import static abcscp.utils.CommonUtils.randomNumber;
import static abcscp.utils.Parameters.COLUMNSCOVERINGROW;
import static abcscp.utils.Parameters.COSTS;
import static abcscp.utils.Parameters.RC_SIZE;
import static abcscp.utils.Parameters.ROWSCOVEREDBYCOLUMN;

public class RepairUtils {
    private RepairUtils() {
    }

    public static int getColumnMinRatio(List<Integer> uncoveredRows, int rowIndex) {
        List<Integer> ai = COLUMNSCOVERINGROW.get(rowIndex);
        List<Double> ratioList = new ArrayList<>();

        for (int columnIndex : ai) {
            List<Integer> rowsCovered = ROWSCOVEREDBYCOLUMN.get(columnIndex);
            List<Integer> uncoveredRowsCovered = getUncoveredRowsCoveredByColumn(uncoveredRows, rowsCovered);
            double ratio = (double) COSTS.get(columnIndex) / uncoveredRowsCovered.size();
            ratioList.add(ratio);
        }

        double ratioMin = ratioList.get(0);
        int selectedColumn = ai.get(0);

        for (int x = 0; x < ratioList.size(); x++) {
            if (ratioMin > ratioList.get(x)) {
                ratioMin = ratioList.get(x);
                selectedColumn = ai.get(x);
            }
        }
        return selectedColumn;
    }

    public static int getColumnMinRatioStream(List<Integer> uncoveredRows, int rowIndex) {
        List<Integer> ai = COLUMNSCOVERINGROW.get(rowIndex);

        return ai.stream()
                .map(columnIndex -> {
                    List<Integer> rowsCovered = ROWSCOVEREDBYCOLUMN.get(columnIndex);
                    List<Integer> uncoveredRowsCovered = getUncoveredRowsCoveredByColumn(uncoveredRows, rowsCovered);
                    double ratio = (double) COSTS.get(columnIndex) / uncoveredRowsCovered.size();
                    return new Tuple2<>(columnIndex, ratio);
                })
                .min(Comparator.comparing(Tuple2::getT2))
                .map(Tuple2::getT1)
                .get();
    }

    public static List<Integer> getUncoveredRowsCoveredByColumn(
            List<Integer> uncoveredRows, List<Integer> rowsCoveredByColumn) {
        return uncoveredRows.stream()
                .filter(rowsCoveredByColumn::contains)
                .collect(Collectors.toList());
/*
        List<Integer> uncoveredRowsCovered = new ArrayList<>();
        for (int rowIndex : uncoveredRows) {
            if (rowsCoveredByColumn.contains(rowIndex)) {
                uncoveredRowsCovered.add(rowIndex);
            }
        }
        return uncoveredRowsCovered;
 */
    }

    public static int selectRandomColumnFromRCL(int i) {
        int random = randomNumber(RC_SIZE);
        List<Integer> columnsCovering = COLUMNSCOVERINGROW.get(i);
        return columnsCovering.get(random);
    }

    public static int getColumnMaxRatio(BitSet solution) {
        List<Integer> columnsCovering = getColumns(solution);
        List<Double> ratioList = new ArrayList<>();

        for (int columnIndex : columnsCovering) {
            List<Integer> rowsCovered = ROWSCOVEREDBYCOLUMN.get(columnIndex);
            double ratio = (double) COSTS.get(columnIndex) / rowsCovered.size();
            ratioList.add(ratio);
        }

        double ratioMax = ratioList.get(0);
        int selectedColumn = columnsCovering.get(0);

        for (int x = 0; x < ratioList.size(); x++) {
            if (ratioMax < ratioList.get(x)) {
                ratioMax = ratioList.get(x);
                selectedColumn = columnsCovering.get(x);
            }
        }
        return selectedColumn;
    }

    public static int getColumnMaxRatioStream(BitSet solution) {
        List<Integer> columnsCovering = getColumns(solution);
        return columnsCovering.stream()
                .map(columnIndex -> {
                    List<Integer> rowsCovered = ROWSCOVEREDBYCOLUMN.get(columnIndex);
                    double ratio = (double) COSTS.get(columnIndex) / rowsCovered.size();
                    return new Tuple2<>(columnIndex, ratio);
                })
                .max(Comparator.comparing(Tuple2::getT2))
                .map(Tuple2::getT1)
                .get();
    }
}
