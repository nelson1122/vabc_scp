package abcscp.utils;

import abcscp.config.Variables;
import lombok.Builder;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static abcscp.config.Parameters.FOOD_NUMBER;

@Builder
public class CommonUtils {
    private Variables vr;

    public CommonUtils(Variables v) {
        this.vr = v;
    }

    public int randomNumber(int high) {
        int low = 0;
        return vr.getRANDOM().nextInt((high - low)) + low;
    }

    public int calculateFitnessOneStream(BitSet xj) {
        return xj.stream()
                .boxed()
                .map(x -> vr.getCOSTS().get(x))
                .reduce(Integer::sum).get();
    }

    public int calculateFitnessOne(BitSet xj) {
        int fitness = 0;
        for (int j = 0; j < vr.getCOLUMNS(); j++) {
            if (xj.get(j)) {
                fitness += vr.getCOSTS().get(j);
            }
        }
        return fitness;
    }

    public int calculateFitnessTwoStream(BitSet xj) {
        return xj.stream()
                .boxed()
                .map(vr.getROWSCOVEREDBYCOLUMN()::get)
                .map(List::size).reduce(Integer::sum)
                .get();
    }

    public int calculateFitnessTwo(BitSet xj) {
        int count = 0;
        for (int j = 0; j < vr.getCOLUMNS(); j++)
            if (xj.get(j)) {
                count += vr.getROWSCOVEREDBYCOLUMN().get(j).size();
            }
        return count;
    }

    public int randomFoodSource(int i) {
        int randomFood = randomNumber(FOOD_NUMBER);
        if (i != randomFood) {
            return randomFood;
        }
        return randomFoodSource(i);
    }

    public List<Integer> distinctColumnsStream(BitSet i, BitSet j) {
        List<Integer> s1 = i.stream()
                .boxed()
                .collect(Collectors.toList());

        return j.stream()
                .boxed()
                .filter(cIndex -> !s1.contains(cIndex))
                .collect(Collectors.toList());
    }

    public List<Integer> distinctColumns(BitSet i, BitSet j) {
        List<Integer> distinctColumns = new ArrayList<>();
        for (int x = 0; x < vr.getCOLUMNS(); x++) {
            if (!i.get(x) && j.get(x)) {
                distinctColumns.add(x);
            }
        }
        return distinctColumns;
    }

    public List<Integer> uncoveredRows(BitSet solution) {
        int[] w = new int[vr.getROWS()];
        List<Integer> uncoveredRows = new ArrayList<>();
        for (int i = 0; i < vr.getROWS(); i++) {
            List<Integer> ai = vr.getCOLUMNSCOVERINGROW().get(i);
            for (int j = 0; j < vr.getCOLUMNS(); j++) {
                if (solution.get(j) && ai.contains(j)) {
                    w[i]++;
                }
            }
        }
        for (int i = 0; i < vr.getROWS(); i++) {
            if (w[i] == 0) {
                uncoveredRows.add(i);
            }
        }
        return uncoveredRows;
    }

    public List<Integer> uncoveredRowsStream(BitSet solution) {
        List<Integer> coveredRows =
                solution.stream()
                        .boxed()
                        .map(vr.getROWSCOVEREDBYCOLUMN()::get)
                        .flatMap(Collection::stream)
                        .distinct()
                        .collect(Collectors.toList());
        return IntStream
                .range(0, vr.getROWS())
                .boxed()
                .filter(x -> !coveredRows.contains(x))
                .collect(Collectors.toList());
    }

    public List<Integer> getColumns(BitSet solution) {
        return solution.stream()
                .boxed()
                .collect(Collectors.toList());
    }

    public List<Integer> getColumnsRandomFoodSource(BitSet solution, int i) {
        int randomFoodS = randomFoodSource(i);
        List<Integer> distinctColumns = distinctColumnsStream(solution, vr.getFOODS().get(randomFoodS));
        if (!distinctColumns.isEmpty()) {
            return distinctColumns;
        }
        return getColumnsRandomFoodSource(solution, i);
    }
}
