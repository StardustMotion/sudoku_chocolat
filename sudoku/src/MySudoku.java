import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

import java.awt.*;
import java.util.Random;

public class MySudoku {

    private Model model;
    // the sudoku size. Defines its height, width, and number of subblocks, to the power n
    private int n;
    private int nPow;
    private String niceASCIIPart;
    private StringBuilder niceASCII;
    private IntVar[][] grid;
    private int[][] tempGrid;
    private int gridNumber;
    private int[] sudokuValues;
    private Solution theSolution;
    private Solver solver;
    private Random randomizer;

    // Without arguments : generate a 3²x3² sudoku grid on a random difficulty
    public MySudoku() {
        new MySudoku(3, "5s", 5, true);
    }

    // dimension² is the size (height and width) of the sudoku
    // timelimit is the time written as a string i.e "5s" which is max time allowed to compute
    // solutionLimit is yes
    // restart = true will assemble another sudoku from the base node tree -
                // resulting in better sudoku values diversity
    public MySudoku(int dimension, String timeLimit, int gridAmount, boolean restart) {

        this.n = dimension;
        this.nPow = n * n;
        this.model = new Model("My sudoku of size " + n);

        niceASCIIPart = "==============";
        niceASCII = new StringBuilder();
        for (int i = 0; i < n; i++)
            niceASCII.append(niceASCIIPart);

        grid = new IntVar[nPow][nPow];
        tempGrid = new int[nPow][nPow];

        randomizer = new Random();

        // creates the values domain of each cell
        sudokuValues = new int[nPow];
        for (int i = 0; i < nPow; i++)
            sudokuValues[i] = i+1;



        // apply the sudoku rules
        applyConstraints();

        solver = model.getSolver();
        solver.limitTime(timeLimit);
        solver.limitSolution(gridAmount);
        if(restart)
            solver.setRestartOnSolutions();
    }

    // findSolution/solution
    // displayAllGrids prints all the grids found while computing if true
    // else it just prints the one which was picked
    public void generateGrid(boolean displayAllGrids) {
       int number = 0;
       int reservoir;
       while (solver.solve()) {
           number++;
           reservoir = randomizer.nextInt(number);
           if (reservoir == 0) {
               gridNumber = number;
               tempGrid = deepCopyGrid(grid);
           }
           if (displayAllGrids) {
               printGrid(number, deepCopyGrid(grid));
           }
       }
       if (number == 0) { // how even wat
           System.out.print("No sudoku grid could be found with these parameters.");
           return;
       }
        System.out.print("\n\n\n\nWe selected randomly...");

       printGrid(gridNumber, tempGrid);



       //if (solver.hasReachedLimit())

       //this.theSolution = solver.findSolution();

    }

    public void printGrid(int number, int[][] someGrid) {
        System.out.print("\n" + niceASCII + "\nSUDOKU GRID N° " + number + "\n" + niceASCII);
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

    // take the Choco IntVar[][] grid and turn it to an int[][] grid
    private int[][] deepCopyGrid(IntVar[][] someGrid) {
        int[][] newGhostGrider = new int[nPow][nPow];
        for (int i = 0; i < nPow; i++)
            for (int j = 0; j < nPow; j++)
                newGhostGrider[i][j] = someGrid[i][j].getValue();

        return newGhostGrider;
    }

    private void applyConstraints(){

        // each cell's value is between 1 and (n*n)
        for (int i = 0; i < nPow; i++) {
            for (int j = 0; j < nPow; j++) {
                grid[i][j] = model.intVar("c("+Integer.toString(i+1) + "," + Integer.toString(j+1) + ")", sudokuValues);
            }
        }


       for (int i = 0; i < nPow; i++) {
            // all numbers on a line are different
            model.allDifferent(grid[i]).post();
        }
        for (int i = 0; i < nPow; i++) {
            IntVar[] column = new IntVar[nPow];
            for (int j = 0; j < nPow; j++) {
                column[j] = grid[j][i];
            }
            // all numbers on a column are different
            model.allDifferent(column).post();
        }


        // all numbers in n*n blocks are different
        // NOTE : the iteration goes like this : block row 1 column 1,
        // r1c2, r1c3, ...r1cn, r2c1, r2c2 etc
        for (int block = 0; block < nPow; block++) {
            IntVar[] blockVals = new IntVar[nPow];

            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++) {
                    blockVals[(i*n)+j] = grid[i+(block/n)*n][j + ((block % n)*n)];
                }

            // all numbers on a column are different
            model.allDifferent(blockVals).post();
        }
    }

    public void stats() {
        solver.printStatistics();
    }


}
