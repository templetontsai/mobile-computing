package unimelb.comp90018.metastasis;

import android.graphics.*;

import java.io.Serializable;

/**
 *  Class to encapsulate a food particle in the game world. All food
 *  objects are uniform size, so the getRadius method required by the
 *  Entity interface will always return 1. The only variable is where
 *  the food is located in the game.
 */
public class Food implements Entity, Serializable {
    private Vector position;
    private Vector screenPosition;
    //private Paint paint;
    private DiskPaint paint;

    /**
     *  Create a new food object at a specified location in the game
     *  world. Locations should be chosen at random, in order for food to
     *  be evenly scattered throughout the world.
     */
    public Food (Vector pos) {
        this.position = pos;

        // choose a color and set up a paint object now. This is more
        // efficient than reconstructing a paint object on every draw.
        paint = new DiskPaint ();
        paint.setColor (Utils.randomColour ());
    }

    @Override
    public Vector getPosition () {
        return position;
    }

    /**
     *  Food particles are always the same size, so no need for a member
     *  variable for this. This method simply always returns 1.
     */
    @Override
    public double getRadius () {
        return 1.0;
    }

    /**
     *  Draw a food particle on the screen.
     */
    @Override
    public void draw (Canvas canvas, double zoom) {
        canvas.drawCircle ((float) screenPosition.x, 
                (float) screenPosition.y, (float) zoom, paint);
    }

    @Override
    public void setScreenPosition (Vector pos) {
        screenPosition = pos;
    }

    @Override
    public EntityType getType () {
        return EntityType.FOOD;
    }
}

// vim: ts=4 sw=4 et
