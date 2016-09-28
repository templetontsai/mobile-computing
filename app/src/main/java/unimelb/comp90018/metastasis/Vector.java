package unimelb.comp90018.metastasis;

import android.util.Log;

import java.io.Serializable;
import java.lang.Math;

public class Vector implements Serializable{
    public double x, y;
    private static final String TAG = "Vector";

    /**
     *  Create a new vector with given starting coordinates.
     */
    public Vector (double xInitial, double yInitial) {
        x = xInitial;
        y = yInitial;
    }

    /**
     *  Create a new vector, initialising x and y coordinates to 0.
     */
    public Vector () {
        x = 0;
        y = 0;
    }

    /**
     *  Create a copy of another vector.
     */
    public Vector (Vector clone) {
        this.x = clone.x;
        this.y = clone.y;
    }

    /**
     *  Generates a random vector with components between 0 and the
     *  corresponding component of the vector given as the parameter to
     *  this method.
     */
    public static Vector random (Vector maximum) {
        Vector generated = new Vector ();

        generated.x = Math.random () * maximum.x;
        generated.y = Math.random () * maximum.y;

        return generated;
    }

    /**
     *  Calculate the shortest distance between two points in a world with
     *  periodic boundary conditions. This means that the left edge of the
     *  world is stitched to the right side, and the top is stitched to
     *  the bottom. Euclidean distance is not always the shortest path in
     *  this situation.
     */
    public static Vector torusDistance (Vector from, Vector to, 
            Vector dimensions) {
        Vector distance = new Vector ();
        double sign;

        distance.addComponents (from);
        distance.scale (-1);
        distance.addComponents (to);

        // if the distance in x is greater than half the width of the world,
        // then teleporting will provide a shorter path.
        if (Math.abs (distance.x) > dimensions.x / 2) {
            sign = -1 * Math.signum (distance.x);
            distance.x = dimensions.x - Math.abs (distance.x);
            distance.x *= sign;
        }

        if (Math.abs (distance.y) > dimensions.y / 2) {
            sign = -1 * Math.signum (distance.y);
            distance.y = dimensions.y - Math.abs (distance.y);
            distance.y *= sign;
        }

        return distance;
    }

    /**
     *  Return true if two vectors are near to equal. Since floating point
     *  numbers can accumulate error from arithmetic operations, we will
     *  permit a small ammount of variation defined by the EPSILON const
     *  in Utils class.
     */
    public static boolean equal (Vector p, Vector q) {
        if (Math.abs (p.x - q.x) > Utils.EPSILON)
            return false;

        if (Math.abs (p.y - q.y) > Utils.EPSILON)
            return false;

        return true;
    }

    /**
     *  Reset the x and y coordinates to the values stored in the vector
     *  instance given as an argument. This can be useful to avoid repeated
     *  object instantiation.
     */
    public void reset (Vector v) {
        this.x = v.x;
        this.y = v.y;
    }

    /**
     *  Add the coordinates of a second vector, given as a parameter to
     *  this method, to the current vector.
     */
    public void addComponents (Vector v) {
        x += v.x;
        y += v.y;
    }

    /**
     *  Add two coordinates and make sure that the result is inside the
     *  specified dimensions. If the result would go outside, periodic
     *  boundary conditions are used.
     */
    public void addVector (Vector v, Vector dimensions) {
        x = (x + dimensions.x + v.x) % dimensions.x;
        y = (y + dimensions.y + v.y) % dimensions.y;
        //Log.d(TAG, "addVector: " + dimensions.x + dimensions.y);
    }

    /**
     *  Multiply a vector by a constant factor.
     */
    public void scale (double factor) {
        x *= factor;
        y *= factor;
    }

    /**
     *  Returns the length of this vector.
     */
    public double abs () {
        return Math.sqrt (x * x + y * y);
    }

    /**
     *  Method for printing out a vector, useful for debug output.
     */
    public String toString () {
        return "x: " + x + "; y: " + y;
    }
}

// vim: ts=4 sw=4 et
