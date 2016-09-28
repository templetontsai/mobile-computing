package unimelb.comp90018.metastasis;

import android.graphics.Canvas;

/**
 *  This interface defines some general methods for entities in the game.
 *  There are three objects that are present on the screen, disks belonging
 *  to players, food particles, and viruses. When a disk collides with any
 *  of these, the result will vary depending on what type of entity was
 *  involved.
 */
public interface Entity {
    /**
     *  These values are returned by the getType method, and distinguish
     *  between different objects in the game.
     */
    public enum EntityType {
        DISK, FOOD, VIRUS
    }

    /**
     *  This method returns the current position of the entity. Combined
     *  with the entity's size, this allows us to test for collisions.
     */
    public Vector getPosition ();

    /**
     *  Returns the radius of the entity, assuming that the game only has
     *  spherical objects in it.
     */
    public double getRadius ();

    /**
     *  Returns what type of object the entity is, which determines how
     *  a collision should be handled.
     */
    public EntityType getType ();

    /**
     *  This method allows the main game class to tell each visible object
     *  where it is located on the screen.
     */
    public void setScreenPosition (Vector pos);

    /**
     *  Draw the object on a canvas. Different objects (food, disks, 
     *  viruses) draw themselves differently, so that objects are visually
     *  distinct.
     */
    public void draw (Canvas canvas, double zoom);
}

// vim: ts=4 sw=4 et
