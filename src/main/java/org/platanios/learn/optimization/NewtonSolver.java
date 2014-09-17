package org.platanios.learn.optimization;

import org.platanios.learn.math.matrix.Matrix;
import org.platanios.learn.math.matrix.SingularMatrixException;
import org.platanios.learn.optimization.function.AbstractFunction;
import org.platanios.learn.optimization.linesearch.StepSizeInitializationMethod;
import org.platanios.learn.optimization.linesearch.StrongWolfeInterpolationLineSearch;

/**
 * @author Emmanouil Antonios Platanios
 */
public final class NewtonSolver extends AbstractLineSearchSolver {
    protected static abstract class AbstractBuilder<T extends AbstractBuilder<T>>
            extends AbstractLineSearchSolver.AbstractBuilder<T> {
        public AbstractBuilder(AbstractFunction objective, double[] initialPoint) {
            super(objective, initialPoint);
            // TODO: Figure out why we cannot use exact line search in the case of a quadratic function.
            lineSearch = new StrongWolfeInterpolationLineSearch(objective, 1e-4, 0.9, 1);
            ((StrongWolfeInterpolationLineSearch) lineSearch)
                    .setStepSizeInitializationMethod(StepSizeInitializationMethod.UNIT);
        }

        public NewtonSolver build() {
            return new NewtonSolver(this);
        }
    }

    public static class Builder extends AbstractBuilder<Builder> {
        public Builder(AbstractFunction objective,
                       double[] initialPoint) {
            super(objective, initialPoint);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    private NewtonSolver(AbstractBuilder<?> builder) {
        super(builder);
    }

    /**
     * Here, if the Hessian matrix is not positive definite, we modify it so that the bounded modified factorization
     * property holds for it and we have global convergence for Newton's method.
     */
    @Override
    public void updateDirection() {
        Matrix hessian = objective.getHessian(currentPoint);
        // TODO: Check Hessian for positive definiteness and modify if necessary.
        currentGradient = objective.getGradient(currentPoint);
        try {
            currentDirection = hessian.solve(currentGradient).multiply(-1);
        } catch (SingularMatrixException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updatePoint() {
        currentPoint = previousPoint.add(currentDirection.multiply(currentStepSize));
    }
}