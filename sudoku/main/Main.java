package main;// This code will generate a random, filled sudoku grid of size n²*n²
import java.util.Random;

// IntVar v2 = model.intVar("v2", new int[]{1, 3});
// // Create an array of 5 variables taking their value in [-1, 1]
//IntVar[] vs = model.intVarArray("vs", 5, -1, 1);
//// Create a matrix of 5x6 variables taking their value in [-1, 1]
//IntVar[][] vs = model.intVarMatrix("vs", 5, 6, -1, 1);


public class Main {

    // count how much times the "peakHole" function was called
    //static int truc;

    static int clues;
    public static void main(String[] args) {

        //truc = 0;
        int dimension = 3;
        int square = dimension*dimension;

        String timeLimit = "3s";
        //boolean restart = true;
        //int difficulty = 1; //1 to 4

        // Generate a random but valid, finished Sudoku grid of size dimension²*dimension², the process
        // is allowed to take up to timeLimit time
        MySudoku sudoku = new MySudoku(dimension, timeLimit); //maxSolutions, restart
        int bestClues = square*square;
        int[][] curGrid;
        int[][] bestGrid = new int[2][2]; // just so intellij leave me alone
        for (int abc = 0; abc < 20; abc++) {
            clues = square*square;
            curGrid = sudoku.deepCopyPrimitiveGrid(sudoku.grid);
            //System.out.printf("Some hole arrangement :");
            peckHoles(square, curGrid, sudoku, dimension);
            if (bestClues > clues) {
                bestGrid = sudoku.deepCopyPrimitiveGrid(curGrid);
                //copiedCodeFromMySudoku(square, dimension, curGrid);
                bestClues = clues;
            }

        }
        System.out.printf("Searched for a small amount of clues : " + bestClues);
        copiedCodeFromMySudoku(square, dimension, bestGrid);


        /*
        // We clear some cells
        for (int i = 0; i < dimension*dimension; i++) {
            for (int j = 0; j < dimension*dimension; j = j+2) {
                System.out.println("Cell " + i + j + " was cleared!");
                sudokuGrid[i][j] = 0;
            }
        }*/










        // DEFINE THE SUDOKU DIFFICULTY OF bestGrid
        //defineDifficulty(square, bestGrid, dimension, timeLimit);


    }


    private static void defineDifficulty(int square, int[][] grid, int dimension, String timeLimitToSolve) {


        MySudoku sudokuToMeasure;
        sudokuToMeasure = new MySudoku(dimension, timeLimitToSolve, 2, grid, true);
        int solutions = sudokuToMeasure.solveSudoku();

        if (solutions > 1)
            System.out.println("Wait this Sudoku has at least " + solutions + " solutions!!\n Make sure your sudoku only has" +
                    "one before defining its difficulty.");
        else {
            System.out.println("This sudoku has only one solution :" + solutions);
            copiedCodeFromMySudoku(square, dimension, sudokuToMeasure.getGrid());

        }

    }

    // choose a random cell in soemGrid to dig a hole into.
    // If the position already had one, then from that random position
    // go forward until you find one without a hole to dig from
    // Returns the [iIndex, jIndex, value] of the cell which was 0'd
    public static int[] peckAHole(int dimsquare, int[][] someGrid, boolean[][] checkCells) {
        //truc++;
        Random randomizer = new Random();
        int gridCells = dimsquare*dimsquare;
        int superValue = randomizer.nextInt(gridCells);
        int iIndex; int jIndex; int temp;
        for (int i = 0; i < gridCells; i++) {
            temp = (superValue % gridCells);
            iIndex = temp / dimsquare;
            jIndex = temp % dimsquare;
            if (!checkCells[iIndex][jIndex] && someGrid[iIndex][jIndex] != 0) {
                int tmpValue = someGrid[iIndex][jIndex];
                checkCells[iIndex][jIndex] = true;
                someGrid[iIndex][jIndex] = 0;
                clues--;
                //System.out.println("\n>>> Popped(" + (iIndex+1) + "," + (jIndex+1) + ") = " + tmpValue);
                return new int[]{iIndex, jIndex, tmpValue};
            }
            superValue++;
        }

        return null;
    }

    // give me a complete grid : I'll randomly peck holes until not possible anymore
    public static void peckHoles(int square, int[][] sudokuGrid, MySudoku originalSudoku, int dimension) {
        // an array telling is a i,j cell has been attempted to be removed. If the randomly chosen cell
        // to be blanked has its value on this array to TRUE then don't try to pop it.
        // tl;dr this is used to not compute twice a cell whose removal would lead to several sudokus solutions
        boolean[][] checkedCells = new boolean[square][square];
        for (int i = 0; i < square; i++)
            for (int j = 0; j < square; j++)
                checkedCells[i][j] = false;

        String timeLimitToSolve = "3s";
        // a "good" sudoku is supposed to only have 1 solution.
        // This variables displays up to maxSolutions solutions if they exist
        int maxSolutions = 3;



        MySudoku tmpSudoku;
        //int[][] tmpSudokuGrid = originalSudoku.deepCopyPrimitiveGrid();

        int[] rememberMe; // contains [i, j, prevValue] which was pecked at
        do {
            rememberMe = peckAHole(square, sudokuGrid, checkedCells);

            // solve it !
            tmpSudoku = new MySudoku(dimension, timeLimitToSolve, maxSolutions, sudokuGrid, false);
            // this has more than one solution?
            if (tmpSudoku.solveSudoku() > 1) {
                //System.out.println("GRRRRRRRRRRRRR");
                clues++;
                // we put the last value we popped into the grid. This cell won't be treated again
                sudokuGrid[rememberMe[0]][rememberMe[1]] = rememberMe[2];
            }


        } while (rememberMe != null);
        if (rememberMe == null) {
            //System.out.println("\n\nWent through the entire grid to produce for you this beauty.\n" + (clues-1) + " clues."  + "\n");
        }

        //copiedCodeFromMySudoku(square, dimension, sudokuGrid);

        // Solve the given sudokuGrid (a int[][] data). "blanks" in the grid are represented by the value 0
        // (this prints the solution)
        //main.MySudoku sudokuToSolve = new main.MySudoku(dimension, timeLimitToSolve, maxSolutions, sudokuGrid);

    }

    // print sudoku stuff
    public static void copiedCodeFromMySudoku(int square, int dimension, int[][]sudokuGrid) {
        System.out.print("\n");
        for (int i = 0; i < square; i++) {
            if ((i) % dimension == 0)
                System.out.println();
            for (int j = 0; j < square; j++) {
                if ((j) % dimension == 0)
                    System.out.print("    ");
                System.out.print(" " + sudokuGrid[i][j] + " ");
                if ((j + 1) % square == 0)
                    System.out.print("\n");
            }
        }
    }

}




