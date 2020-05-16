import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;

public class MySudoku {

    private Model model;
    // the sudoku size. Defines its height, width, and number of subblocks, to the power n
    private int n;
    private int nPow;
    private String niceASCIIPart;
    private StringBuilder niceASCII;
    private IntVar[][] grid;
    private int[] sudokuValues;
    private Solution theSolution;

    public MySudoku(int dimension) {
        this.n = dimension;
        this.nPow = n * n;
        this.model = new Model("My sudoku of size " + n);

        niceASCIIPart = "==============";
        niceASCII = new StringBuilder();
        for (int i = 0; i < n; i++)
            niceASCII.append(niceASCIIPart);

        grid = new IntVar[nPow][nPow];

        // creates the values domain of each cell
        sudokuValues = new int[nPow];
        for (int i = 0; i < nPow; i++)
            sudokuValues[i] = i+1;



        // apply the sudoku rules
        applyConstraints();
    }

    public void findSolution() {
       this.theSolution = model.getSolver().findSolution();
    }

    public void printSolution() {
        if(theSolution != null) {

            for (int i = 0; i < nPow; i++) {
                if ((i) % n == 0)
                    System.out.print("\n" + niceASCII + "\n");
                for (int j = 0; j < nPow; j++) {
                    if ((j) % n == 0)
                        System.out.print("    ");
                    System.out.print(" " + grid[i][j].getValue() + " ");
                    if ((j + 1) % nPow == 0)
                        System.out.print("\n");
                }
            }
        }
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


}
