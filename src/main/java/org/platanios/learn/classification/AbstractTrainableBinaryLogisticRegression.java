package org.platanios.learn.classification;

import org.platanios.learn.math.matrix.MatrixUtilities;
import org.platanios.learn.math.matrix.Vector;
import org.platanios.learn.math.matrix.VectorFactory;
import org.platanios.learn.optimization.function.AbstractFunction;
import org.platanios.learn.optimization.function.AbstractStochasticFunction;

import java.util.Arrays;
import java.util.List;

/**
 * This class implements a binary logistic regression model that is trained using the stochastic gradient descent
 * algorithm.
 *
 * @author Emmanouil Antonios Platanios
 */
abstract class AbstractTrainableBinaryLogisticRegression
        extends BinaryLogisticRegressionPrediction implements TrainableClassifier<Vector, Integer> {
    /** The data used to train this model. */
    private final DataInstance<Vector, Integer>[] trainingData;

    /**
     * This abstract class needs to be extended by the builder of its parent binary logistic regression class. It
     * provides an implementation for those parts of those builders that are common. This is basically part of a small
     * "hack" so that we can have inheritable builder classes.
     *
     * @param   <T> This type corresponds to the type of the final object to be built. That is, the super class of the
     *              builder class that extends this class, which in this case will be the
     *              {@link org.platanios.learn.classification.BinaryLogisticRegressionSGD} class.
     */
    protected static abstract class AbstractBuilder<T extends AbstractBuilder<T>>
            extends BinaryLogisticRegressionPrediction.AbstractBuilder<T> {
        /** The data used to train the logistic regression model to be built. */
        private final DataInstance<Vector, Integer>[] trainingData;

        /** Indicates whether /(L_1/) regularization is used. */
        protected boolean useL1Regularization = false;
        /** The /(L_1/) regularization weight used. This variable is only used when {@link #useL1Regularization} is set
         * to true. */
        protected double l1RegularizationWeight = 1;
        /** Indicates whether /(L_2/) regularization is used. */
        protected boolean useL2Regularization = false;
        /** The /(L_2/) regularization weight used. This variable is only used when {@link #useL2Regularization} is set
         * to true. */
        protected double l2RegularizationWeight = 1;

        /**
         * Constructs a builder object for a binary logistic regression model that will be trained with the provided
         * training data. This constructor should be used if the logistic regression model that is being built is going
         * to be trained.
         *
         * @param   trainingData    The training data with which the binary logistic regression model to be built by
         *                          this builder will be trained.
         */
        protected AbstractBuilder(DataInstance<Vector, Integer>[] trainingData) {
            this.trainingData = trainingData;
            numberOfFeatures = trainingData[0].getFeatures().size();
        }

        /**
         * Sets the {@link #useL1Regularization} field that indicates whether /(L_1/) regularization is used.
         *
         * @param   useL1Regularization The value to which to set the {@link #useL1Regularization} field.
         * @return                      This builder object itself. That is done so that we can use a nice and
         *                              expressive code format when we build objects using this builder class.
         */
        public T useL1Regularization(boolean useL1Regularization) {
            this.useL1Regularization = useL1Regularization;
            return self();
        }

        /**
         * Sets the {@link #l1RegularizationWeight} field that contains the value of the /(L_1/) regularization weight
         * used. This variable is only used when {@link #useL1Regularization} is set to true.
         *
         * @param   l1RegularizationWeight  The value to which to set the {@link #l1RegularizationWeight} field.
         * @return                          This builder object itself. That is done so that we can use a nice and
         *                                  expressive code format when we build objects using this builder class.
         */
        public T l1RegularizationWeight(double l1RegularizationWeight) {
            this.l1RegularizationWeight = l1RegularizationWeight;
            return self();
        }

        /**
         * Sets the {@link #useL2Regularization} field that indicates whether /(L_2/) regularization is used.
         *
         * @param   usel2Regularization The value to which to set the {@link #useL2Regularization} field.
         * @return                      This builder object itself. That is done so that we can use a nice and
         *                              expressive code format when we build objects using this builder class.
         */
        public T useL2Regularization(boolean usel2Regularization) {
            this.useL2Regularization = usel2Regularization;
            return self();
        }

        /**
         * Sets the {@link #l2RegularizationWeight} field that contains the value of the /(L_2/) regularization weight
         * used. This variable is only used when {@link #useL2Regularization} is set to true.
         *
         * @param   l2RegularizationWeight  The value to which to set the {@link #l2RegularizationWeight} field.
         * @return                          This builder object itself. That is done so that we can use a nice and
         *                                  expressive code format when we build objects using this builder class.
         */
        public T l2RegularizationWeight(double l2RegularizationWeight) {
            this.l2RegularizationWeight = l2RegularizationWeight;
            return self();
        }
    }

    /**
     * The builder class for this class. This is basically part of a small "hack" so that we can have inheritable
     * builder classes.
     */
    public static class Builder extends AbstractBuilder<Builder> {
        /**
         * Constructs a builder object for a binary logistic regression model that will be trained with the provided
         * training data using the stochastic gradient descent algorithm.
         *
         * @param   trainingData    The training data with which the binary logistic regression model to be built by
         *                          this builder will be trained.
         */
        public Builder(DataInstance<Vector, Integer>[] trainingData) {
            super(trainingData);
        }

        /** {@inheritDoc} */
        @Override
        protected Builder self() {
            return this;
        }
    }

    /**
     * Constructs a binary logistic regression object that uses the stochastic gradient descent algorithm to train the
     * model, given an appropriate builder object. This constructor can only be used from within the builder class of
     * this class.
     *
     * @param   builder The builder object to use.
     */
    protected AbstractTrainableBinaryLogisticRegression(AbstractBuilder<?> builder) {
        super(builder);
        trainingData = builder.trainingData;
    }

    /**
     * Trains this logistic regression model using the data provided while building this object.
     */
    public abstract void train();

    /**
     * Class implementing the likelihood function for the binary logistic regression model. No function is provided to
     * compute the hessian matrix because no binary logistic regression class has to use it.
     */
    protected class LikelihoodFunction extends AbstractFunction {
        /**
         * Computes the value of the likelihood function for the binary logistic regression model.
         *
         * @param   weights The current weights vector.
         * @return          The value of the logistic regression likelihood function.
         */
        @Override
        public double computeValue(Vector weights) {
            double likelihood = 0;
            for (DataInstance<Vector, Integer> dataInstance : trainingData) {
                double probability = weights.dot(dataInstance.getFeatures());
                likelihood += probability * dataInstance.getLabel() - MatrixUtilities.computeLogSumExp(0, probability);
            }
            return -likelihood;
        }

        /**
         * Computes the gradient of the likelihood function for the binary logistic regression model.
         *
         * @param   weights The current weights vector.
         * @return          The gradient vector of the logistic regression likelihood function.
         */
        @Override
        public Vector computeGradient(Vector weights) {
            Vector gradient = VectorFactory.build(weights.size(), weights.type());
            for (DataInstance<Vector, Integer> dataInstance : trainingData) {
                double probability = weights.dot(dataInstance.getFeatures());
                gradient.addInPlace(dataInstance.getFeatures().mult(
                        Math.exp(probability - MatrixUtilities.computeLogSumExp(0, probability)) - dataInstance.getLabel()
                ));
            }
            return gradient;
        }
    }

    /**
     * Class implementing the likelihood function for the binary logistic regression model for use with stochastic
     * solvers.
     */
    protected class StochasticLikelihoodFunction extends AbstractStochasticFunction<DataInstance<Vector, Integer>> {
        public StochasticLikelihoodFunction() {
            // Using the method Arrays.asList so that the training data array is not duplicated. The newly created list
            // is backed by the existing array and any changes made to the list also "write through" to the array.
            this.data = Arrays.asList(trainingData);
        }

        /**
         * Computes the gradient of the likelihood function for the multi-class logistic regression model.
         *
         * @param weights The current weights vector.
         * @return The gradient vector of the logistic regression likelihood function.
         */
        @Override
        public Vector estimateGradient(Vector weights, List<DataInstance<Vector, Integer>> dataBatch) {
            Vector gradient = VectorFactory.build(weights.size(), weights.type());
            for (DataInstance<Vector, Integer> dataInstance : dataBatch) {
                double probability = weights.dot(dataInstance.getFeatures());
                gradient.addInPlace(dataInstance.getFeatures().mult(
                        Math.exp(probability - MatrixUtilities.computeLogSumExp(0, probability)) - dataInstance.getLabel()
                ));
            }
            return gradient;
        }
    }
}