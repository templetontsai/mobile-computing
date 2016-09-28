package unimelb.comp90018.metastasis;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.io.Serializable;

import unimelb.comp90018.metastasis.ui.R;


/**
 *  Class to represent a virus. As with food, viruses will be of a
 *  constant size, and don't move around. This class is also responsible
 *  for drawing the virus on screen.
 */
public class Virus implements Entity, Serializable {
    private Vector position;
    private Vector screenPosition;
    private Paint paint;

    private Context context;

    // Viruses are all the same size.
    private static final double VIRUS_RADIUS = 2.0;

    private static final String TAG = "Virus";


    /**
     *  Create a new instance of a virus at given coordinates in the game
     *  world.
     */

    public Virus (Vector pos, Context context) {

        position = pos;

        // Set up the paint object for drawing the virus. For the sake of
        // simplicity, we will draw them as black cirles, since the spiky
        // ball shapes in the Real Game are more difficult to reproduce.
        paint = new Paint ();
        paint.setColor (Color.BLACK);


        this.context = context;

    }

    @Override
    public Vector getPosition () {
        return position;
    }

    @Override
    public double getRadius () {
        return VIRUS_RADIUS;
    }

    @Override
    public EntityType getType () {
        return EntityType.VIRUS;
    }

    @Override
    public void setScreenPosition (Vector pos) {
        screenPosition = pos;
    }

    @Override
    public void draw (Canvas canvas, double zoom) {
        //Set the width and height of the bitmap using the radius and the zoom factor
        double radius = 0;

        radius = VIRUS_RADIUS * zoom;
        if( !Double.isInfinite(radius)) {
            //Resize the image
            Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.mipmap.virus_96);
            Bitmap virus = Bitmap.createScaledBitmap(image, (int)radius, (int)radius, true);
            canvas.drawBitmap(virus, (float) screenPosition.x, (float) screenPosition.y, null);
        }



    }
}

// vim: ts=4 sw=4 et
