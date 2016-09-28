package unimelb.comp90018.metastasis.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import unimelb.comp90018.metastasis.Vector;

/**
 *  Unit test cases on the Vector class.
 */
public class VectorTest {
    private static final int DISTANCE_TEST_ITERATIONS = 10;

    // for testing our handling of periodic boundary conditions, we will
    // use a basic world of 100 by 100.
    private Vector dimensions;

    @Before
    public void initialiseDimensions () {
        dimensions = new Vector (100, 100);
    }

    /**
     *  Test the torusDistance method. We expect torusDistance to return
     *  a vector that when added to the first vector, will produce the
     *  second vector.
     */
    @Test
    public void torusDistanceAddsUp () {
        Vector start, end, distance;

        // try a number of random combinations of start and end vectors
        for (int i = 0; i < DISTANCE_TEST_ITERATIONS; i ++) {
            start = Vector.random (dimensions);
            end = Vector.random (dimensions);

            distance = Vector.torusDistance (start, end, dimensions);

            // if we add distance to start, it should give us end.
            start.addVector (distance, dimensions);

            assertTrue ("Got " + start + ", Expected " + end, 
                    Vector.equal (start, end));
        }
    }
}

// vim: ts=4 sw=4 et
