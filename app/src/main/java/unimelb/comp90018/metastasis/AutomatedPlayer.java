package unimelb.comp90018.metastasis;

import java.util.List;

import unimelb.comp90018.metastasis.ui.GamePanelFragment;

/**
 *  Class for an AI player. On each update step, as well as checking for
 *  collisions between our disks and other objects, we will try to move
 *  our disks towards food and away from bigger disks. There is no point
 *  in chasing smaller disks, though, since they can run away faster than
 *  we can run after them. Smaller disks move quicker than bigger ones.
 */
public class AutomatedPlayer extends Player {
    private Vector direction;
    private Vector ourPosition;

    /**
     *  Create a new AI player. This method will create a vector object
     *  for our velocity, and call the constructor for the base class.
     */
    public AutomatedPlayer (GamePanelFragment activity, String name) {
        super (activity, name);
        this.direction = new Vector ();
    }

    @Override
    protected void moveDisks (List <Entity> objects) {
        ourPosition = getPosition ();
        calculateVelocity (objects);

        // advance our disks by one step.
        for (Disk disk : disks) {
            disk.applyForce (direction);
            disk.moveOneStep (game.getWorldSize ());
        }
    }

    /**
     *  Choose a direction in which to move our disks based on where food
     *  and other disks are located. We want to move our disks towards
     *  food, and away from other disks that might eat us.
     *
     *  This method will calculate a direction and store it in the 
     *  velocity member variable.
     */
    private void calculateVelocity (List <Entity> objects) {
        direction.x = 0;
        direction.y = 0;

        // step through all of the objects, and decide which ones we want
        // to move towards, and which ones we want to avoid.
        for (Entity object : objects) {
            switch (object.getType ()) {
                case FOOD:
                    moveTowards (object.getPosition ());
                    break;

                case DISK:
                    // if they are bigger than us, run away.
                    if (object.getRadius () > ourSmallestDisk ()) {
                        moveAwayFrom (object.getPosition ());
                    }else{
                        moveTowards (object.getPosition ());
                    }

                    break;

                case VIRUS:
                    // if we are big enough to be popped by a virus,
                    // avoid viruses.
                    if (ourSmallestDisk () > Utils.VIRUS_POP_THRESHOLD) {
                        moveAwayFrom (object.getPosition ());
                    }

                    break;
            }
        }
    }

    /**
     *  Update the velocity to point more towards a desirable thing.
     */
    private void moveTowards (Vector pos) {
        Vector increment = Vector.torusDistance (ourPosition, pos, 
                game.getWorldSize ());

        // avoid dividing by zero.
        if (increment.abs () < Utils.EPSILON)
            return;

        // food that is far away is not as useful to us, so we will give
        // it less weight.
        increment.scale (1 / increment.abs ());

        // and add to the overall velocity vector.
        direction.addVector (increment, game.getWorldSize ());
    }

    /**
     *  Update the velocity to avoid a bad thing.
     */
    private void moveAwayFrom (Vector pos) {
        Vector increment = Vector.torusDistance (ourPosition, pos, 
                game.getWorldSize ());

        if (increment.abs () < Utils.EPSILON)
            return;

        // once again, big disks that are a long way away are not as
        // threatening as a big disk that is right next to us, so we will
        // scale by the distance.
        increment.scale (-1 / increment.abs ());

        direction.addVector (increment, game.getWorldSize ());
    }

    /**
     *  Returns the size of our smallest disk.
     */
    private double ourSmallestDisk () {
        double smallest = -1, radius;

        for (Disk disk : disks) {
            radius = disk.getRadius ();

            if ((radius < smallest) || (smallest == -1))
                smallest = radius;
        }

        return smallest;
    }
}

// vim: ts=4 sw=4 et
