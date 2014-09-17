package org.platanios.learn.optimization.function;

import org.platanios.learn.math.matrix.Matrix;
import org.platanios.learn.math.Utilities;
import org.platanios.learn.math.matrix.Vector;

/**
 * TODO: Jacobian approximation.
 *
 * @author Emmanouil Antonios Platanios
 */
public final class DerivativesApproximation {
    private final AbstractFunction function;
    private final double epsilon;

    private Method method;

    public DerivativesApproximation(AbstractFunction function, Method method) {
        this.function = function;
        this.method = method;
        epsilon = method.computeEpsilon();
    }

    public Vector approximateGradient(Vector point) {
        return method.approximateGradient(this, point);
    }

    public Matrix approximateHessian(Vector point) {
        return method.approximateHessian(this, point);
    }

    public Matrix approximateHessianGivenGradient(Vector point) {
        return method.approximateHessianGivenGradient(this, point);
    }

    public Vector approximateHessianVectorProductGivenGradient(Vector point, Vector p) {
        return method.approximateHessianVectorProductGivenGradient(this, point, p);
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public enum Method {
        FORWARD_DIFFERENCE {
            protected double computeEpsilon() {
                return Math.sqrt(Utilities.computeMachineEpsilonDouble());
            }

            protected Vector approximateGradient(DerivativesApproximation owner, Vector point) {
                int n = point.getDimension();
                Vector gradient = new Vector(n, 0);
                Vector ei = new Vector(n, 0);
                double currentFunctionValue = owner.function.getValue(point);
                for (int i = 0; i < n; i++) {
                    ei.setAllElements(0);
                    ei.setElement(i, 1);
                    double forwardFunctionValue = owner.function.getValue(point.add(ei.multiply(owner.epsilon)));
                    gradient.setElement(i, (forwardFunctionValue - currentFunctionValue) / owner.epsilon);
                }
                return gradient;
            }

            protected Matrix approximateHessian(DerivativesApproximation owner, Vector point) {
                int n = point.getDimension();
                Matrix hessian = new Matrix(n, n);
                Vector ei = new Vector(n, 0);
                Vector ej = new Vector(n, 0);
                double currentFunctionValue = owner.function.getValue(point);
                for (int i = 0; i < n; i++) {
                    ei.setAllElements(0);
                    ei.setElement(i, 1);
                    double iFunctionValue = owner.function.getValue(point.add(ei.multiply(owner.epsilon)));
                    for (int j = i; j < n; j++) {
                        ej.setAllElements(0);
                        ej.setElement(j, 1);
                        double jFunctionValue = owner.function.getValue(point.add(ej.multiply(owner.epsilon)));
                        double ijFunctionValue = owner.function.getValue(point.add(ei.add(ej).multiply(owner.epsilon)));
                        double ijEntry = (ijFunctionValue - iFunctionValue - jFunctionValue + currentFunctionValue)
                                / Math.pow(owner.epsilon, 2);
                        hessian.setElement(i, j, ijEntry);
                        if (i != j) {
                            hessian.setElement(j, i, ijEntry);
                        }
                    }
                }
                return hessian;
            }

            protected Matrix approximateHessianGivenGradient(DerivativesApproximation owner, Vector point) {
                int n = point.getDimension();
                Matrix hessian = new Matrix(n, n);
                Vector ei = new Vector(n, 0);
                Vector currentGradientValue = owner.function.getGradient(point);
                for (int i = 0; i < n; i++) {
                    ei.setAllElements(0);
                    ei.setElement(i, 1);
                    Vector forwardGradientValue = owner.function.getGradient(point.add(ei.multiply(owner.epsilon)));
                    hessian.setSubMatrix(
                            0,
                            hessian.getRowDimension() - 1,
                            i,
                            i,
                            forwardGradientValue.subtract(currentGradientValue).multiply(1 / owner.epsilon)
                                    .copyAsMatrix()
                    );
                }
                return hessian;
            }

            protected Vector approximateHessianVectorProductGivenGradient(DerivativesApproximation owner,
                                                                          Vector point,
                                                                          Vector p) {
                Vector forwardGradientValue = owner.function.getGradient(point.add(p.multiply(owner.epsilon)));
                Vector currentGradientValue = owner.function.getGradient(point);
                return forwardGradientValue.subtract(currentGradientValue).multiply(1 / owner.epsilon);
            }
        },
        /** Much more accurate than the forward-difference method (\(O(\epsilon^2)\) estimation error instead of
         * \(O(\epsilon)\)). */
        CENTRAL_DIFFERENCE {
            protected double computeEpsilon() {
                return Math.cbrt(Utilities.computeMachineEpsilonDouble());
            }

            protected Vector approximateGradient(DerivativesApproximation owner, Vector point) {
                int n = point.getDimension();
                Vector gradient = new Vector(n, 0);
                Vector ei = new Vector(n, 0);
                for (int i = 0; i < n; i++) {
                    ei.setAllElements(0);
                    ei.setElement(i, 1);
                    double forwardFunctionValue = owner.function.getValue(point.add(ei.multiply(owner.epsilon)));
                    double backwardFunctionValue = owner.function.getValue(point.subtract(ei.multiply(owner.epsilon)));
                    gradient.setElement(i, (forwardFunctionValue - backwardFunctionValue) / (2 * owner.epsilon));
                }
                return gradient;
            }

            protected Matrix approximateHessian(DerivativesApproximation owner, Vector point) {
                int n = point.getDimension();
                Matrix hessian = new Matrix(n, n);
                Vector ei = new Vector(n, 0);
                Vector ej = new Vector(n, 0);
                double currentFunctionValue = owner.function.getValue(point);
                for (int i = 0; i < n; i++) {
                    ei.setAllElements(0);
                    ei.setElement(i, 1);
                    for (int j = i; j < n; j++) {
                        ej.setAllElements(0);
                        ej.setElement(j, 1);
                        if (i != j) {
                            double term1 = owner.function.getValue(point.add(ei.add(ej).multiply(owner.epsilon)));
                            double term2 = owner.function.getValue(point.add(ei.subtract(ej).multiply(owner.epsilon)));
                            double term3 = owner.function.getValue(point.add(ei.subtract(ej).multiply(-owner.epsilon)));
                            double term4 = owner.function.getValue(point.add(ei.add(ej).multiply(-owner.epsilon)));
                            double ijEntry = (term1 - term2 - term3 + term4) / (4 * Math.pow(owner.epsilon, 2));
                            hessian.setElement(i, j, ijEntry);
                            hessian.setElement(j, i, ijEntry);
                        } else {
                            double term1 = owner.function.getValue(point.add(ei.multiply(2 * owner.epsilon)));
                            double term2 = owner.function.getValue(point.add(ei.multiply(owner.epsilon)));
                            double term3 = owner.function.getValue(point.subtract(ei.multiply(owner.epsilon)));
                            double term4 = owner.function.getValue(point.subtract(ei.multiply(2 * owner.epsilon)));
                            double ijEntry = (- term1 + 16 * term2 - 30 * currentFunctionValue + 16 * term3 - term4)
                                    / (12 * Math.pow(owner.epsilon, 2));
                            hessian.setElement(i, j, ijEntry);
                        }
                    }
                }
                return hessian;
            }

            protected Matrix approximateHessianGivenGradient(DerivativesApproximation owner, Vector point) {
                int n = point.getDimension();
                Matrix hessian = new Matrix(n, n);
                Vector ei = new Vector(n, 0);
                for (int i = 0; i < n; i++) {
                    ei.setAllElements(0);
                    ei.setElement(i, 1);
                    Vector forwardGradientValue = owner.function.getGradient(point.add(ei.multiply(owner.epsilon)));
                    Vector backwardGradientValue =
                            owner.function.getGradient(point.subtract(ei.multiply(owner.epsilon)));
                    hessian.setSubMatrix(
                            0,
                            hessian.getRowDimension() - 1,
                            i,
                            i,
                            forwardGradientValue
                                    .subtract(backwardGradientValue).multiply(1 / (2 * owner.epsilon)).copyAsMatrix()
                    );
                }
                return hessian;
            }

            protected Vector approximateHessianVectorProductGivenGradient(DerivativesApproximation owner,
                                                                          Vector point,
                                                                          Vector p) {
                Vector forwardGradientValue = owner.function.getGradient(point.add(p.multiply(owner.epsilon)));
                Vector backwardGradientValue = owner.function.getGradient(point.subtract(p.multiply(owner.epsilon)));
                return forwardGradientValue.subtract(backwardGradientValue).multiply(1 / (2 * owner.epsilon));
            }
        };

        protected abstract double computeEpsilon();
        protected abstract Vector approximateGradient(DerivativesApproximation owner, Vector point);
        protected abstract Matrix approximateHessian(DerivativesApproximation owner, Vector point);
        protected abstract Matrix approximateHessianGivenGradient(DerivativesApproximation owner, Vector point);
        protected abstract Vector approximateHessianVectorProductGivenGradient(DerivativesApproximation owner,
                                                                               Vector point,
                                                                               Vector p);
    }
}