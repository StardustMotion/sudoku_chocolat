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
        String timeLimit = "10s";
        int maxGridAmount = 500;
        boolean restart = true;
        String difficulty;

        MySudoku sudoku = new MySudoku(dimension, timeLimit, maxGridAmount, restart);

        // find some grids with these parameters. Set arg to TRUE to display all the computed grids
        sudoku.generateGrid(false);

        // print solution calculation stats
        sudoku.stats();




    /*
        Solution solution = model.getSolver().findSolution();
        if(solution != null){
            System.out.println(solution.toString());*/
        }



}

