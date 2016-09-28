package unimelb.comp90018.metastasis;

import java.util.List;

import unimelb.comp90018.metastasis.ui.GamePanelFragment;

/**
 * This class is responsible for another human player in the world that doesn't
 * respond to touch events
 */
public class OtherPlayer extends Player{
    private static final String TAG = "HumanPlayer";
    private Vector direction;
    private Vector ourPosition;

    public OtherPlayer (GamePanelFragment activity, String name, double x, double y) {
        super (activity, name, x, y);
        this.direction = new Vector ();
    }

    /**
     *  This method moves the disks one step, in the direction and speed
     *  determined by user input received via the onTouch callback.
     */
    @Override
    protected void moveDisks (List<Entity> gameObjects) {
        ourPosition = getPosition ();

        for (Disk disk : disks) {
            disk.applyForce (direction);
            disk.moveOneStep (game.getWorldSize ());
        }
    }


    /**
     *  Returns the sum of the radii of our disks. This is used so that
     *  our disks will move faster when the user touches somewhere further
     *  away.
     */
    private double sumDiskRadii () {
        double sum = 0;

        for (Disk disk : disks) {
            sum += disk.getRadius ();
        }

        return sum;
    }

    public void setDirection(Vector direction){
        this.direction = direction;
    }
}
