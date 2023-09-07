package main.java.localsearch;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ABCSCPLocalSearch2 {
    private BitSet[] solution; // The current bitset vector solution
    private int[] columnCosts; // Cost of each column
    private int[] rowCounts; // Number of columns covering each row

    public ABCSCPLocalSearch2(BitSet[] solution, int[] columnCosts) {
        this.solution = solution;
        this.columnCosts = columnCosts;
        this.rowCounts = computeRowCounts();
    }

    // Step 1: Compute the number of columns covering each row
    private int[] computeRowCounts() {
        int[] rowCounts = new int[solution.length];
        for (int i = 0; i < solution.length; i++) {
            rowCounts[i] = solution[i].cardinality();
        }
        return rowCounts;
    }

    // Step 2: Find the set of rows solely covered by column j
    private Set<Integer> findSolelyCoveredRows(int column) {
        Set<Integer> solelyCoveredRows = new HashSet<>();
        for (int i = 0; i < solution.length; i++) {
            if (solution[i].get(column) && rowCounts[i] == 1) {
                solelyCoveredRows.add(i);
            }
        }
        return solelyCoveredRows;
    }

    // Step 3: Remove column j from the solution
    private void removeColumn(int column) {
        for (int i = 0; i < solution.length; i++) {
            solution[i].clear(column);
            rowCounts[i] = solution[i].cardinality();
        }
    }

    // Step 4: Check and replace column j based on conditions for cardinality 1
    private void handleCardinality1(int column) {
        Set<Integer> solelyCoveredRows = findSolelyCoveredRows(column);
        if (solelyCoveredRows.size() == 1) {
            int row = solelyCoveredRows.iterator().next();
            if (columnCosts[column] < columnCosts[row]) {
                replaceColumn(column, row);
            }
        }
    }

    // Replace column j with column i
    private void replaceColumn(int j, int i) {
        solution[j].clear();
        solution[j].or(solution[i]);
        rowCounts[j] = solution[j].cardinality();
    }

    // Step 5: Check and replace column j based on conditions for cardinality 2
    private void handleCardinality2(int column) {
        Set<Integer> solelyCoveredRows = findSolelyCoveredRows(column);
        if (solelyCoveredRows.size() == 2) {
            Iterator<Integer> iterator = solelyCoveredRows.iterator();
            int i1 = iterator.next();
            int i2 = iterator.next();
            int costI1 = columnCosts[i1];
            int costI2 = columnCosts[i2];

            // Find the least cost columns covering i1 and i2
            int leastCostI1 = findLeastCostColumn(i1);
            int leastCostI2 = findLeastCostColumn(i2);

            // Replace column j based on conditions
            if (leastCostI1 != leastCostI2) {
                int totalCost = costI1 + costI2;
                if (totalCost <= columnCosts[column]) {
                    replaceColumn(column, leastCostI1);
                    replaceColumn(leastCostI2, leastCostI1);
                } else {
                    replaceColumn(column, leastCostI1);
                }
            } else {
                replaceColumn(column, leastCostI1);
            }
        }
    }

    // Find the least cost column covering row i
    private int findLeastCostColumn(int row) {
        int minCost = Integer.MAX_VALUE;
        int minCostColumn = -1;
        for (int j = 0; j < solution.length; j++) {
            if (solution[row].get(j) && columnCosts[j] < minCost) {
                minCost = columnCosts[j];
                minCostColumn = j;
            }
        }
        return minCostColumn;
    }

    // Perform the local search step of the algorithm
    private void localSearch() {
        for (int j = 0; j < solution[0].length(); j++) {
            Set<Integer> solelyCoveredRows = findSolelyCoveredRows(j);
            int cardinality = solelyCoveredRows.size();

            if (cardinality == 0) {
                removeColumn(j);
            } else if (cardinality == 1) {
                handleCardinality1(j);
            } else if (cardinality == 2) {
                handleCardinality2(j);
            }
        }
    }

    // Run the entire algorithm
    public void runAlgorithm() {
        localSearch();
    }

    // Helper method to print the current solution
    public void printSolution() {
        for (int i = 0; i < solution.length; i++) {
            for (int j = 0; j < solution[i].length(); j++) {
                System.out.print(solution[i].get(j) ? "1 " : "0 ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        // Example usage:
        BitSet[] solution = {
                BitSet.valueOf(new long[]{5}), // 101
                BitSet.valueOf(new long[]{6}), // 110
                BitSet.valueOf(new long[]{3})  // 011
        };
        int[] columnCosts = {3, 5, 4};

        ABCSCPLocalSearch2 solver = new ABCSCPLocalSearch2(solution, columnCosts);
        System.out.println("Initial ABCSCP:");
        solver.printSolution();

        solver.runAlgorithm();

        System.out.println("\nABCSCP after local search:");
        solver.printSolution();
    }
}

