package org.platanios.learn.optimization;

/**
 * @author Emmanouil Antonios Platanios
 */
public class SteepestDescentSolver extends AbstractSolver {
    public SteepestDescentSolver(ObjectiveFunctionWithGradient objectiveFunction,
                                 double[] initialPoint) {
        super(objectiveFunction, new BacktrackingLineSearchAlgorithm(objectiveFunction, 1.0, 0.9, 1e-4), initialPoint);
    }

    public void updateDirection() {
        currentDirection =
                ((ObjectiveFunctionWithGradient) objectiveFunction).computeGradient(currentPoint).mapMultiply(-1);
    }

    public void updatePoint() {
        double stepSize = lineSearchAlgorithm.computeStepSize(currentPoint, currentDirection);
        currentPoint = currentPoint.add(currentDirection.mapMultiply(stepSize));
    }

    public boolean checkForConvergence() {
        pointL2NormChange = currentPoint.subtract(previousPoint).getNorm();
        objectiveChange = Math.abs((previousObjectiveValue - currentObjectiveValue) / previousObjectiveValue);
        pointL2NormConverged = pointL2NormChange <= pointL2NormChangeTolerance;
        objectiveConverged = objectiveChange <= objectiveChangeTolerance;

        return pointL2NormConverged || objectiveConverged;
    }
}
