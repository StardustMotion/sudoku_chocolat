package truc;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

import truc.MySudoku;

import java.util.Random;

public class MySudoku {

    private Model model;
    // the sudoku size. Defines its height, width, and number of subblocks, to the power n
    private int n;
    private int nPow;
    private String niceASCIIPart = "==============";
    private StringBuilder niceASCII;
    public int[][] grid;
    public IntVar[][] tempGrid;

    private int[][] preGrid;
    private int[] sudokuValues;
    private Solver sudokuSolver;
    private Random randomizer;

    /* ##################################################
                           SOLVER CORE
     ################################################## */

    // /*                       CONSTRUCTOR A            */
    // This constructor generates a random, valid Sudoku grid
    public MySudoku(int dimension, String timeLimit) { // maxSolutions, restart
        sudokuSetup(dimension, "Generated Sudoku of dimension " + n);

        // All the sudoku values are between 1 and dimension²
        for (int i = 0; i < nPow; i++) {
            for (int j = 0; j < nPow; j++) {
                tempGrid[i][j] = model.intVar("c("+Integer.toString(i+1) + "," + Integer.toString(j+1) + ")", sudokuValues);
            }
        }

        // apply the sudoku rules (row/column/block constraints)
        applyConstraints();
        this.sudokuSolver = model.getSolver();
        sudokuSolver.limitTime(timeLimit);
        //sudokuSolver.setSearch(org.chocosolver.solver.search.strategy.selectors.variables.ActivityBased(5));
        //sudokuSolver.setSearch(
        //        Search.intVarSearch(new AntiFirstFail(model), new IntDomainMax()));

        sudokuSolver.setRestartOnSolutions();
        //sudokuSolver.limitSolution(maxSolutions);
        generateSudokuGrid();
        stats();
    }



    // /*                       CONSTRUCTOR B            */
    // This constructor attempts to solve the sudoku represented by holeSudoku. The value 0 for a cell
    // means blank cell.
    // and shows at best maxSolutions solutions if there are more than 1, or none if.. there's none
    // NB : a "good" sudoku is supposed to only have ONE solution
    public MySudoku(int dimension, String timeLimitToSolve, int maxSolutions, int[][] holeSudoku, boolean humanSearch) {
        sudokuSetup(dimension, "Solving the input Sudoku");

        int cellVal;
        // All the sudoku values are between 1 and dimension², except for the one already assigned obviously
        for (int i = 0; i < nPow; i++) {
            for (int j = 0; j < nPow; j++) {
                cellVal = holeSudoku[i][j];
                // blank
                if (cellVal == 0)
                    tempGrid[i][j] = model.intVar("c("+Integer.toString(i+1) + "," + Integer.toString(j+1) + ")",
                            sudokuValues);
                    // pre-defined
                else
                    tempGrid[i][j] = model.intVar("c("+Integer.toString(i+1) + "," + Integer.toString(j+1) + ")",
                            cellVal);
            }
        }

        // apply the sudoku rules (row/column/block constraints)
        applyConstraints();
        this.sudokuSolver = model.getSolver();
        sudokuSolver.limitTime(timeLimitToSolve);
        sudokuSolver.limitSolution(maxSolutions);
        /*this.sudokuSolver.plugMonitor(new IMonitorContradiction() {
            @Override
            public void onContradiction(ContradictionException cex) {
                System.out.print("h");
            }
        });*/
        //solveSudoku();
    }


    /*         CONSTRUCTOR C (in building)
    // Generates a sudoku based on the filled initialGrid
    public truc.MySudoku(int[][] initialGrid, String timeLimitToSolve, int maxSolutions, int difficulty) {
        truc.MySudoku
        sudokuSetup(dimension, "Solving the input Sudoku");

        int cellVal;
        // All the sudoku values are between 1 and dimension², except for the one already assigned obviously
        for (int i = 0; i < nPow; i++) {
            for (int j = 0; j < nPow; j++) {
                cellVal = holeSudoku[i][j];
                // blank
                if (cellVal == 0)
                    tempGrid[i][j] = model.intVar("c("+Integer.toString(i+1) + "," + Integer.toString(j+1) + ")",
                            sudokuValues);
                    // pre-defined
                else
                    tempGrid[i][j] = model.intVar("c("+Integer.toString(i+1) + "," + Integer.toString(j+1) + ")",
                            cellVal);
            }
        }

        // apply the sudoku rules (row/column/block constraints)
        applyConstraints();
        this.sudokuSolver = model.getSolver();
        sudokuSolver.limitTime(timeLimitToSolve);
        sudokuSolver.limitSolution(maxSolutions);
        solveSudoku();
    }*/









    // Common stuff between the sudoku solver and the sudoku grid generator
    private void sudokuSetup(int dimension, String modelName) {
        this.n = dimension;
        this.randomizer = new Random();
        this.nPow = n * n;
        this.model = new Model(modelName);
        this.tempGrid = new IntVar[nPow][nPow];

        // builds some ASCII shenanigans
        this.niceASCII = new StringBuilder();
        for (int i = 0; i < n; i++)
            niceASCII.append(niceASCIIPart);

        // creates the values domain of each cell
        this.sudokuValues = new int[nPow];
        for (int i = 0; i < nPow; i++)
            sudokuValues[i] = i+1;

    }



    // deep copies a int[][] object
    public int[][] deepCopyPrimitiveGrid(int[][] oldGrid) {
        int[][] newGhostGrider = new int[nPow][nPow];
        for (int i = 0; i < nPow; i++)
            for (int j = 0; j < nPow; j++)
                newGhostGrider[i][j] = oldGrid[i][j];
        return newGhostGrider;
    }


    private void generateSudokuGrid() {
        int n = 0;
        int reservoir = 0;
        int picked = 0;
        while (sudokuSolver.solve()) {
            n++;
            reservoir = randomizer.nextInt(n);
            if (reservoir == 0) {
                picked = n;
                grid = deepCopyGrid(tempGrid);
            }
            //printGrid(n, deepCopyGrid(tempGrid), "SUDOKU GRID N° ");
            //if (displayAllGrids) {
            //    printGrid(number, deepCopyGrid(grid));
            //}

        }

        if (n == 0) { // how even wat
            // this shouldn't happen... UNLESS you define a naive model and a low time limit.
            System.out.print("No sudoku grid could be found with these parameters.\n\n");
        }
        else {
            System.out.print("\n\n\n\nWe selected randomly...");
            printGrid(picked, grid, "SUDOKU GRID N° ");
        }

    }

    // Find the solution(s) and print them for the sudoku given as input (Constructor B)
    // return the amount of solutions found
    public int solveSudoku() {
        int n = 0;
        while (sudokuSolver.solve()) {
            n++;
            grid = deepCopyGrid(tempGrid);
            //printGrid(n, grid, "SOLUTION N° ");
        }
        //stats();
        return n;
    }




    /*            SUDOKU RULES                   */
    // No matter the sudoku dimension or what, two same numbers must not be in the same row, column or block
    private void applyConstraints(){

        // all numbers on a line are different
        for (int i = 0; i < nPow; i++) {
            model.allDifferent(tempGrid[i]).post();
        }
        // all numbers on a column are different
        for (int i = 0; i < nPow; i++) {
            IntVar[] column = new IntVar[nPow];
            for (int j = 0; j < nPow; j++) {
                column[j] = tempGrid[j][i];
            }
            model.allDifferent(column).post();
        }
        // all numbers in n*n blocks are different
        // NOTE : for each block, the iteration goes like this : row 1 column 1,
        // r1c2, r1c3, ...r1cn, r2c1, r2c2 etc
        for (int block = 0; block < nPow; block++) {
            IntVar[] blockVals = new IntVar[nPow];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++) {
                    blockVals[(i*n)+j] = tempGrid[i+(block/n)*n][j + ((block % n)*n)];
                }
            // all numbers on a column are different
            model.allDifferent(blockVals).post();
        }
    }




















    /* ##################################################
                           UTILITY
     ################################################## */

    public void printGrid(int number, int[][] someGrid, String message) {
        System.out.print("\n" + niceASCII + "\n" + message + number + "\n" + niceASCII);
        for (int i = 0; i < nPow; i++) {
            if ((i) % n == 0)
                System.out.println();
            for (int j = 0; j < nPow; j++) {
                if ((j) % n == 0)
                    System.out.print("    ");
                System.out.print(" " + someGrid[i][j] + " ");
                if ((j + 1) % nPow == 0)
                    System.out.print("\n");
            }
        }
        System.out.print(niceASCII + "\n\n\n");
    }

    // take the Choco IntVar[][] object grid and turn it to an int[][] grid
    public int[][] deepCopyGrid(IntVar[][] someGrid) {
        int[][] newGhostGrider = new int[nPow][nPow];
        for (int i = 0; i < nPow; i++)
            for (int j = 0; j < nPow; j++)
                newGhostGrider[i][j] = someGrid[i][j].getValue();
        return newGhostGrider;
    }

    public int[][] getGrid() { return deepCopyGrid(tempGrid); }

    public void stats() { sudokuSolver.printStatistics(); }












        /*// each cell's value is between 1 and (n*n)
        if (preGrid == null)
            for (int i = 0; i < nPow; i++) {
                for (int j = 0; j < nPow; j++) {
                    grid[i][j] = model.intVar("c("+Integer.toString(i+1) + "," + Integer.toString(j+1) + ")", sudokuValues);
                }
            }
        else { // CODE DUPLICATION      HUUUUUUH UHUHUH
            // case if some cells have been chosen to be pre-assigned
            int preGridVal;
            for (int i = 0; i < nPow; i++) {
                for (int j = 0; j < nPow; j++) {
                    preGridVal = preGrid[i][j];
                    if (preGridVal == 0) {
                        grid[i][j] = model.intVar("c("+Integer.toString(i+1) + "," + Integer.toString(j+1) + ")", sudokuValues);

                        System.out.println("CELL " + i + j + " is unknown so " + preGridVal);
                    }
                       else {
                        grid[i][j] = model.intVar("c("+Integer.toString(i+1) + "," + Integer.toString(j+1) + ")", preGridVal);

                        System.out.println("CELL " + i + j + " was pre-assigned to " + preGridVal);
                    }

                }
            }
        }*/



}
