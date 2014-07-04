package org.platanios.learn.math.matrix;

import org.platanios.learn.math.Utilities;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author Emmanouil Antonios Platanios
 */
public class Matrix {
    /** The row dimension of the internal two-dimensional array. */
    private final int rowDimension;
    /** The column dimension of the internal two-dimensional array. */
    private final int columnDimension;

    /** Array for internal storage of the matrix elements. */
    private double[][] array;

    //region Constructors
    /**
     * Constructs a matrix with the given dimensions and fills it with zeros.
     *
     * @param   rowDimension        The row dimension of the matrix.
     * @param   columnDimension     The column dimension of the matrix.
     */
    public Matrix(int rowDimension, int columnDimension) {
        this.rowDimension = rowDimension;
        this.columnDimension = columnDimension;
        array = new double[rowDimension][columnDimension];
    }

    /**
     * Constructs a matrix with the given dimensions and fills it with the given value.
     *
     * @param   rowDimension        The row dimension of the matrix.
     * @param   columnDimension     The column dimension of the matrix.
     * @param   value               The value with which to fill the matrix.
     */
    public Matrix(int rowDimension, int columnDimension, double value) {
        this.rowDimension = rowDimension;
        this.columnDimension = columnDimension;
        array = new double[rowDimension][columnDimension];
        for (int i = 0; i < rowDimension; i++) {
            for (int j = 0; j < columnDimension; j++) {
                array[i][j] = value;
            }
        }
    }

    /**
     * Constructs a matrix from a two-dimensional array.
     *
     * @param       array       Two-dimensional array of doubles.
     * @param       copyArray   Boolean value indicating whether to copy the provided array or use it as it is for the
     *                          internal two-dimensional array of this array.
     *
     * @exception   IllegalArgumentException    All rows of the input array must have the same length.
     */
    public Matrix(double[][] array, boolean copyArray) {
        rowDimension = array.length;
        columnDimension = array[0].length;
        if (copyArray) {
            this.array = new double[rowDimension][columnDimension];
            for (int i = 0; i < rowDimension; i++) {
                if (array[i].length != columnDimension) {
                    throw new IllegalArgumentException("All rows of the input matrix must have the same length.");
                }
                System.arraycopy(array[i], 0, this.array[i], 0, columnDimension);
            }
        } else {
            for (int i = 0; i < rowDimension; i++) {
                if (array[i].length != columnDimension) {
                    throw new IllegalArgumentException("All rows of the input matrix must have the same length.");
                }
            }
            this.array = array;
        }
    }

    /**
     * Constructs a matrix from a two-dimensional array quickly, without checking the arguments. The provided array is
     * used as it is for the internal two-dimensional array of this array.
     *
     * @param   array   Two-dimensional array of doubles.
     */
    public Matrix(double[][] array, int rowDimension, int columnDimension) {
        this.rowDimension = rowDimension;
        this.columnDimension = columnDimension;
        this.array = array;
    }

    /**
     * Constructs a matrix from a one-dimensional packed array (packed by columns, as in FORTRAN).
     *
     * @param   elements        One-dimensional array of doubles, packed by columns (as in FORTRAN).
     * @param   rowDimension    The row dimension of the matrix.
     *
     * @exception   IllegalArgumentException    The length of the input array must be a multiple of
     *                                          {@code rowDimension}.
     */
    public Matrix(double[] elements, int rowDimension) {
        this.rowDimension = rowDimension;
        columnDimension = rowDimension != 0 ? elements.length / rowDimension : 0;
        if (rowDimension * columnDimension != elements.length) {
            throw new IllegalArgumentException(
                    "The length of the input array must be a multiple of the row dimension."
            );
        }
        array = new double[rowDimension][columnDimension];
        for (int i = 0; i < rowDimension; i++) {
            for (int j = 0; j < columnDimension; j++) {
                array[i][j] = elements[i + j * rowDimension];
            }
        }
    }
    //endregion

    //region Getters, Setters and Other Such Methods
    /**
     * Copies this matrix.
     *
     * @return  A copy of this matrix.
     */
    public Matrix copy() {
        Matrix resultMatrix = new Matrix(rowDimension, columnDimension);
        double[][] resultMatrixArray = resultMatrix.getArray();
        for (int i = 0; i < rowDimension; i++) {
            System.arraycopy(array[i], 0, resultMatrixArray[i], 0, columnDimension);
        }
        return resultMatrix;
    }

    /**
     * Gets a pointer to the internal two-dimensional array.
     *
     * @return  A pointer to the internal two-dimensional array.
     */
    public double[][] getArray() {
        return array;
    }

    /**
     * Copies the internal two-dimensional array.
     *
     * @return  A copy of the internal two-dimensional array.
     */
    public double[][] getArrayCopy() {
        double[][] resultArray = new double[rowDimension][columnDimension];
        for (int i = 0; i < rowDimension; i++) {
            System.arraycopy(array[i], 0, resultArray[i], 0, columnDimension);
        }
        return resultArray;
    }

    /**
     * Makes a one-dimensional column packed copy of the internal two-dimensional array.
     *
     * @return  One-dimensional array containing the array elements packed by column.
     */
    public double[] getColumnPackedArrayCopy() {
        double[] elements = new double[rowDimension * columnDimension];
        for (int i = 0; i < rowDimension; i++) {
            for (int j = 0; j < columnDimension; j++) {
                elements[i + j * rowDimension] = array[i][j];
            }
        }
        return elements;
    }

    /**
     * Makes a one-dimensional row packed copy of the internal two-dimensional array.
     *
     * @return  One-dimensional array containing the array elements packed by row.
     */
    public double[] getRowPackedArrayCopy() {
        double[] elements = new double[rowDimension * columnDimension];
        for (int i = 0; i < rowDimension; i++) {
            for (int j = 0; j < columnDimension; j++) {
                elements[i * columnDimension + j] = array[i][j];
            }
        }
        return elements;
    }

    /**
     * Gets the row dimension of this matrix (that is, the number of rows of this matrix).
     *
     * @return  The row dimension of this matrix.
     */
    public int getRowDimension() {
        return rowDimension;
    }

    /**
     * Gets the column dimension of this matrix (that is, the number of columns of this matrix).
     *
     * @return  The column dimension of this matrix.
     */
    public int getColumnDimension() {
        return columnDimension;
    }

    /**
     * Gets the value of the matrix element at the provided position.
     *
     * @param   row     The row index of the element.
     * @param   column  The column index of the element.
     * @return          The value of the element at the provided position.
     */
    public double getElement(int row, int column) {
        return array[row][column]; // TODO: Check for the index values (i.e. out of bounds).
    }

    /**
     * Sets the value of the matrix element at the provided position to the provided value.
     *
     * @param   row     The row index of the element.
     * @param   column  The column index of the element.
     * @param   value   The value to which to set the element at the provided position.
     */
    public void setElement(int row, int column, double value) {
        array[row][column] = value;
    }

    /**
     * Gets a sub-matrix of this matrix.
     *
     * @param   initialRowIndex     The initial row index.
     * @param   finalRowIndex       The final row index.
     * @param   initialColumnIndex  The initial column index.
     * @param   finalColumnIndex    The final column index.
     * @return                      The sub-matrix corresponding to the provided indexes.
     *
     * @exception   ArrayIndexOutOfBoundsException  Some or all of the provided sub-matrix indexes are out of bounds.
     */
    public Matrix getSubMatrix(int initialRowIndex, int finalRowIndex, int initialColumnIndex, int finalColumnIndex) {
        Matrix resultMatrix =
                new Matrix(finalRowIndex - initialRowIndex + 1, finalColumnIndex - initialColumnIndex + 1);
        double[][] resultMatrixArray = resultMatrix.getArray();
        try {
            for (int i = initialRowIndex; i <= finalRowIndex; i++) {
                System.arraycopy(array[i],
                                 initialColumnIndex,
                                 resultMatrixArray[i - initialRowIndex],
                                 0,
                                 finalColumnIndex + 1 - initialColumnIndex);
            }
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException(
                    "Some or all of the provided sub-matrix indexes are out of bounds."
            );
        }
        return resultMatrix;
    }

    /**
     * Gets a sub-matrix of this matrix.
     *
     * @param   rowIndexes      The row indexes of the rows of this matrix to be included in the returned sub-matrix.
     * @param   columnIndexes   The column indexes of the columns of this matrix to be included in the returned
     *                          sub-matrix.
     * @return                  The sub-matrix corresponding to the provided indexes.
     *
     * @exception   ArrayIndexOutOfBoundsException  Some or all of the provided sub-matrix indexes are out of bounds.
     */
    public Matrix getSubMatrix(int[] rowIndexes, int[] columnIndexes) {
        Matrix resultMatrix = new Matrix(rowIndexes.length, columnIndexes.length);
        double[][] resultMatrixArray = resultMatrix.getArray();
        try {
            for (int i = 0; i < rowIndexes.length; i++) {
                for (int j = 0; j < columnIndexes.length; j++) {
                    resultMatrixArray[i][j] = array[rowIndexes[i]][columnIndexes[j]];
                }
            }
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException(
                    "Some or all of the provided sub-matrix indexes are out of bounds."
            );
        }
        return resultMatrix;
    }

    /**
     * Gets a sub-matrix of this matrix.
     *
     * @param   initialRowIndex The initial row index.
     * @param   finalRowIndex   The final row index.
     * @param   columnIndexes   The column indexes of the columns of this matrix to be included in the returned
     *                          sub-matrix.
     * @return                  The sub-matrix corresponding to the provided indexes.
     *
     * @exception   ArrayIndexOutOfBoundsException  Some or all of the provided sub-matrix indexes are out of bounds.
     */
    public Matrix getSubMatrix(int initialRowIndex, int finalRowIndex, int[] columnIndexes) {
        Matrix resultMatrix = new Matrix(finalRowIndex - initialRowIndex + 1, columnIndexes.length);
        double[][] resultMatrixArray = resultMatrix.getArray();
        try {
            for (int i = initialRowIndex; i <= finalRowIndex; i++) {
                for (int j = 0; j < columnIndexes.length; j++) {
                    resultMatrixArray[i - initialRowIndex][j] = array[i][columnIndexes[j]];
                }
            }
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException(
                    "Some or all of the provided sub-matrix indexes are out of bounds."
            );
        }
        return resultMatrix;
    }

    /**
     * Gets a sub-matrix of this matrix.
     *
     * @param   rowIndexes          The row indexes of the rows of this matrix to be included in the returned
     *                              sub-matrix.
     * @param   initialColumnIndex  The initial column index.
     * @param   finalColumnIndex    The final column index.
     * @return                      The sub-matrix corresponding to the provided indexes.
     *
     * @exception   ArrayIndexOutOfBoundsException  Some or all of the provided sub-matrix indexes are out of bounds.
     */
    public Matrix getSubMatrix(int[] rowIndexes, int initialColumnIndex, int finalColumnIndex) {
        Matrix resultMatrix = new Matrix(rowIndexes.length, finalColumnIndex - initialColumnIndex + 1);
        double[][] resultMatrixArray = resultMatrix.getArray();
        try {
            for (int i = 0; i < rowIndexes.length; i++) {
                System.arraycopy(array[rowIndexes[i]],
                                 initialColumnIndex,
                                 resultMatrixArray[i],
                                 0,
                                 finalColumnIndex + 1 - initialColumnIndex);
            }
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException(
                    "Some or all of the provided sub-matrix indexes are out of bounds."
            );
        }
        return resultMatrix;
    }

    /**
     * Sets a sub-matrix of this matrix to the provided matrix values.
     *
     * @param   initialRowIndex     The initial row index.
     * @param   finalRowIndex       The final row index.
     * @param   initialColumnIndex  The initial column index.
     * @param   finalColumnIndex    The final column index.
     * @param   matrix              The matrix to whose values we set the values of the specified sub-matrix of this
     *                              matrix.
     *
     * @exception   ArrayIndexOutOfBoundsException  Some or all of the provided matrix indexes are out of bounds.
     */
    public void setSubMatrix(int initialRowIndex,
                             int finalRowIndex,
                             int initialColumnIndex,
                             int finalColumnIndex,
                             Matrix matrix) {
        try {
            for (int i = initialRowIndex; i <= finalRowIndex; i++) {
                for (int j = initialColumnIndex; j <= finalColumnIndex; j++) {
                    array[i][j] = matrix.getElement(i - initialRowIndex, j - initialColumnIndex);
                }
            }
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException(
                    "Some or all of the provided matrix indexes are out of bounds."
            );
        }
    }

    /**
     * Sets a sub-matrix of this matrix to the provided matrix values.
     *
     * @param   rowIndexes      The row indexes of the rows of this matrix to be changed to values of the rows of the
     *                          provided sub-matrix.
     * @param   columnIndexes   The column indexes of the columns of this matrix to be changed to values of the columns
     *                          of the provided sub-matrix.
     * @param   matrix          The matrix to whose values we set the values of the specified sub-matrix of this matrix.
     *
     * @exception   ArrayIndexOutOfBoundsException  Some or all of the provided matrix indexes are out of bounds.
     */
    public void setSubMatrix(int[] rowIndexes, int[] columnIndexes, Matrix matrix) {
        try {
            for (int i = 0; i < rowIndexes.length; i++) {
                for (int j = 0; j < columnIndexes.length; j++) {
                    array[rowIndexes[i]][columnIndexes[j]] = matrix.getElement(i, j);
                }
            }
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException(
                    "Some or all of the provided matrix indexes are out of bounds."
            );
        }
    }

    /**
     * Sets a sub-matrix of this matrix to the provided matrix values.
     *
     * @param   rowIndexes          The row indexes of the rows of this matrix to be changed to values of the rows of
     *                              the provided sub-matrix.
     * @param   initialColumnIndex  The initial column index.
     * @param   finalColumnIndex    The final column index.
     * @param   matrix              The matrix to whose values we set the values of the specified sub-matrix of this
     *                              matrix.
     *
     * @exception   ArrayIndexOutOfBoundsException  Some or all of the provided matrix indexes are out of bounds.
     */
    public void setSubMatrix(int[] rowIndexes, int initialColumnIndex, int finalColumnIndex, Matrix matrix) {
        try {
            for (int i = 0; i < rowIndexes.length; i++) {
                for (int j = initialColumnIndex; j <= finalColumnIndex; j++) {
                    array[rowIndexes[i]][j] = matrix.getElement(i, j - initialColumnIndex);
                }
            }
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException(
                    "Some or all of the provided matrix indexes are out of bounds."
            );
        }
    }

    /**
     * Sets a sub-matrix of this matrix to the provided matrix values.
     *
     * @param   initialRowIndex The initial row index.
     * @param   finalRowIndex   The final row index.
     * @param   columnIndexes   The column indexes of the columns of this matrix to be changed to values of the columns
     *                          of the provided sub-matrix.
     * @param   matrix          The matrix to whose values we set the values of the specified sub-matrix of this matrix.
     *
     * @exception   ArrayIndexOutOfBoundsException  Some or all of the provided matrix indexes are out of bounds.
     */
    public void setSubMatrix(int initialRowIndex, int finalRowIndex, int[] columnIndexes, Matrix matrix) {
        try {
            for (int i = initialRowIndex; i <= finalRowIndex; i++) {
                for (int j = 0; j < columnIndexes.length; j++) {
                    array[i][columnIndexes[j]] = matrix.getElement(i - initialRowIndex, j);
                }
            }
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException(
                    "Some or all of the provided matrix indexes are out of bounds."
            );
        }
    }
    //endregion

    /**
     * Returns the transpose of this matrix.
     *
     * @return  The transpose of this matrix.
     */
    public Matrix transpose() {
        Matrix resultMatrix = new Matrix(columnDimension, rowDimension);
        double[][] resultMatrixArray = resultMatrix.getArray();
        for (int i = 0; i < rowDimension; i++) {
            for (int j = 0; j < columnDimension; j++) {
                resultMatrixArray[j][i] = array[i][j];
            }
        }
        return resultMatrix;
    }

    /**
     * Computes the trace of this matrix. The trace of a matrix is equal to the sum of its diagonal elements.
     *
     * @return  The trace of this matrix.
     */
    public double trace() {
        double trace = 0;
        for (int i = 0; i < Math.min(rowDimension, columnDimension); i++) {
            trace += array[i][i];
        }
        return trace;
    }

    //region Norm Computations
    /**
     * Computes the \(L_1\) norm of this matrix. Denoting this matrix by \(A\in\mathbb{R}^{m\times n}\), its element at
     * row \(i\) and column \(j\) by \(A_{ij}\) and its \(L_1\) norm by \(\|A\|_1\), we have that:
     * \[\|A\|_1=\max_{1\leq j\leq n}{\sum_{i=1}^m{\left|A_{ij}\right|}},\]
     * which is the maximum absolute column sum of the matrix.
     *
     * @return  The \(L_1\) norm of this matrix.
     */
    public double computeL1Norm() {
        double l1Norm = 0;
        for (int j = 0; j < columnDimension; j++) {
            double columnSum = 0;
            for (int i = 0; i < rowDimension; i++) {
                columnSum += Math.abs(array[i][j]);
            }
            l1Norm = Math.max(l1Norm, columnSum);
        }
        return l1Norm;
    }

    /**
     * Computes the \(L_2\) norm of this matrix. The \(L_2\) norm of a matrix is equal to its largest singular value.
     * For square matrices, the \(L_2\) norm is also known as the spectral norm.
     *
     * @return  The \(L_2\) norm of this matrix.
     */
    public double computeL2Norm() {
        throw new NotImplementedException(); // TODO: Implement the SVD and then this method.
    }

    /**
     * Computes the \(L_\infty\) norm of this matrix. Denoting this matrix by \(A\in\mathbb{R}^{m\times n}\), its
     * element at row \(i\) and column \(j\) by \(A_{ij}\) and its \(L_\infty\) norm by \(\|A\|_\infty\), we have that:
     * \[\|A\|_\infty=\max_{1\leq i\leq m}{\sum_{j=1}^n{\left|A_{ij}\right|}},\]
     * which is the maximum absolute row sum of the matrix.
     *
     * @return  The \(L_\infty\) norm of this matrix.
     */
    public double computeLInfinityNorm() {
        double lInfinityNorm = 0;
        for (int i = 0; i < rowDimension; i++) {
            double rowSum = 0;
            for (int j = 0; j < columnDimension; j++) {
                rowSum += Math.abs(array[i][j]);
            }
            lInfinityNorm = Math.max(lInfinityNorm, rowSum);
        }
        return lInfinityNorm;
    }

    /**
     * Computes the Frobenius norm of this matrix. Denoting this matrix by \(A\in\mathbb{R}^{m\times n}\), its element
     * at row \(i\) and column \(j\) by \(A_{ij}\) and its Frobenius norm by \(\|A\|_F\), we have that:
     * \[\|A\|_F=\sqrt{\sum_{i=1}^m{\sum_{j=1}^n{A_{ij}^2}}}.\]
     *
     * @return  The Frobenius norm of this matrix.
     */
    public double computeFrobeniusNorm() {
        double frobeniusNorm = 0;
        for (int i = 0; i < rowDimension; i++) {
            for (int j = 0; j < columnDimension; j++) {
                frobeniusNorm = Utilities.computeSquareRootOfSumOfSquares(frobeniusNorm, array[i][j]);
            }
        }
        return frobeniusNorm;
    }
    //endregion

    //region Element-wise Operations
    /**
     * Adds another matrix to the current matrix and returns the result in a new matrix.
     *
     * @param   matrix  The matrix to add to the current matrix.
     * @return          A new matrix holding the result of the addition.
     */
    public Matrix add(Matrix matrix) {
        checkMatrixDimensions(matrix);
        Matrix resultMatrix = new Matrix(rowDimension, columnDimension);
        double[][] resultMatrixArray = resultMatrix.getArray();
        for (int i = 0; i < rowDimension; i++) {
            for (int j = 0; j < columnDimension; j++) {
                resultMatrixArray[i][j] = array[i][j] + matrix.array[i][j];
            }
        }
        return resultMatrix;
    }

    /**
     * Adds another matrix to the current matrix and replaces the current matrix with the result.
     *
     * @param   matrix  The matrix to add to the current matrix.
     */
    public void addEquals(Matrix matrix) {
        checkMatrixDimensions(matrix);
        for (int i = 0; i < rowDimension; i++) {
            for (int j = 0; j < columnDimension; j++) {
                array[i][j] += matrix.array[i][j];
            }
        }
    }

    /**
     * Subtracts another matrix from the current matrix and returns the result in a new matrix.
     *
     * @param   matrix  The matrix to subtract from the current matrix.
     * @return          A new matrix holding the result of the subtraction.
     */
    public Matrix subtract(Matrix matrix) {
        checkMatrixDimensions(matrix);
        Matrix resultMatrix = new Matrix(rowDimension, columnDimension);
        double[][] resultMatrixArray = resultMatrix.getArray();
        for (int i = 0; i < rowDimension; i++) {
            for (int j = 0; j < columnDimension; j++) {
                resultMatrixArray[i][j] = array[i][j] - matrix.array[i][j];
            }
        }
        return resultMatrix;
    }

    /**
     * Subtracts another matrix from the current matrix and replaces the current matrix with the result.
     *
     * @param   matrix  The matrix to subtract from the current matrix.
     */
    public void subtractEquals(Matrix matrix) {
        checkMatrixDimensions(matrix);
        for (int i = 0; i < rowDimension; i++) {
            for (int j = 0; j < columnDimension; j++) {
                array[i][j] -= matrix.array[i][j];
            }
        }
    }

    /**
     * Multiplies the current matrix with another matrix element-wise and returns the result in a new matrix.
     *
     * @param   matrix  The matrix to multiply with the current matrix element-wise.
     * @return          A new matrix holding the result of the multiplication.
     */
    public Matrix multiplyElementwise(Matrix matrix) {
        checkMatrixDimensions(matrix);
        Matrix resultMatrix = new Matrix(rowDimension, columnDimension);
        double[][] resultMatrixArray = resultMatrix.getArray();
        for (int i = 0; i < rowDimension; i++) {
            for (int j = 0; j < columnDimension; j++) {
                resultMatrixArray[i][j] = array[i][j] * matrix.array[i][j];
            }
        }
        return resultMatrix;
    }

    /**
     * Multiplies the current matrix with another matrix element-wise and replaces the current matrix with the result.
     *
     * @param   matrix  The matrix to multiply with the current matrix element-wise.
     */
    public void multiplyElementwiseEquals(Matrix matrix) {
        checkMatrixDimensions(matrix);
        for (int i = 0; i < rowDimension; i++) {
            for (int j = 0; j < columnDimension; j++) {
                array[i][j] *= matrix.array[i][j];
            }
        }
    }

    /**
     * Right-divides the current matrix with another matrix element-wise and returns the result in a new matrix.
     *
     * @param   matrix  The matrix to right-divide with the current matrix element-wise.
     * @return          A new matrix holding the result of the division.
     */
    public Matrix rightDivideElementwise(Matrix matrix) {
        checkMatrixDimensions(matrix);
        Matrix resultMatrix = new Matrix(rowDimension, columnDimension);
        double[][] resultMatrixArray = resultMatrix.getArray();
        for (int i = 0; i < rowDimension; i++) {
            for (int j = 0; j < columnDimension; j++) {
                resultMatrixArray[i][j] = array[i][j] / matrix.array[i][j];
            }
        }
        return resultMatrix;
    }

    /**
     * Right-divides the current matrix with another matrix element-wise and replaces the current matrix with the result.
     *
     * @param   matrix  The matrix to right-divide with the current matrix element-wise.
     */
    public void rightDivideElementwiseEquals(Matrix matrix) {
        checkMatrixDimensions(matrix);
        for (int i = 0; i < rowDimension; i++) {
            for (int j = 0; j < columnDimension; j++) {
                array[i][j] /= matrix.array[i][j];
            }
        }
    }

    /**
     * Left-divides the current matrix with another matrix element-wise and returns the result in a new matrix.
     *
     * @param   matrix  The matrix to left-divide with the current matrix element-wise.
     * @return          A new matrix holding the result of the division.
     */
    public Matrix leftDivideElementwise(Matrix matrix) {
        checkMatrixDimensions(matrix);
        Matrix resultMatrix = new Matrix(rowDimension, columnDimension);
        double[][] resultMatrixArray = resultMatrix.getArray();
        for (int i = 0; i < rowDimension; i++) {
            for (int j = 0; j < columnDimension; j++) {
                resultMatrixArray[i][j] = matrix.array[i][j] / array[i][j];
            }
        }
        return resultMatrix;
    }

    /**
     * Left-divides the current matrix with another matrix element-wise and replaces the current matrix with the result.
     *
     * @param   matrix  The matrix to left-divide with the current matrix element-wise.
     */
    public void leftDivideElementwiseEquals(Matrix matrix) {
        checkMatrixDimensions(matrix);
        for (int i = 0; i < rowDimension; i++) {
            for (int j = 0; j < columnDimension; j++) {
                array[i][j] = matrix.array[i][j] / array[i][j];
            }
        }
    }
    //endregion

    /**
     * Multiplies the current matrix with a scalar and returns the result in a new matrix.
     *
     * @param   scalar  The scalar with which to multiply the current matrix.
     * @return          A new matrix holding the result of the multiplication.
     */
    public Matrix multiply(double scalar) {
        Matrix resultMatrix = new Matrix(rowDimension, columnDimension);
        double[][] resultMatrixArray = resultMatrix.getArray();
        for (int i = 0; i < rowDimension; i++) {
            for (int j = 0; j < columnDimension; j++) {
                resultMatrixArray[i][j] = array[i][j] * scalar;
            }
        }
        return resultMatrix;
    }

    /**
     * Multiplies the current matrix with a scalar and replaces the current matrix with the result.
     *
     * @param   scalar  The scalar with which to multiply the current matrix.
     */
    public void multiplyEquals(double scalar) {
        for (int i = 0; i < rowDimension; i++) {
            for (int j = 0; j < columnDimension; j++) {
                array[i][j] *= scalar;
            }
        }
    }

    /**
     * Divides the current matrix with a scalar and returns the result in a new matrix.
     *
     * @param   scalar  The scalar with which to divide the current matrix.
     * @return          A new matrix holding the result of the division.
     */
    public Matrix divide(double scalar) {
        Matrix resultMatrix = new Matrix(rowDimension, columnDimension);
        double[][] resultMatrixArray = resultMatrix.getArray();
        for (int i = 0; i < rowDimension; i++) {
            for (int j = 0; j < columnDimension; j++) {
                resultMatrixArray[i][j] = array[i][j] / scalar;
            }
        }
        return resultMatrix;
    }

    /**
     * Divides the current matrix with a scalar and replaces the current matrix with the result.
     *
     * @param   scalar  The scalar with which to divide the current matrix.
     */
    public void divideEquals(double scalar) {
        for (int i = 0; i < rowDimension; i++) {
            for (int j = 0; j < columnDimension; j++) {
                array[i][j] /= scalar;
            }
        }
    }

    /**
     * Multiplies the current matrix with a vector and returns the result in a new vector.
     *
     * @param   vector  The vector with which to multiply the current matrix.
     * @return          A new vector holding the result of the multiplication.
     */
    public Vector multiply(Vector vector) {
        if (vector.getDimension() != columnDimension) {
            throw new IllegalArgumentException(
                    "The column dimension of the matrix must agree with the dimension of the vector."
            );
        }
        Vector resultVector = new Vector(rowDimension);
        double[] resultVectorArray = resultVector.getArray();
        for (int i = 0; i < rowDimension; i++) {
            for (int j = 0; j < columnDimension; j++) {
                resultVectorArray[i] += array[i][j] * vector.getElement(j);
            }
        }
        return resultVector;
    }

    /**
     * Multiplies the current matrix with another matrix and returns the result in a new matrix.
     *
     * @param   matrix  The matrix with which to multiply the current matrix.
     * @return          A new matrix holding the result of the multiplication.
     *
     * @exception   IllegalArgumentException    The inner dimensions of the matrices must agree.
     */
    public Matrix multiply(Matrix matrix) {
        if (matrix.rowDimension != columnDimension) {
            throw new IllegalArgumentException("The inner dimensions of the matrices must agree.");
        }
        Matrix resultMatrix = new Matrix(rowDimension, matrix.columnDimension);
        double[][] resultMatrixArray = resultMatrix.getArray();
        double[] matrixColumnJ = new double[columnDimension];
        for (int j = 0; j < matrix.columnDimension; j++) {
            for (int k = 0; k < columnDimension; k++) {
                matrixColumnJ[k] = matrix.array[k][j];
            }
            for (int i = 0; i < rowDimension; i++) {
                double[] currentMatrixRowI = array[i];
                double dotProduct = 0; // TODO: Use compute dot product function for vectors.
                for (int k = 0; k < columnDimension; k++) {
                    dotProduct += currentMatrixRowI[k] * matrixColumnJ[k];
                }
                resultMatrixArray[i][j] = dotProduct;
            }
        }
        return resultMatrix;
    }

    //region Special Matrix "Constructors"
    /**
     * Constructs and returns a square identity matrix with the provided dimension.
     *
     * @param   dimension   The dimension of the identity matrix (note that the row dimension and the column dimension
     *                      are equal in this case because the identity matrix is square).
     * @return              An identity matrix with the given dimension.
     */
    public static Matrix generateIdentityMatrix(int dimension) {
        Matrix identityMatrix = new Matrix(dimension, dimension);
        double[][] identityMatrixArray = identityMatrix.getArray();
        for (int i = 0; i < dimension; i++) {
            identityMatrixArray[i][i] = 1.0;
        }
        return identityMatrix;
    }

    /**
     * Constructs and returns a random matrix. The returned matrix contains random values ranging from {@code 0.0} to
     * {@code 1.0}.
     *
     * @param   rowDimension        The row dimension of the random matrix.
     * @param   columnDimension     The column dimension of the random matrix.
     * @return                      A matrix that contains random values ranging from {@code 0.0} to {@code 1.0}.
     */
    public static Matrix generateRandomMatrix(int rowDimension, int columnDimension) {
        Matrix randomMatrix = new Matrix(rowDimension, columnDimension);
        double[][] randomMatrixArray = randomMatrix.getArray();
        for (int i = 0; i < rowDimension; i++) {
            for (int j = 0; j < columnDimension; j++) {
                randomMatrixArray[i][j] = Math.random();
            }
        }
        return randomMatrix;
    }
    //endregion

    /**
     * Checks whether the provided matrix has the same dimensions as this matrix. If the dimensions of the two matrices
     * do not agree an exception is thrown.
     *
     * @param   matrix  The matrix whose dimensions to check.
     *
     * @exception   IllegalArgumentException    Matrix dimensions must agree.
     */
    private void checkMatrixDimensions(Matrix matrix) {
        if (matrix.rowDimension != rowDimension || matrix.columnDimension != columnDimension) {
            throw new IllegalArgumentException("Matrix dimensions must agree.");
        }
    }
}