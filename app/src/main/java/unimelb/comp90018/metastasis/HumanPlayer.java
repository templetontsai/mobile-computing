package unimelb.comp90018.metastasis;

import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


import java.util.List;

import unimelb.comp90018.metastasis.ui.GamePanelFragment;

/**
 *  Player class for the human who is using the device that the game is
 *  running on. This is different from AutomatedPlayer in that this class
 *  needs to be responsive to user input via the touch screen or gyroscope
 *  sensors; in contrast to the AI which makes it's decisions based on the
 *  position of other game objects.
 */
public class HumanPlayer extends Player implements OnTouchListener {
    private static final String TAG = "HumanPlayer";

    private Vector direction;
    private GestureDetector gestureDetector = null;
    private boolean isSplited = false;

    /**
     *  Create a new player to respond to user input. Note that there
     *  should only be one human player on any device.
     */
    public HumanPlayer (GamePanelFragment activity, String name, Bitmap image) {
        super (activity, name, image);
        direction = new Vector ();
        gestureDetector = new GestureDetector(activity.getActivity().getApplicationContext(), new GestureListener());
    }

    /**
     *  This method is called when the user touches the screen. In
     *  response to this event, we will update our disks to move towards
     *  where the screen was touched.
     */
    @Override
    public boolean onTouch (View v, MotionEvent data) {

        switch(data.getAction()){
            case MotionEvent.ACTION_MOVE:
                Vector touchPosition = new Vector ((double) data.getX (),
                        (double) data.getY ());
                Vector screenPosition = game.getScreenPosition ();

                Log.d (TAG, "Got touch input.");
                Log.d (TAG, "Screen top left " + screenPosition);

                // calculate the coordinates of the touch event in terms of the
                // game world coordinate system.
                touchPosition.addComponents (screenPosition);

                // calculate the vector from our position to the position of the
                // touch event, done by subtracting our position vector from the
                // touch position vector.
                direction = getPosition ();
                direction.scale (-1);
                direction.addComponents (touchPosition);

                return true;
            default:
                return gestureDetector.onTouchEvent(data);


        }

    }

    public void onAccSensorChanged (double xPos, double yPos) {

        Vector accPosition = new Vector (xPos, yPos);
        Vector screenPosition = game.getScreenPosition ();

        Log.d (TAG, "Got Acc sensor input.");
        Log.d (TAG, "Screen top left " + screenPosition);

        // calculate the coordinates of the acc event in terms of the
        // game world coordinate system.
        accPosition.addComponents (screenPosition);

        // calculate the vector from our position to the position of the
        // acc event, done by subtracting our position vector from the
        // acc position vector.
        direction = getPosition ();
        direction.scale (-1);
        direction.addComponents (accPosition);


    }



    class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        /**
         *  This method is invoked when the user double taps on a part of the
         *  screen. We will split our disk (or disks) and send the fragment in
         *  the direction of the double tap event from where our disk is now.
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if(!isSplited) {
                isSplited = true;
                if(disks.size()>0)
                  disks.get(0).setSplit(true);

                split();
            }

            return true;
        }

        public void split() {

            CountDownTimer splitTimer = new CountDownTimer(5000, 1000) {

                public void onTick(long millisUntilFinished) {
                    //do nothing
                }

                public void onFinish() {
                   isSplited = false;
                    if(disks.size()>0)
                        disks.get(0).setSplit(false);
                }
            };
            splitTimer.start();


      }


    }


    /**
     *  This method moves the disks one step, in the direction and speed
     *  determined by user input received via the onTouch callback.
     */
    @Override
    protected void moveDisks (List <Entity> gameObjects) {
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

    public Vector getCurrentDirection(){
        return direction;
    }

}

// vim: ts=4 sw=4 et
