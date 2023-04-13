package abcscp.config;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

@Getter
@Setter
public class Variables {
    //Variables
    private int ROWS;
    private int COLUMNS;
    private List<Integer> COSTS;
    private List<List<Integer>> COLUMNSCOVERINGROW;
    private List<List<Integer>> ROWSCOVEREDBYCOLUMN;
    private List<BitSet> FOODS;
    private BitSet GLOBAL_PARAMS;
    private List<Integer> FITNESS;
    private int[] TRIAL;
    private List<Double> PROB;
    private Integer GLOBAL_MIN;

    private List<Integer> GLOBAL_MINS;
    private Double MEAN;

    private Random RANDOM;

    public Variables() {
        long seed = System.currentTimeMillis();
        this.RANDOM = new Random(seed);
        this.GLOBAL_MINS = new ArrayList<>();
        this.MEAN = 0d;
    }

    public void addFoodSource(BitSet foodSource) {
        this.FOODS.add(foodSource);
    }

    public void addFitness(Integer fitness) {
        this.FITNESS.add(fitness);
    }

    public void setTrial(int index, int value) {
        this.TRIAL[index] = value;
    }

    public Integer getFitness(int i) {
        return this.FITNESS.get(i);
    }

    public BitSet getFoodSource(int i) {
        return (BitSet) this.FOODS.get(i).clone();
    }

    public double getProbability(int i) {
        return this.PROB.get(i);
    }

    public int getTrial(int i) {
        return this.TRIAL[i];
    }

    public void setFoodSource(int i, BitSet foodSource) {
        this.FOODS.set(i, (BitSet) foodSource.clone());
    }

    public void setFitness(int i, Integer value) {
        this.FITNESS.set(i, value);
    }

    public void incrementTrial(int i) {
        this.TRIAL[i]++;
    }

    public void addProbability(double value) {
        this.PROB.add(value);
    }

    public void addCosts(int value) {
        this.COSTS.add(value);
    }

    public void addColumnsCoveringRow(List<Integer> columns) {
        this.COLUMNSCOVERINGROW.add(columns);
    }

    public List<Integer> getColumnsCoveringRow(int i) {
        return this.COLUMNSCOVERINGROW.get(i);
    }

    public void addRowsCoveredByColumn(List<Integer> rows) {
        this.ROWSCOVEREDBYCOLUMN.add(rows);
    }

    public void addGlobalMin(Integer value) {
        this.GLOBAL_MINS.add(value);
    }

    public List<Integer> getRowsCoveredByColumn(int j) {
        return this.ROWSCOVEREDBYCOLUMN.get(j);
    }

    public boolean getGlobalParamsColumnValue(int j){
        return this.GLOBAL_PARAMS.get(j);
    }
}
