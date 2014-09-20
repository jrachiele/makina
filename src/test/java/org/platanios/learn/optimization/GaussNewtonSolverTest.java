package org.platanios.learn.optimization;

import org.junit.Assert;
import org.junit.Test;
import org.platanios.learn.math.matrix.Matrix;
import org.platanios.learn.math.matrix.Vector;
import org.platanios.learn.math.matrix.VectorFactory;
import org.platanios.learn.optimization.function.AbstractLeastSquaresFunction;

/**
 * @author Emmanouil Antonios Platanios
 */
public class GaussNewtonSolverTest {
    @Test
    public void testGaussNewtonCholeskyDecompositionSolver() {
        System.out.println("Exponential Least Squares Function Gauss-Newton (Cholesky Decomposition) Solver:\n");
        double[] t = { 1, 2, 4, 5, 8 };
        double[] y = { 3.2939, 4.2699, 7.1749, 9.3008, 20.259 };
        GaussNewtonSolver gaussNewtonSolver =
                new GaussNewtonSolver.Builder(new ExponentialLeastSquaresFunction(t, y), new double[] { 0, 0 })
                        .linearLeastSquaresSubproblemMethod(LinearLeastSquaresSolver.Method.CHOLESKY_DECOMPOSITION)
                        .build();
        double[] actualResult = gaussNewtonSolver.solve().getDenseArray();
        double[] expectedResult = new double[] { 2.5411, 0.2595 };
        Assert.assertArrayEquals(expectedResult, actualResult, 1e-4);
    }

    @Test
    public void testGaussNewtonQRDecompositionSolver() {
        System.out.println("Exponential Least Squares Function Gauss-Newton (QR Decomposition) Solver:\n");
        double[] t = { 1, 2, 4, 5, 8 };
        double[] y = { 3.2939, 4.2699, 7.1749, 9.3008, 20.259 };
        GaussNewtonSolver gaussNewtonSolver =
                new GaussNewtonSolver.Builder(new ExponentialLeastSquaresFunction(t, y), new double[] { 0, 0 })
                        .linearLeastSquaresSubproblemMethod(LinearLeastSquaresSolver.Method.QR_DECOMPOSITION)
                        .build();
        double[] actualResult = gaussNewtonSolver.solve().getDenseArray();
        double[] expectedResult = new double[] { 2.5411, 0.2595 };
        Assert.assertArrayEquals(expectedResult, actualResult, 1e-4);
    }

    @Test
    public void testGaussNewtonSingularValueDecompositionSolver() {
        System.out.println("Exponential Least Squares Function Gauss-Newton (Singular Value Decomposition) Solver:\n");
        double[] t = { 1, 2, 4, 5, 8 };
        double[] y = { 3.2939, 4.2699, 7.1749, 9.3008, 20.259 };
        GaussNewtonSolver gaussNewtonSolver =
                new GaussNewtonSolver.Builder(new ExponentialLeastSquaresFunction(t, y), new double[] { 0, 0 })
                        .linearLeastSquaresSubproblemMethod(LinearLeastSquaresSolver.Method.SINGULAR_VALUE_DECOMPOSITION)
                        .build();
        double[] actualResult = gaussNewtonSolver.solve().getDenseArray();
        double[] expectedResult = new double[] { 2.5411, 0.2595 };
        Assert.assertArrayEquals(expectedResult, actualResult, 1e-4);
    }

    @Test
    public void testGaussNewtonConjugateGradientSolver() {
        System.out.println("Exponential Least Squares Function Gauss-Newton (Conjugate Gradient) Solver:\n");
        double[] t = { 1, 2, 4, 5, 8 };
        double[] y = { 3.2939, 4.2699, 7.1749, 9.3008, 20.259 };
        GaussNewtonSolver gaussNewtonSolver =
                new GaussNewtonSolver.Builder(new ExponentialLeastSquaresFunction(t, y), new double[] { 0, 0 })
                        .linearLeastSquaresSubproblemMethod(LinearLeastSquaresSolver.Method.CONJUGATE_GRADIENT)
                        .build();
        double[] actualResult = gaussNewtonSolver.solve().getDenseArray();
        double[] expectedResult = new double[] { 2.5411, 0.2595 };
        Assert.assertArrayEquals(expectedResult, actualResult, 1e-4);
    }

    public class ExponentialLeastSquaresFunction extends AbstractLeastSquaresFunction {
        private final double[] t;
        private final double[] y;

        public ExponentialLeastSquaresFunction(double[] t, double[] y) {
            this.t = t;
            this.y = y;
        }

        @Override
        public Vector computeResiduals(Vector point) {
            double[] resultArray = new double[t.length];
            for (int i = 0; i < t.length; i++) {
                resultArray[i] = point.get(0) * Math.exp(point.get(1) * t[i]) - y[i];
            }
            return VectorFactory.buildDense(resultArray);
        }

        @Override
        public Matrix computeJacobian(Vector point) {
            double[][] resultArray = new double[t.length][2];
            for (int i = 0; i < t.length; i++) {
                resultArray[i][0] = Math.exp(point.get(1) * t[i]);
                resultArray[i][1] = point.get(0) * Math.exp(point.get(1) * t[i]) * t[i];
            }
            return new Matrix(resultArray);
        }
    }
}
