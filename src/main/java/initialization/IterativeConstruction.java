package main.java.initialization;

import main.java.utils.CommonUtils;
import main.java.utils.RepairUtils;
import main.java.utils.Tuple2;
import main.java.variables.AbcVars;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static main.java.variables.ScpVars.ROWS;
import static main.java.variables.ScpVars.getColumnsCoveringRow;
import static main.java.variables.ScpVars.getCost;
import static main.java.variables.ScpVars.getRowsCoveredByColumn;

public class IterativeConstruction {

    private final AbcVars vr;
    private final CommonUtils cUtils;
    private final RepairUtils rUtils;

    public IterativeConstruction(AbcVars vr) {
        this.vr = vr;
        this.cUtils = new CommonUtils(vr);
        this.rUtils = new RepairUtils(vr);
    }

    public BitSet createSolution() {
        BitSet xj = new BitSet();
        int j = 0;
        List<List<Integer>> mapList = IntStream.range(0, ROWS)
                .boxed()
                .map(i -> {
                    List<Integer> cList = new ArrayList<>(getColumnsCoveringRow(i));
                    return new Tuple2<>(cList, cList.size());
                })
                .sorted(Comparator.comparing(Tuple2::getT2))
                .map(Tuple2::getT1)
                .collect(Collectors.toList());

        return processMap(xj, mapList);
/*
        while (!mapList.isEmpty()) {
            int rFunction = cUtils.randomNumber(6);
            List<Tuple2<Integer, Double>> cols = mapList.get(0)
                    .stream()
                    .map(c -> new Tuple2<>(c, calculateFunction(xj, c, rFunction) * 10.0 / 10.0))
                    .collect(Collectors.toList());

            double r1 = (vr.getRANDOM().nextDouble() * 100.0) / 100.0;

            if (r1 <= (1.0 / cols.size())) {
                double sumFunction = cols.stream().mapToDouble(Tuple2::getT2).sum();
                double r2 = (vr.getRANDOM().nextDouble() * 100.0) / 100.0;
                double cumulativeProbability = 0.0;

                for (Tuple2<Integer, Double> c : cols) {
                    cumulativeProbability += (c.getT2() / sumFunction);
                    if (r2 <= cumulativeProbability) {
                        j = c.getT1();
                        break;
                    }
                }
                xj.set(j);
            } else {
                j = cols.stream()
                        .min(Comparator.comparing(Tuple2::getT2))
                        .map(Tuple2::getT1)
                        .get();
                xj.set(j);
            }
            updateMap(mapList, j);

            if (cUtils.uncoveredRowsStream(xj).isEmpty()) {
                break;
            }
        }

        return xj;
 */
    }

    private double calculateFunction(BitSet xj, int j, int rFunction) {
        List<Integer> uncoveredRows = cUtils.uncoveredRowsStream(xj);
        List<Integer> rowsCoveredByColumn = getRowsCoveredByColumn(j);
        List<Integer> uncoveredRowsCovered = rUtils.getUncoveredRowsCoveredByColumn(uncoveredRows, rowsCoveredByColumn);
        int Pj = uncoveredRowsCovered.size();
        double result = 0;
        switch (rFunction) {
            case 0:
                result = getCost(j) * 1.0 / Pj;
                break;
            case 1:
                result = getCost(j) * 1.0 / Math.log(1.0 + Pj);
                break;
            case 2:
                result = getCost(j) * 1.0 / Math.sqrt(Pj);
                break;
            case 3:
                result = getCost(j) * 1.0 / Math.pow(Pj, 2);
                break;
            case 4:
                result = Math.sqrt(getCost(j)) / Pj;
                break;
            case 5:
                result = getCost(j) * 1.0 / (Pj * Math.log(Pj));
                break;
            default:
                result = 0.0;
                break;
        }
        return result;
    }

    private void updateMap(List<List<Integer>> mapList, int j) {
        mapList.remove(0);
        List<List<Integer>> test;
        // test = mapList.stream().filter(l -> l.contains(j)).collect(Collectors.toList());
        // System.out.println();
        mapList.forEach(l -> l.remove(Integer.valueOf(j)));
        // test = mapList.stream().filter(l -> l.contains(j)).collect(Collectors.toList());
        //System.out.println();
    }


    private BitSet processMap(BitSet xj, List<List<Integer>> mapList) {
        int rFunction = cUtils.randomNumber(6);
        int j = 0;
        List<Tuple2<Integer, Double>> cols = mapList.get(0)
                .stream()
                .map(c -> new Tuple2<>(c, calculateFunction(xj, c, rFunction) * 10.0 / 10.0))
                .collect(Collectors.toList());

        double r1 = (vr.getRANDOM().nextDouble() * 100.0) / 100.0;

        if (r1 <= (1.0 / cols.size())) {
            double sumFunction = cols.stream().mapToDouble(Tuple2::getT2).sum();
            double r2 = (vr.getRANDOM().nextDouble() * 100.0) / 100.0;
            double cumulativeProbability = 0.0;

            for (Tuple2<Integer, Double> c : cols) {
                cumulativeProbability += (c.getT2() / sumFunction);
                if (r2 <= cumulativeProbability) {
                    j = c.getT1();
                    break;
                }
            }
            xj.set(j);
        } else {
            j = cols.stream()
                    .min(Comparator.comparing(Tuple2::getT2))
                    .map(Tuple2::getT1)
                    .get();
            xj.set(j);
        }
        List<Integer> uncoveredRows = cUtils.uncoveredRowsStream(xj);
        if (uncoveredRows.isEmpty()) {
            return xj;
        }

        updateMap(mapList, j);
        return processMap(xj, mapList);
    }
}
