package org.platanios.learn.math.matrix;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Emmanouil Antonios Platanios
 */
public class DenseVectorTest {
    @Test
    public void testEquals() {
        DenseVector vector1 = new DenseVector(new double[] { 0.54593, 0.32234, 0.94671, 0.05686, 0.33245, 0.45634 });
        DenseVector vector2 = new DenseVector(new double[] { 0.54593, 0.32234, 0.94671, 0.05686, 0.33245, 0.45634 });
        DenseVector vector3 = new DenseVector(new double[] { 0.54593, 0.32234, 0.94671, 0.05686, 0.34245, 0.45634 });
        DenseVector vector4 = new DenseVector(new double[] { 0.54593, 0.32234, 0.94671, 0.05686, 0.33245 });
        Assert.assertTrue(vector1.equals(vector2));
        Assert.assertTrue(!vector1.equals(vector3));
        Assert.assertTrue(!vector1.equals(vector4));
        Assert.assertTrue(!vector2.equals(vector3));
        Assert.assertTrue(!vector2.equals(vector4));
        Assert.assertTrue(!vector3.equals(vector4));
    }

    @Test
    public void testSerialization() {
        int vectorSize = 1000;
        try {
            // Test for when we do not store the vector type
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4 + (vectorSize << 3));
            DenseVector vector = DenseVector.generateRandomVector(vectorSize);
            DenseVector expectedVector = vector.copy();
            vector.write(outputStream, false);
            outputStream.close();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            DenseVector actualVector = DenseVector.read(inputStream, false);
            inputStream.close();
            Assert.assertTrue(expectedVector.equals(actualVector));
            // Test for when we store the vector type
            outputStream = new ByteArrayOutputStream(4 + (vectorSize << 3));
            vector = DenseVector.generateRandomVector(vectorSize);
            expectedVector = vector.copy();
            vector.write(outputStream, true);
            outputStream.close();
            inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            actualVector = DenseVector.read(inputStream, true);
            inputStream.close();
            Assert.assertTrue(expectedVector.equals(actualVector));
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testEncoder() {
        int vectorSize = 1000;
        try {
            // Test for when we do not store the vector type
            DenseVector vector = DenseVector.generateRandomVector(vectorSize);
            DenseVector expectedVector = vector.copy();
            InputStream inputStream = vector.getEncoder(false);
            DenseVector actualVector = DenseVector.read(inputStream, false);
            inputStream.close();
            Assert.assertTrue(expectedVector.equals(actualVector));
            // Test for when we store the vector type
            vector = DenseVector.generateRandomVector(vectorSize);
            expectedVector = vector.copy();
            inputStream = vector.getEncoder(true);
            actualVector = DenseVector.read(inputStream, true);
            inputStream.close();
            Assert.assertTrue(expectedVector.equals(actualVector));
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }
}