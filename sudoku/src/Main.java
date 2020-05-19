// This code will generate a random, filled sudoku grid of size n²*n²

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;

import java.util.Arrays;
import java.util.Random;


// IntVar v2 = model.intVar("v2", new int[]{1, 3});
// // Create an array of 5 variables taking their value in [-1, 1]
//IntVar[] vs = model.intVarArray("vs", 5, -1, 1);
//// Create a matrix of 5x6 variables taking their value in [-1, 1]
//IntVar[][] vs = model.intVarMatrix("vs", 5, 6, -1, 1);

/*
Modelling: Bounded or Enumerated?

        The choice of domain types may have strong impact on performance. Not only the memory consumption
        should be considered but also the used constraints. Indeed, some constraints only update bounds of
        integer variables, using them with bounded domains is enough. Others make holes in variables’ domain,
        using them with enumerated domains takes advantage of the power of their filtering algorithm. Most of
        the time, variables are associated with propagators of various power. The choice of domain representation
        should then be done on a case by case basis.
*/

public class Main {


    public static void main(String[] args) {

        int dimension = 3;
        int square = dimension*dimension;
        String timeLimit = "1s";
        boolean restart = true;
        int difficulty = 1; //1 to 4

        // Generate a random but valid, finished Sudoku grid of size dimension²*dimension², the process
        // is allowed to take up to timeLimit time
        MySudoku sudoku = new MySudoku(dimension, timeLimit); //maxSolutions, restart
        int[][] sudokuGrid = sudoku.getGrid();

        /*
        // We clear some cells
        for (int i = 0; i < dimension*dimension; i++) {
            for (int j = 0; j < dimension*dimension; j = j+2) {
                System.out.println("Cell " + i + j + " was cleared!");
                sudokuGrid[i][j] = 0;
            }
        }*/

        String timeLimitToSolve = "3s";
        // a "good" sudoku is supposed to only have 1 solution.
        // This variables displays up to maxSolutions solutions if they exist
        int maxSolutions = 3;

        int tame = 0;
        do {
            peckAHole(square, sudokuGrid);
            tame++;

            copiedCodeFromMySudoku(square, dimension, sudokuGrid);

        } while (tame <10);


        // Solve the given sudokuGrid (a int[][] data). "blanks" in the grid are represented by the value 0
        // (this prints the solution)
        //MySudoku sudokuToSolve = new MySudoku(dimension, timeLimitToSolve, maxSolutions, sudokuGrid);
    }

    // choose a random cell in soemGrid to dig a hole into.
    // If the position already had one, then from that random position
    // go forward until you find one without a hole to dig from
    public static void peckAHole(int dimsquare, int[][] someGrid) {
        Random randomizer = new Random();
        int gridCells = dimsquare*dimsquare;
        int superValue = randomizer.nextInt(gridCells);
        int iIndex; int jIndex; int temp;
        for (int i = 0; i < gridCells; i++) {
            temp = (superValue % gridCells);
            iIndex = temp / dimsquare;
            jIndex = temp % dimsquare;
            if (someGrid[iIndex][jIndex] != 0) {
                someGrid[iIndex][jIndex] = 0;
                System.out.println("\n>>> Popped(" + (iIndex+1) + "," + (jIndex+1) + ")");
                //return someGrid; // object modification is implied here in Java
                break;
            }
            superValue++;
        }


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

