package org.platanios.learn.optimization;

import org.platanios.learn.optimization.function.AbstractStochasticFunction;

/**
 * @author Emmanouil Antonios Platanios
 */
public final class StochasticGradientDescentSolver extends AbstractStochasticIterativeSolver {
    protected static abstract class AbstractBuilder<T extends AbstractBuilder<T>>
            extends AbstractStochasticIterativeSolver.AbstractBuilder<T> {
        public AbstractBuilder(AbstractStochasticFunction objective, double[] initialPoint) {
            super(objective, initialPoint);
        }

        public StochasticGradientDescentSolver build() {
            return new StochasticGradientDescentSolver(this);
        }
    }

    public static class Builder extends AbstractBuilder<Builder> {
        public Builder(AbstractStochasticFunction objective,
                       double[] initialPoint) {
            super(objective, initialPoint);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    private StochasticGradientDescentSolver(AbstractBuilder<?> builder) {
        super(builder);
    }

    @Override
    public void updateDirection() {
        currentDirection = currentGradient.multiply(-1);
    }

    @Override
    public void updatePoint() {
        currentPoint = previousPoint.add(currentDirection.multiply(currentStepSize));
    }
}
