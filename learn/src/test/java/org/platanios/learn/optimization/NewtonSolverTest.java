package org.platanios.learn.optimization;

import org.junit.Assert;
import org.junit.Test;
import org.platanios.learn.math.matrix.Matrix;
import org.platanios.learn.math.matrix.Vector;
import org.platanios.learn.math.matrix.Vectors;
import org.platanios.learn.optimization.function.QuadraticFunction;

/**
 * @author Emmanouil Antonios Platanios
 */
public class NewtonSolverTest {
    @Test
    public void testNewtonSolver() {
        System.out.println("Rosenbrock Function Newton Solver:\n");
        NewtonSolver newtonSolver =
                new NewtonSolver.Builder(new RosenbrockFunction(),
                                         Vectors.dense(new double[]{-1.2, 1})).build();
        double[] actualResult = newtonSolver.solve().getDenseArray();
        double[] expectedResult = new double[] { 1, 1 };
        Assert.assertArrayEquals(expectedResult, actualResult, 1e-2);

        System.out.println("Quadratic Function Newton Solver:\n");
        Matrix A = new Matrix(new double[][] { { 1, 0.5 }, { 0.5, 1 } });
        Vector b = Vectors.dense(new double[]{1, 2});
        newtonSolver = new NewtonSolver.Builder(new QuadraticFunction(A, b),
                                                       Vectors.dense(new double[]{0, 0})).build();
        actualResult = newtonSolver.solve().getDenseArray();
        expectedResult = new double[] { 0, 2 };
        Assert.assertArrayEquals(expectedResult, actualResult, 1e-2);
    }
}
