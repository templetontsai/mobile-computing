package unimelb.comp90018.metastasis;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.io.Serializable;

/**
 *  Class to represent a disk in the game. Each player can have multiple
 *  disks, due to splitting or being popped by a virus.
 */
public class Disk implements Entity, Serializable {
    private static final String TAG = "Disk";
    private static final double DRAG_COEFFICIENT = 0.005;

    // width of the border around disks, in pixels.
    public static final double DISK_BORDER_WIDTH = 5;

    private Player owner;
    private Vector worldSize;
    private Vector screenPosition;
    private Vector position;
    private Vector velocity;
    private Vector acceleration;
    private Vector force;
    private Paint outerPaint, innerPaint;
    private double area;
    private boolean useTexture = false;
    Bitmap image;
    private boolean isAlive, isShielded, isVisible, isSplit;

    public Disk (Player owner, Vector position, Vector worldSize) {
        this.isAlive = true;
        this.isShielded = false;
        this.isVisible = true;
        this.owner = owner;
        this.worldSize = worldSize;
        this.position = new Vector (position);
        this.velocity = new Vector ();
        this.area = Utils.INITIAL_DISK_SIZE;
        this.isSplit = false;

        Log.d(TAG, "Creating new Disk at position " +
                position.toString());

        outerPaint = new Paint ();
        innerPaint = new Paint ();

        // set Disk to have a random colour, and random border colour.
        outerPaint.setColor (Utils.randomColour ());
        innerPaint.setColor (Utils.randomColour ());

    }

    /**
     * Second constructor if the player is using a texture
     *
     */
    public Disk (Player owner, Vector position, Vector worldSize, Bitmap image) {
        this.isAlive = true;
        this.isShielded = false;
        this.isVisible = true;
        this.owner = owner;
        this.worldSize = worldSize;
        this.position = new Vector (position);
        this.velocity = new Vector ();
        this.area = Utils.INITIAL_DISK_SIZE;

        Log.d (TAG, "Creating new Disk at position " +
                position.toString ());

        outerPaint = new Paint ();
        innerPaint = new Paint ();

        // set Disk to have a random colour, and random border colour.
        outerPaint.setColor(Utils.randomColour());
        innerPaint.setColor (Utils.randomColour ());
        useTexture = true;
        this.image = image;
        this.isSplit = false;

    }

    /**
     * This is called when the human player disk is popped and ends the game
     */
    public void destroyDisk(){
        isAlive = false;
    }
    /**
     *  This method is called when another, larger disk eats this one.
     */
    public void eaten () {
        owner.diskEaten (this);
    }

    /**
     *  Called when this disk collides with food or another smaller disk.
     *  We absorb the other object, and grow bigger. Note that the Player
     *  class is responsible for updating the score appropriately.
     */
    public void grow (double ammount) {
        area += ammount;
    }

	/**
	 *   Called when this disk collides with virus and is shrunk to it's
 	 *   original size at the start of the game.
	 */
    public void shrink(double amount) {
		area -= amount;
	}

    /**
     *  Apply a force on the disk to move it in a particular direction.
     */
    public void applyForce (Vector appliedForce) {
        force = appliedForce;
    }

    /**
     *  Moves the disk by one time step. Note that velocity is assumed to
     *  be defined in units of pixels per time step.
     */
    public void moveOneStep (Vector dimensions) {
        Vector netForce = new Vector (force);
        netForce.addComponents(calculateDragForce());

        // calculate acceleration from force.
        acceleration = netForce;
        acceleration.scale(1/(area/2));

        // update velocity.
        velocity.reset(acceleration);


        // and update position from velocity.
        if(!Double.isInfinite(velocity.x) && !Double.isInfinite(velocity.x)) {
            position.addVector(velocity, dimensions);
            velocity.reset(new Vector(0,0));
        }
        Log.d(TAG, owner.getName()+" position: "+position.toString());


    }

    /**
     *  Moves the disk by one time step. Note that velocity is calculated by the accelerometer
     *  and assumed to be defined in units of pixels per time step.
     */
    public void moveOneStepAcc (Vector dimensions) {
        Vector netForce = new Vector (force);
        netForce.addComponents (calculateDragForce ());


        // calculate acceleration from force.
        acceleration = netForce;
        acceleration.scale (1 / area);

        // update velocity.
        velocity.addComponents(acceleration);

        // and update position from velocity.
        position.addVector(velocity, dimensions);
    }

    /**
     *  Calculate drag force. This is a force in the opposite direction to
     *  the velocity, proportional to the magnitude of the velocity, and
     *  imposes a maximum speed (or terminal velocity) on disks, which
     *  becomes slower as disks get bigger.
     */
    public Vector calculateDragForce () {
        Vector dragForce = new Vector (velocity);
        dragForce.scale(-1 * DRAG_COEFFICIENT * area * velocity.abs());
        return dragForce;
    }

    /**
     *  Disks are drawn with a border, achieved by drawing a second circle
     *  over the top of a slightly larger circle.
     */
    @Override
    public void draw (Canvas canvas, double zoom) {
        // calculate radius from area.
        double radius = Math.sqrt (area / Math.PI);

        // calculate the radius of the two circles to be drawn.
        double outerRadius = radius * zoom;
        double innerRadius = outerRadius - DISK_BORDER_WIDTH;

        if(owner instanceof HumanPlayer) {

            if (isSplit) {


                if (useTexture && image != null) {
                    drawTexture(canvas, innerRadius);
                } else {
                    Log.d(TAG, "No texture being used");
                    canvas.drawCircle((float) screenPosition.x + 10,
                            (float) screenPosition.y, (float) innerRadius,
                            innerPaint);

                    canvas.drawCircle((float) screenPosition.x - 10,
                            (float) screenPosition.y, (float) innerRadius,
                            innerPaint);
                }
            } else {
                canvas.drawCircle((float) screenPosition.x,
                        (float) screenPosition.y, (float) outerRadius,
                        outerPaint);
                if (useTexture && image != null) {
                    drawTexture(canvas, innerRadius);
                } else {
                    Log.d(TAG, "No texture being used");
                    canvas.drawCircle((float) screenPosition.x,
                            (float) screenPosition.y, (float) innerRadius,
                            innerPaint);
                }
            }
        }



    }

    public void drawTexture(Canvas canvas, double innerRadius){
        int diameter = (int)(2*innerRadius);
        if(isSplit){
            Bitmap texture = Bitmap.createScaledBitmap(image, diameter, diameter, true);
            canvas.drawBitmap(texture, (float) (screenPosition.x + innerRadius + 10), (float) (screenPosition.y-innerRadius), null);
            canvas.drawBitmap(texture, (float) (screenPosition.x - innerRadius - 10), (float) (screenPosition.y-innerRadius), null);

        } else {
            if(diameter > 0){
                Bitmap texture = Bitmap.createScaledBitmap(image, diameter, diameter, true);
                canvas.drawBitmap(texture, (float) (screenPosition.x-innerRadius), (float) (screenPosition.y-innerRadius), null);
            }
        }


    }

    /**
     *This is to turn off and on the shield of the player
     */
    public void setIsShielded(boolean state){
        this.isShielded = state;
    }

    /**
     * This is to turn off and on a player's visibility
     */
    public void setIsVisible(boolean state){
        this.isVisible = state;
    }

    /**
     * Check if the disk is shielded
     */
    public boolean getIsShielded(){
        return isShielded;
    }

    public boolean getIsVisible(){
        return isVisible;
    }

    public void setPosition (Vector position){this.position=position;}
    @Override
    public Vector getPosition () {
        return position;
    }

    public Vector getVelocity () {
        return velocity;
    }

    public void setVelocity (Vector newVelocity) {
        this.velocity = newVelocity;
    }

    @Override
    public double getRadius () {
        return Math.sqrt (area / Math.PI);
    }

    public void setRadius (double radius) {
        area = Math.PI * radius * radius;
    }

    @Override
    public EntityType getType () {
        return EntityType.DISK;
    }

    @Override
    public void setScreenPosition (Vector pos) {
        screenPosition = pos;
    }

    public Player getOwner(){
        return owner;
    }

    public void setIsAlive(boolean state){this.isAlive = state;}

    public boolean getIsAlive(){return isAlive;}

    public void setSplit(boolean isSplit){this.isSplit = isSplit;}
}

// vim: ts=4 sw=4 et
