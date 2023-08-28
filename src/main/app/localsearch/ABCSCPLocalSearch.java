package main.app.localsearch;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import static main.app.variables.ScpVars.COLUMNS;
import static main.app.variables.ScpVars.ROWS;
import static main.app.variables.ScpVars.getColumnsCoveringRow;
import static main.app.variables.ScpVars.getCost;
import static main.app.variables.ScpVars.getRowsCoveredByColumn;

public class ABCSCPLocalSearch {

    public static BitSet applyLocalSearch(BitSet solution) {
        int[] u = new int[ROWS];


        // 1- Computing for each row i the number of columns ui in the current solution covering it.
        for (int i = 0; i < ROWS; i++) {
            List<Integer> ai = getColumnsCoveringRow(i);
            for (int j = 0; j < COLUMNS; j++) {
                if (solution.get(j) && ai.contains(j)) {
                    u[i]++;
                }
            }
        }
/*
        List<List<Integer>> Pj = new ArrayList<>();
        for (int j = 0; j < COLUMNS; j++) {
            List<Integer> rowsCoveredByOneColumn = new ArrayList<>();
            if (solution.get(j)) {
                List<Integer> bj = getRowsCoveredByColumn(j);
                for (int i = 0; i < bj.size(); i++) {
                    int rowIndex = bj.get(i);
                    if (u[rowIndex] == 1) {
                        rowsCoveredByOneColumn.add(rowIndex);
                    }
                }
            }
            Pj.add(rowsCoveredByOneColumn);
        }
*/

        solution.stream()
                .boxed()
                .forEach(j -> {
                    List<Integer> rowsCoveredByOneColumn = new ArrayList<>();
                    List<Integer> bj = getRowsCoveredByColumn(j);
                    for (int rowIndex : bj) {
                        if (u[rowIndex] == 1) {
                            rowsCoveredByOneColumn.add(rowIndex);
                        }
                    }
                    int Pj = rowsCoveredByOneColumn.size();
                    if (Pj == 0) {
                        solution.clear(j);
                        updateUi(u, j, false);
                    }
                    if (Pj == 1) {
                        int row = rowsCoveredByOneColumn.get(0);
                        int minCostColumn = findMinCostColumn(row); // Implement this function
                        if (minCostColumn != j) {
                            solution.clear(j);
                            solution.set(minCostColumn);
                            updateUi(u, j, false);
                            updateUi(u, minCostColumn, true);
                        }
                    }
                    if (Pj == 2) {
                        int row1 = rowsCoveredByOneColumn.get(0);
                        int row2 = rowsCoveredByOneColumn.get(1);

                        int minCostColumn1 = findMinCostColumn(row1); // Implement this function
                        int minCostColumn2 = findMinCostColumn(row2); // Implement this function

                        int sumCosts = calculateColumnCost(minCostColumn1) + calculateColumnCost(minCostColumn2);

                        if (minCostColumn1 != minCostColumn2 && sumCosts <= calculateColumnCost(j)) {
                            solution.clear(j);
                            solution.set(minCostColumn1);
                            solution.set(minCostColumn2);
                            updateUi(u, j, false);
                            updateUi(u, minCostColumn1, true);
                            updateUi(u, minCostColumn2, true);
                        } else if (minCostColumn1 == minCostColumn2 && minCostColumn1 != j) {
                            solution.clear(j);
                            solution.set(minCostColumn1);
                            updateUi(u, j, false);
                            updateUi(u, minCostColumn1, true);
                        }
                    }
                    if (Pj == 3) {
                        int row1 = rowsCoveredByOneColumn.get(0);
                        int row2 = rowsCoveredByOneColumn.get(1);
                        int row3 = rowsCoveredByOneColumn.get(2);

                        int minCostColumn1 = findMinCostColumn(row1); // Implement this function
                        int minCostColumn2 = findMinCostColumn(row2); // Implement this function
                        int minCostColumn3 = findMinCostColumn(row3); // Implement this function

                        int sumCosts = calculateColumnCost(minCostColumn1) + calculateColumnCost(minCostColumn2) + calculateColumnCost(minCostColumn3);

                        if (minCostColumn1 != minCostColumn2 && minCostColumn1 != minCostColumn3 && minCostColumn2 != minCostColumn3 && sumCosts <= calculateColumnCost(j)) {
                            solution.clear(j);
                            solution.set(minCostColumn1);
                            solution.set(minCostColumn2);
                            solution.set(minCostColumn3);
                            updateUi(u, j, false);
                            updateUi(u, minCostColumn1, true);
                            updateUi(u, minCostColumn2, true);
                            updateUi(u, minCostColumn3, true);
                        } else if (minCostColumn1 == minCostColumn2 && minCostColumn2 == minCostColumn3 && minCostColumn1 != j) {
                            solution.clear(j);
                            solution.set(minCostColumn1);
                            updateUi(u, j, false);
                            updateUi(u, minCostColumn1, true);
                        } else if (minCostColumn1 == minCostColumn2 && minCostColumn1 != minCostColumn3 && sumCosts <= calculateColumnCost(j)) {
                            solution.clear(j);
                            solution.set(minCostColumn1);
                            solution.set(minCostColumn3);
                            updateUi(u, j, false);
                            updateUi(u, minCostColumn1, true);
                            updateUi(u, minCostColumn3, true);
                        } else if (minCostColumn1 == minCostColumn3 && minCostColumn1 != minCostColumn2 && sumCosts <= calculateColumnCost(j)) {
                            solution.clear(j);
                            solution.set(minCostColumn1);
                            solution.set(minCostColumn2);
                            updateUi(u, j, false);
                            updateUi(u, minCostColumn1, true);
                            updateUi(u, minCostColumn2, true);
                        } else if (minCostColumn2 == minCostColumn3 && minCostColumn2 != minCostColumn1 && sumCosts <= calculateColumnCost(j)) {
                            solution.clear(j);
                            solution.set(minCostColumn2);
                            solution.set(minCostColumn1);
                            updateUi(u, j, false);
                            updateUi(u, minCostColumn2, true);
                            updateUi(u, minCostColumn1, true);
                        }
                    }
                });

        return solution;
    }


    private static int findMinCostColumn(int i) {
        // Implement your logic here
        List<Integer> ai = getColumnsCoveringRow(i);
        return ai.get(0); // Replace with actual column index
    }

    private static int calculateColumnCost(int j) {
        // Implement your logic here
        return getCost(j); // Replace with actual cost value
    }

    private static void updateUi(int[] ui, int j, boolean add) {
        for (int row : getRowsCoveredByColumn(j)) {
            if (add) {
                ui[row]++;
            } else {
                ui[row]--;
            }
        }
    }
}
