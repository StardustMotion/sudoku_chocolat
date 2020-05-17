// This code will generate a random, filled sudoku grid of size n²*n²

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;

import java.util.Arrays;


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
        String timeLimit = "3s";
        boolean restart = true;
        String difficulty;

        // Generate a random but valid, finished Sudoku grid of size dimension²*dimension², the process
        // is allowed to take up to timeLimit time
        MySudoku sudoku = new MySudoku(dimension, timeLimit); //maxSolutions, restart
        int[][] sudokuGrid = sudoku.getGrid();

        // We clear some cells
        for (int i = 0; i < dimension*dimension; i++) {
            for (int j = 0; j < dimension*dimension; j = j+2) {
                System.out.println("Cell " + i + j + " was cleared!");
                sudokuGrid[i][j] = 0;
            }
        }

        String timeLimitToSolve = "3s";
        // a "good" sudoku is supposed to only have 1 solution.
        // This variables displays up to maxSolutions solutions if they exist
        int maxSolutions = 10;
        // Solve the given sudokuGrid (a int[][] data). "blanks" in the grid are represented by the value 0
        // (this prints the solution)
        MySudoku sudokuToSolve = new MySudoku(dimension, timeLimitToSolve, maxSolutions, sudokuGrid);
    }


}

