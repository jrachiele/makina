package org.platanios.learn.optimization.function;

import org.platanios.learn.math.matrix.Matrix;
import org.platanios.learn.math.matrix.Vector;
import org.platanios.learn.math.matrix.Vectors;

/**
 * @author Emmanouil Antonios Platanios
 */
public final class ProbabilisticSoftLogicFunction extends SumFunction {
    public static class Builder {
        private final SumFunction.Builder sumFunctionBuilder;

        public Builder(int numberOfVariables) {
            sumFunctionBuilder = new SumFunction.Builder(numberOfVariables);
        }

        public Builder addTerm(int[] variablesIndexes,
                               double[] coefficients,
                               double constant,
                               double power,
                               double weight) {
            MaxFunction.Builder maxFunctionBuilder = new MaxFunction.Builder(sumFunctionBuilder.numberOfVariables);
            maxFunctionBuilder.addConstantTerm(0);
            maxFunctionBuilder.addFunctionTerm(new LinearFunction(Vectors.dense(coefficients), constant));
            sumFunctionBuilder.addTerm(
                    new ProbabilisticSoftLogicSumFunctionTerm(maxFunctionBuilder.build(), power, weight),
                    variablesIndexes
            );
            return this;
        }

        public ProbabilisticSoftLogicFunction build() {
            return new ProbabilisticSoftLogicFunction(this);
        }
    }

    private ProbabilisticSoftLogicFunction(Builder builder) {
        super(builder.sumFunctionBuilder);
    }

    public LinearFunction getTermLinearFunction(int term) {
        return ((ProbabilisticSoftLogicSumFunctionTerm) terms.get(term)).getLinearFunction();
    }

    public double getTermPower(int term) {
        return ((ProbabilisticSoftLogicSumFunctionTerm) terms.get(term)).getPower();
    }

    public double getTermWeight(int term) {
        return ((ProbabilisticSoftLogicSumFunctionTerm) terms.get(term)).getWeight();
    }

    public static class ProbabilisticSoftLogicSumFunctionTerm extends AbstractFunction {
        private final MaxFunction maxFunction;
        private final double power;
        private final double weight;

        private ProbabilisticSoftLogicSumFunctionTerm(MaxFunction maxFunction, double power, double weight) {
            this.maxFunction = maxFunction;
            this.power = power;
            this.weight = weight;
        }

        @Override
        public double computeValue(Vector point) {
            return weight * Math.pow(maxFunction.computeValue(point), power);
        }

        public MaxFunction getMaxFunction() {
            return maxFunction;
        }

        public LinearFunction getLinearFunction() {
            return (LinearFunction) maxFunction.getFunctionTerm(0);
        }

        public double getPower() {
            return power;
        }

        public double getWeight() {
            return weight;
        }

        public ProbabilisticSoftLogicSubProblemObjectiveFunction getSubProblemObjectiveFunction() {
            return new ProbabilisticSoftLogicSubProblemObjectiveFunction(
                    (LinearFunction) maxFunction.getFunctionTerm(0),
                    power,
                    weight
            );
        }
    }

    public static class ProbabilisticSoftLogicSubProblemObjectiveFunction extends AbstractFunction {
        private final LinearFunction linearFunction;
        private final double power;
        private final double weight;

        private ProbabilisticSoftLogicSubProblemObjectiveFunction(LinearFunction linearFunction,
                                                                  double power,
                                                                  double weight) {
            this.linearFunction = linearFunction;
            this.power = power;
            this.weight = weight;
        }

        @Override
        public double computeValue(Vector point) {
            return weight * Math.pow(linearFunction.computeValue(point), power);
        }

        @Override
        public Vector computeGradient(Vector point) {
            if (power > 0) {
                return linearFunction.computeGradient(point).mult(
                        weight * power * Math.pow(linearFunction.computeValue(point), power - 1)
                );
            } else {
                return Vectors.build(point.size(), point.type());
            }
        }

        @Override
        public Matrix computeHessian(Vector point) {
            if (power > 1) {
                Vector a = linearFunction.computeGradient(point);
                return Matrix.generateDiagonalMatrix(a.multElementwise(a).getDenseArray())
                        .multiply(weight * power * (power - 1) * Math.pow(linearFunction.computeValue(point), power - 2));
            } else {
                return new Matrix(point.size(), point.size());
            }
        }
    }
}