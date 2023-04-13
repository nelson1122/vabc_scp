package abcscp.utils;

import abcscp.config.Variables;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static abcscp.config.Parameters.RC_SIZE;


public class RepairUtils {
    private Variables vr;
    private CommonUtils cUtils;
    public RepairUtils(Variables v) {
        this.vr = v;
        this.cUtils = new CommonUtils(v);
    }

    public int getColumnMinRatio(List<Integer> uncoveredRows, int rowIndex) {
        List<Integer> ai = vr.getCOLUMNSCOVERINGROW().get(rowIndex);
        List<Double> ratioList = new ArrayList<>();

        for (int columnIndex : ai) {
            List<Integer> rowsCovered = vr.getROWSCOVEREDBYCOLUMN().get(columnIndex);
            List<Integer> uncoveredRowsCovered = getUncoveredRowsCoveredByColumn(uncoveredRows, rowsCovered);
            double ratio = (double) vr.getCOSTS().get(columnIndex) / uncoveredRowsCovered.size();
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

    public int getColumnMinRatioStream(List<Integer> uncoveredRows, int rowIndex) {
        List<Integer> ai = vr.getCOLUMNSCOVERINGROW().get(rowIndex);

        return ai.stream()
                .map(columnIndex -> {
                    List<Integer> rowsCovered = vr.getROWSCOVEREDBYCOLUMN().get(columnIndex);
                    List<Integer> uncoveredRowsCovered = getUncoveredRowsCoveredByColumn(uncoveredRows, rowsCovered);
                    double ratio = (double) vr.getCOSTS().get(columnIndex) / uncoveredRowsCovered.size();
                    return new Tuple2<>(columnIndex, ratio);
                })
                .min(Comparator.comparing(Tuple2::getT2))
                .map(Tuple2::getT1)
                .get();
    }

    public List<Integer> getUncoveredRowsCoveredByColumn(
            List<Integer> uncoveredRows, List<Integer> rowsCoveredByColumn) {
        return uncoveredRows.stream()
                .filter(rowsCoveredByColumn::contains)
                .collect(Collectors.toList());
    }

    public int selectRandomColumnFromRCL(int i) {
        int random = cUtils.randomNumber(RC_SIZE);
        List<Integer> columnsCovering = vr.getCOLUMNSCOVERINGROW().get(i);
        return columnsCovering.get(random);
    }

    public int getColumnMaxRatio(BitSet solution) {
        List<Integer> columnsCovering = cUtils.getColumns(solution);
        List<Double> ratioList = new ArrayList<>();

        for (int columnIndex : columnsCovering) {
            List<Integer> rowsCovered = vr.getROWSCOVEREDBYCOLUMN().get(columnIndex);
            double ratio = (double) vr.getCOSTS().get(columnIndex) / rowsCovered.size();
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

    public int getColumnMaxRatioStream(BitSet solution) {
        List<Integer> columnsCovering = cUtils.getColumns(solution);
        return columnsCovering.stream()
                .map(columnIndex -> {
                    List<Integer> rowsCovered = vr.getROWSCOVEREDBYCOLUMN().get(columnIndex);
                    double ratio = (double) vr.getCOSTS().get(columnIndex) / rowsCovered.size();
                    return new Tuple2<>(columnIndex, ratio);
                })
                .max(Comparator.comparing(Tuple2::getT2))
                .map(Tuple2::getT1)
                .get();
    }
}
