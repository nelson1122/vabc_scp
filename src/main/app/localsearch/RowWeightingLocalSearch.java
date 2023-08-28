package main.app.localsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RowWeightingLocalSearch {

    // Define the problem matrix and its dimensions
    private int[][] problemMatrix;
    private int numRows;
    private int numCols;

    // Define the solution vector
    private List<Integer> solution;

    // Define the row weights
    private List<Integer> rowWeights;

    public RowWeightingLocalSearch(int[][] problemMatrix) {
        this.problemMatrix = problemMatrix;
        this.numRows = problemMatrix.length;
        this.numCols = problemMatrix[0].length;
        this.solution = new ArrayList<>();
        this.rowWeights = new ArrayList<>();
    }

    // Generate a random initial solution
    private void generateInitialSolution() {
        Random random = new Random();
        for (int i = 0; i < numRows; i++) {
            solution.add(random.nextInt(2));  // randomly select sets
            rowWeights.add(0);  // initialize row weights
        }
    }

    // Calculate the weights of the rows
    private void calculateRowWeights() {
        for (int i = 0; i < numRows; i++) {
            int weight = 0;
            for (int j = 0; j < numCols; j++) {
                if (problemMatrix[i][j] == 1 && solution.get(i) == 0) {
                    weight++;
                }
            }
            rowWeights.set(i, weight);
        }
    }

    // Perform a local move on the solution
    private void performLocalMove() {
        int maxWeightIndex = getMaxWeightIndex();
        int randomElementIndex = getRandomElementIndex(maxWeightIndex);
        int newSetSelection = 1 - solution.get(randomElementIndex);  // flip the selection

        solution.set(randomElementIndex, newSetSelection);
    }

    // Get the index of the row with the maximum weight
    private int getMaxWeightIndex() {
        int maxWeight = -1;
        List<Integer> maxWeightIndices = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            if (rowWeights.get(i) > maxWeight) {
                maxWeight = rowWeights.get(i);
                maxWeightIndices.clear();
                maxWeightIndices.add(i);
            } else if (rowWeights.get(i) == maxWeight) {
                maxWeightIndices.add(i);
            }
        }

        Random random = new Random();
        return maxWeightIndices.get(random.nextInt(maxWeightIndices.size()));
    }

    // Get a random element index from a row with the given index
    private int getRandomElementIndex(int rowIndex) {
        List<Integer> elementIndices = new ArrayList<>();
        for (int j = 0; j < numCols; j++) {
            if (problemMatrix[rowIndex][j] == 1) {
                elementIndices.add(j);
            }
        }

        Random random = new Random();
        return elementIndices.get(random.nextInt(elementIndices.size()));
    }

    // Check if all elements are covered by the solution
    private boolean isSolutionValid() {
        int[] coveredElements = new int[numCols];
        for (int i = 0; i < numRows; i++) {
            if (solution.get(i) == 1) {
                for (int j = 0; j < numCols; j++) {
                    if (problemMatrix[i][j] == 1) {
                        coveredElements[j] = 1;
                    }
                }
            }
        }

        for (int element : coveredElements) {
            if (element == 0) {
                return false;
            }
        }

        return true;
    }

    // Run the RowWeightingLocalSearch algorithm
    public void run() {
        generateInitialSolution();
        calculateRowWeights();

        while (!isSolutionValid()) {
            performLocalMove();
            calculateRowWeights();
        }

        // Print the selected sets
        System.out.println("Selected sets:");
        for (int i = 0; i < numRows; i++) {
            if (solution.get(i) == 1) {
                System.out.print(i + " ");
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int[][] problemMatrix = {
                {1, 0, 1, 0, 0},
                {1, 1, 0, 1, 0},
                {0, 1, 0, 0, 1},
                {0, 0, 1, 1, 0},
                {1, 1, 1, 0, 1}
        };

        RowWeightingLocalSearch setCovering = new RowWeightingLocalSearch(problemMatrix);
        setCovering.run();
    }
}
