package org.platanios.learn.optimization;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.platanios.learn.optimization.function.AbstractFunction;

/**
 * @author Emmanouil Antonios Platanios
 */
class RosenbrockFunction extends AbstractFunction {
    @Override
    public double computeValue(RealVector optimizationVariables) {
        double x1 = optimizationVariables.getEntry(0);
        double x2 = optimizationVariables.getEntry(1);
        return 100 * Math.pow(x2 - Math.pow(x1, 2), 2) + Math.pow(1 - x1, 2);
    }

    @Override
    public RealVector computeGradient(RealVector optimizationVariables) {
        double x1 = optimizationVariables.getEntry(0);
        double x2 = optimizationVariables.getEntry(1);
        double dx1 = - 400 * (x2 - Math.pow(x1, 2)) * x1 - 2 * (1 - x1);
        double dx2 = 200 * (x2 - Math.pow(x1, 2));
        return new ArrayRealVector(new double[] { dx1, dx2 });
    }

    @Override
    public RealMatrix computeHessian(RealVector optimizationVariables) {
        double x1 = optimizationVariables.getEntry(0);
        double x2 = optimizationVariables.getEntry(1);
        double dx1x1 = 1200 * Math.pow(x1, 2) - 400 * x2 + 2;
        double dx1x2 = - 400 * x1;
        double dx2x2 = 200;
        return new Array2DRowRealMatrix(new double[][] { { dx1x1, dx1x2 }, { dx1x2, dx2x2 } });
    }
}
