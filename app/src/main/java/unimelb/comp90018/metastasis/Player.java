package unimelb.comp90018.metastasis;


import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

import unimelb.comp90018.metastasis.ui.GamePanelFragment;


public abstract class Player {
    private static final String TAG = "Player";

    protected List <Disk> disks;
    protected GamePanelFragment game;
    private double score;
	private double disksEaten;
	private String name;
    private boolean isAlive;

    /**
     *  Generic constructor for a new player. This method sets up the
     *  list of disks, and creates a single disk for us to start with.
     */
    public Player (GamePanelFragment activity, String name) {
        this.game = activity;
        this.disks = new ArrayList <Disk> ();
        this.score = 0;
		this.disksEaten = 0;
        this.name = name;
        this.isAlive = true;

        Vector startPos = Vector.random (this.game.getWorldSize ());
        Disk disk = new Disk (this, startPos, this.game.getWorldSize ());
        disks.add(disk);
        game.addObject(disk);
    }

    /**
     *
     * Second constructor to make player disk using a texture
     */
    public Player (GamePanelFragment activity, String name, Bitmap image) {
        this.game = activity;
        this.disks = new ArrayList <Disk> ();
        this.score = 0;
        this.disksEaten = 0;
        this.name = name;
        this.isAlive = true;

        Vector startPos = Vector.random (this.game.getWorldSize ());
        Disk disk = new Disk (this, startPos, this.game.getWorldSize (), image);
        disks.add(disk);
        game.addObject(disk);
    }

    /**
     *
     * Third constructor to make player disk to a specific location
     */
    public Player (GamePanelFragment activity, String name, double x, double y) {
        this.game = activity;
        this.disks = new ArrayList <Disk> ();
        this.score = 0;
        this.disksEaten = 0;
        this.name = name;
        this.isAlive = true;

        Vector startPos = new Vector(x,y);
        Disk disk = new Disk (this, startPos, activity.getWorldSize ());
        disks.add(disk);
        game.addObject(disk);
    }

    /**
     *  This method is called when another disk has eaten one of our disks.
     *  We need to remove it from our list of disks, and tell the main game
     *  class that the disk has been eaten.
     */
    public void diskEaten (Disk disk) {
        disks.remove(disk);
        game.removeObject(disk);
        if(disks.size()==0)
            isAlive = false;

    }

    /**
     *  This method is called when the player splits their disks. If we
     *  have more than one disk, each one will be split. When a disk is
     *  split, it is divided into two new disks, each half the size of the
     *  original disk, and one disk can shoot off at a high speed, which
     *  facilitates attacks to be launched against other players.
     */
    public void split (Vector splitVelocity) {
        Vector oldVelocity, newVelocity = new Vector ();
        Vector oldPosition;
        double oldSize;

        for (Disk disk : disks) {
            oldVelocity = disk.getVelocity ();
            oldPosition = disk.getPosition ();
            oldSize = disk.getRadius ();

            // one of the disks will shoot away with speed and direction
            // determined by splitVelocity. This needs to be recalculated
            // for each disk.
            newVelocity.reset (oldVelocity);
            newVelocity.addComponents (splitVelocity);

            // remove the old disk from the game, and create two new disks
            // in its place.
            disks.remove (disk);
            game.removeObject (disk);

            // create the new disks.
            addNewDisk (oldPosition, oldSize / 2, oldVelocity);
            addNewDisk (oldPosition, oldSize / 2, splitVelocity);
        }
    }

    /**
     *  Create a new instance of a disk, and add it to both our list of
     *  disks, and the game's list of objects. This method also allows
     *  the initial position and velocity to be set, which is important
     *  for splitting an existing disk.
     */
    private void addNewDisk (Vector position, double radius, Vector velocity) {
        Disk created = new Disk (this, position, game.getWorldSize ());
        created.setVelocity (velocity);
        created.setRadius (radius);

        disks.add (created);
        game.addObject(created);
    }

    /**
     *  Get the position of the player's disks, in order to have them in
     *  the center of the screen on the device.
     */
    public Vector getPosition () {
        Vector averagePosition = new Vector (0, 0);
        double numDisks = 0;

        for (Disk disk : disks) {
            averagePosition.addComponents (disk.getPosition ());
            numDisks ++;
        }

        averagePosition.scale(1 / numDisks);

        return averagePosition;
    }

    /**
     *  Returns the sum of the radii of this player's disks.
     */
    public double sumDiskRadius () {
        double sum = 0;

        for (Disk disk : disks) {
            sum += disk.getRadius ();
        }

        return sum;
    }

    public void step (List <Entity> gameObjects) {
        // step through each of our disks and check for collision with 
        // each of the other game objects.
        for (Disk disk : disks) {
            for (Entity object : gameObjects) {
                // our own disks will also appear in the list of all game 
                // objects, so don't count collisions with ourself.
                if (object == disk)
                    continue;

                checkForCollision (disk, object);
            }
        }

        // now we need to move all of our disks, which will be different
        // for human versus AI players.
        moveDisks(gameObjects);
    }

    protected abstract void moveDisks (List <Entity> objects);

    /**
     *  Check for a collision between one of our disks and some other 
     *  object, and take appropriate action. If we have collided with
     *  food, the food is eaten; if a virus, our disk might be popped; and
     *  if another disk, we may eat them. Note that if another disk eats
     *  us, they will notify us; we don't destroy our own disks.
     */
    private void checkForCollision (Disk disk, Entity object) {
        // we will only take action if objects have collided.
        if (!hasCollided (disk, object))
            return;

        switch (object.getType ()) {
            case FOOD:
                eatFood ((Food) object, disk);
                break;

            case DISK:
                eatOtherDisk ((Disk) object, disk);
                break;

            case VIRUS:

                popDisk ((Virus) object, disk);

                break;
        }
    }

    /**
     *  Check if two objects have collided. Since all of the objects in
     *  the game are treated as circles, we are looking to see if the two
     *  circles overlap. This is done by looking at the distance between
     *  the centre of each circle, compared to the radius. If the two 
     *  radii add up to a value greater than the distance between the
     *  centers, then they must overlap.
     *
     *  @return true if the objects overlap, false otherwise.
     */
    private boolean hasCollided (Entity p, Entity q) {
            double zoom = game.getZoom ();
            // get the centres of the two objects.
            Vector pPos = p.getPosition();
            Vector qPos = q.getPosition();
            Vector qPos2 = new Vector(pPos);
            // and get the radii.
            double pRadius = p.getRadius() * zoom;
            double qRadius = q.getRadius() * zoom;

            // get the distance between the centers of the two objects.
            Vector distance = Vector.torusDistance(pPos, qPos,
                    game.getWorldSize());

            // and compare to the sum of the radii.
            if (distance.abs() < pRadius + qRadius) {
                return true;
            }
        return false;
    }

    /**
     *  Eat a food object, and increase our score, and the size of the disk.
     */
    private void eatFood (Food food, Disk disk) {
            score += 1;
            disk.grow(1);
            game.removeObject(food);
    }

    /**
     *  Check if we can eat another disk that we have collided with. If
     *  so, it needs to be removed from the game's list of objects, and
     *  also from the other player's list of disks.
     */
    private void eatOtherDisk (Disk theirs, Disk ours) {
        // our disk needs to be bigger than the other one by a given
        // amount in order for us to be able to eat them. If the size
        // difference is less than that threshold, we will return and do
        // nothing.
        if(!ours.getIsShielded() && !theirs.getIsShielded()) {
            if (ours.getRadius() > theirs.getRadius()) {
                // eat the other disk.
                score += theirs.getRadius();
                ours.grow(theirs.getRadius());
                theirs.eaten();
                disksEaten++;
                if (theirs.getOwner().disks.size() == 0)
                    game.removePlayer(theirs.getOwner());
            } else if (ours.getRadius() < theirs.getRadius()) {
                ours.eaten();
            }
        }
    }

    /**
     *  Our disk has collided with a virus. If our disk is bigger than a
     *  certain size, it will be popped, or broken up into many smaller
     *  disks.
     */

    private void popDisk (Virus virus, Disk disk) {
		if(score>0){
		   disk.shrink(score);
		   score = 0;
		   game.removeObject (virus);
		}
    }
	
	/**
	 *  Gets the player's score
	 */
	public double getScore() {
		return score;
	}

	/**
	 *   Gets the number of other disks the player has eaten
	 */
	public double getDisksEaten(){
		return disksEaten;
	}
	
	/**
	 *  Return the username and score
	 */
	public String toString(){
	    return name+":"+(int)score;
    }
	
	/**
	 *   Set the name of the player
	 */
	public void setName(String name){
		this.name = name;
	}

    /**
     *   Get the name of the player
     */
    public String getName(){
        return name;
    }

    /**
     *This is to turn off and on the shield of the plaer
     */
    public void setIsShielded(boolean state){
        for(int i=0; i<disks.size(); i++)
            disks.get(i).setIsShielded(state);
    }

    /**
     * This is to turn off and on a player's visibility
     */
    public void setIsVisible(boolean state){
        for(int i=0; i<disks.size(); i++)
            disks.get(i).setIsVisible(state);
    }

    /**
     * This allows a player to teleport to a random place
     * in the world
     */
    public void teleport(){
        Vector teleportPosition = Vector.random (game.getWorldSize ());
        for(int i=0; i<disks.size(); i++)
            disks.get(i).setPosition(teleportPosition);
    }

    /**
     * This returns the current velocity of a player
     */
    public Vector getVelocity(){
        return disks.get(0).calculateDragForce();
    }

    public Vector getCurrentVelocity(){return disks.get(0).getVelocity();}

    /**
     * This sets the position of other player object in the world
     */
    public void setPosition(Vector pos){
        for(int i=0; i<disks.size(); i++)
            disks.get(i).setPosition(pos);
    }
    /**
     * This returns whether a player is alive
     */
    public boolean isAlive(){
        return isAlive;
    }

    /**
     *This returns the current score of the player
     */
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * This returns the total disks eaten
     */
    public void setDisksEaten(double disksEaten) {
        this.disksEaten = disksEaten;
    }

    /**
     * This returns the total disks the player has
     */
    public int getDisks(){return disks.size();}

    /**
     * Return the shielded state of a player
     */
    public boolean getIsShielded(){return disks.get(0).getIsShielded();}
}

// vim: ts=4 sw=4 et
