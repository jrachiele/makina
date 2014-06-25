package org.platanios.learn.optimization.linesearch;

import org.apache.commons.math3.linear.RealVector;

/**
 * @author Emmanouil Antonios Platanios
 */
public interface LineSearch {
    /**
     * Computes the step size value using the implemented algorithm.
     *
     * @param   currentPoint
     * @param   direction
     * @return
     */
    public double computeStepSize(RealVector currentPoint, RealVector direction);
}