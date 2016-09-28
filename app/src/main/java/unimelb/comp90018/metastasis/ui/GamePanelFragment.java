package unimelb.comp90018.metastasis.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import unimelb.comp90018.metastasis.AutomatedPlayer;
import unimelb.comp90018.metastasis.Entity;
import unimelb.comp90018.metastasis.Food;
import unimelb.comp90018.metastasis.HumanPlayer;
import unimelb.comp90018.metastasis.OtherPlayer;
import unimelb.comp90018.metastasis.Perk;
import unimelb.comp90018.metastasis.Player;
import unimelb.comp90018.metastasis.SensingCapability;
import unimelb.comp90018.metastasis.Utils;
import unimelb.comp90018.metastasis.Vector;
import unimelb.comp90018.metastasis.Virus;
import unimelb.comp90018.metastasis.gamedata.DatabaseHandlerScore;
import unimelb.comp90018.metastasis.gamedata.GameConstants;
import unimelb.comp90018.metastasis.gamedata.GameScore;
import unimelb.comp90018.metastasis.gamedata.GameSetting;
import unimelb.comp90018.metastasis.gamedata.SessionManager;
import unimelb.comp90018.metastasis.network.GameDataTransferClient;
import unimelb.comp90018.metastasis.network.GameDataTransferServer;

/**
 *  This class is responsible for lists of players, both human and AI,
 *  and objects in the game. Also contains the main loop, consisting of
 *  updating the positions of disks, and redrawing objects that are visible
 *  on the screen, done many times per second to create an impression of smooth movement.
 *  Also it is a fragment and will overlap with a game control panel for special capability
 */
public class GamePanelFragment extends Fragment implements Runnable {
    private static final String TAG = "GamePanelFragment";

    // number of milliseconds to sleep before updating the physics of all
    // of the game objects.
    private static final int UPDATE_DELAY = 30;

    private static final int WORLD_WIDTH = 6000;
    private static final int WORLD_HEIGHT = 6000;

    private static final int NUM_INITIAL_FOOD = 200;
    private static final int TOTAL_WORLD_FOOD = 300;

    private static final int NUM_INITIAL_VIRUS = 10;
    private static final int NUM_INITIAL_AI = 3;
    private static final int NUM_MAX_AI = 5;
    private static int CURR_TOTAL_AI = 3;
    private static int CURR_TOTAL_FOOD = 0;
    private static final double FOOD_SPAWNED_PER_ITERATION = 0.01;
    private static final double PR_NEW_AI_PLAYER = 0.1;
    private static final double VIRUS_SPAWNED_PER_ITERATION = 0;

    // proportion of the screen that will be occupied by the player's disks.
    private static final double ZOOM_FACTOR = 0.045;


    private Vector worldDimensions;

    private MainGamePanel display;
    private boolean isPlaying;
    private List<Player> players;
    private List <Entity> gameObjects;
    private List <Entity> visibleObjects;

    // the player who is using the local device.
    private HumanPlayer localPlayer;

    private int screenWidth;
    private int screenHeight;

    private double zoom;
    private Context mContext;

    //store the time the player has been alive in the game
    private float gameStartTime =0;
    private float gameEndTime = 0;
    private float gameRunTime = 0;

    private int totalAI = 0;
    //Create session get username and use for player
    private SessionManager session;
    private String username;
    private String gameMode;
    private String [] keywords;
    private ArrayList<Bitmap> texturesArray = new ArrayList<Bitmap>();
    private Bitmap texture;

    //Arraylst to store perks
    private List<Perk> gainedPerks;
    private Timer perkTimer;

    private SensingCapability mSensingCapability = null;
    private GameSetting mGameSetting = null;
    private GameDataTransferServer mGameDataTransferServer = null;
    private GameDataTransferClient mGameDataTransferClient = null;

    public GamePanelFragment(Context context, Thread threadType) {
        mContext = context;
        if( threadType instanceof GameDataTransferClient) {
            mGameDataTransferClient = (GameDataTransferClient) threadType;
        } else if(threadType instanceof GameDataTransferServer) {
            mGameDataTransferServer = (GameDataTransferServer) threadType;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        players = new CopyOnWriteArrayList<Player>();
        gameObjects = new CopyOnWriteArrayList <Entity> ();
        visibleObjects = new CopyOnWriteArrayList <Entity> ();
        gainedPerks = new CopyOnWriteArrayList <Perk> ();

        gainedPerks.add(new Perk("Shield", 15));
        gainedPerks.add(new Perk("Virus", 0));
        gainedPerks.add(new Perk("Teleportation",0));
        gainedPerks.add(new Perk("Invisibility",15 ));

        // get screen dimensions.
        DisplayMetrics metrics = new DisplayMetrics ();
        WindowManager mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.getDefaultDisplay ().getMetrics (metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        worldDimensions = new Vector (WORLD_WIDTH, WORLD_HEIGHT);

        Log.d(TAG, "Playing with screen resolution of " + screenWidth
                + " by " + screenHeight);

        //Set the session to obtain the username and game mode
        session = new SessionManager(getActivity());
        username = session.getLoginUsername();
        gameMode = session.getGameMode();

        //Create list of keywords for textures
        keywords = new String[]{"lupiyamujala", "matt", "templeton", "pura", "arun", "gnick", "queen"};

        //Create the array list of textures to load when the user selects a username in the list
        texturesArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.zambia));
        texturesArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.australia));
        texturesArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.taiwan));
        texturesArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.srilanka));
        texturesArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.india));
        texturesArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.king));
        texturesArray.add(BitmapFactory.decodeResource(getResources(), R.drawable.queen));

        //Check if the username is amon the keywords and load texture
        for(int i=0; i<keywords.length; i++){
            if(username.equals(keywords[i])) {
                texture = texturesArray.get(i);
            }
        }

        // create initial game objects and player classes.
        localPlayer = new HumanPlayer (this, username, texture);
        players.add(localPlayer);

        if(session.getGameStateSession().equals(GameConstants.PAUSED_STATE))
        {
            // to be set to current playerscores from previous games scores
            restoreHumanPlayerScoresFromPrevious(localPlayer);
        }

        //Using session variable to determine game actions based on single and multiplayer mode
        //Both single player and multiplayer server can create food
        if(gameMode.equals("SinglePlayer") || gameMode.equals("MultiplayerServer")) {
            createInitialFood();
        }
        //Only single player mode can create AI players and virus
        if(gameMode.equals("SinglePlayer")) {
            createInitialAIPlayers();
            createInitialVirus();

            if(session.getGameStateSession().equals(GameConstants.PAUSED_STATE))
            {
                // to be set to current playerscores from previous games scores
                for (Player player : players) {
                    if(player.getName().contains(GameConstants.AI)) {
                        restoreAIPlayerScoresFromPrevious(player);
                    }

                }
            }
        }



        if(mGameDataTransferClient != null)
            display = new MainGamePanel (mContext, this, mGameDataTransferClient);
        else if(mGameDataTransferServer != null)
            display = new MainGamePanel (mContext, this, mGameDataTransferServer);
        else
            display = new MainGamePanel (mContext, this, null);
        //Create SensingCapability for ambient light and accelerometer
        mGameSetting = session.getGameSettingSession();
        if(Utils.isSensorSupported(mContext, Sensor.TYPE_LIGHT) || Utils.isSensorSupported(mContext, Sensor.TYPE_ACCELEROMETER) ) {
            mSensingCapability = SensingCapability.getInstance();
            mSensingCapability.init(mContext, display);
        } else {
            Log.d(TAG, "No Sensing capability is supported");
        }

        perkTimer = new Timer();

        gameStartTime = System.currentTimeMillis();
        // Inflate the layout for this fragment
        return display;
    }

    public  void restoreHumanPlayerScoresFromPrevious(HumanPlayer humanPlayer)
    {
        DatabaseHandlerScore scoreHandler = new DatabaseHandlerScore(getActivity());
        List<GameScore> scoreList = scoreHandler.getLatestGameScore();

        for(int i = 0 ; i < scoreList.size() ; i++) {
            GameScore gameScore = (GameScore) scoreList.get(i);
            // It should be set to current game_fragment.players (Human Player)
            if(gameScore.getPlayer_type().equals(GameConstants.HUMAN_PLAYER))
            {
                humanPlayer.setScore(gameScore.getScore());
                humanPlayer.setDisksEaten(gameScore.getDisks_eaten());
            }
        }
    }

    public  void restoreAIPlayerScoresFromPrevious(Player aiPlayer)
    {
        DatabaseHandlerScore scoreHandler = new DatabaseHandlerScore(getActivity());
        List<GameScore> scoreList = scoreHandler.getLatestGameScore();

        for(int i = 0 ; i < scoreList.size() ; i++) {
            GameScore gameScore = (GameScore) scoreList.get(i);
            // It should be set to current game_fragment.players (Human Player)
            if(gameScore.getPlayer_type().equals(GameConstants.AI_PLAYER) && aiPlayer.getName().equals(gameScore.getPlayer_name()))
            {
                aiPlayer.setScore(gameScore.getScore());
                aiPlayer.setDisksEaten(gameScore.getDisks_eaten());
            }
        }
    }


    @Override
    public void onResume() {
        gameStartTime = System.currentTimeMillis();
        super.onResume();
        Log.d(TAG, "Resume activity");

        //To register background
        if(mSensingCapability != null && mGameSetting.isSensors()) {
            if(mSensingCapability.registerSensors(Sensor.TYPE_LIGHT))
                Log.d(TAG, "Light Sensor is registered successfully");
        }

        //To move the player disk either with hand touch or accelerometer
        if(mSensingCapability != null && mGameSetting.isAccelerometer()) {
            mSensingCapability.registerSensors(Sensor.TYPE_ACCELEROMETER);
            mSensingCapability.setPlayer(localPlayer);

            Log.d(TAG, "Accelerometer Sensor is registered successfully");


        } else {
            display.setOnTouchListener(localPlayer);
        }

        Thread t = new Thread (this);

        t.start();
        display.startDisplay();

    }

    @Override
    public void onPause () {
        super.onPause();
        Log.d(TAG, "Pause activity");

        if(mSensingCapability != null) {
            if(mGameSetting.isSensors() || mGameSetting.isAccelerometer()) {
                mSensingCapability.unregisterSensors();
            }
        }


        isPlaying = false;
        display.stopDisplay();
    }

    /**
     *  Main game update loop. On each iteration, each Player object will
     *  be called to update the position of their disk(s), check for
     *  collisions with other disks/viruses/food, and finally, any game
     *  objects that are visible on the screen will be drawn.
     *
     *  This method stops when the isPlaying flag is set to false by the
     *  Android onPause callback.
     */
    @Override
    public void run () {
        isPlaying = true;

        Log.d(TAG, "Main game loop starting.");

        while (isPlaying) {
            //Both single player and multiplayer server can create food
            if(gameMode.equals("SinglePlayer")) {
                spawnFood();
            }

            if(gameMode.equals("SinglePlayer")){
                spawnAIPlayers ();
                spawnVirus();
            }

            calculateZoomFactor ();

            Log.d(TAG, "Current Total Food: "+CURR_TOTAL_FOOD);

            // step through all of the player objects, and allow them to
            // update their disks. For the human players, this will
            // involve moving the disks according to UI input; for AI
            // players, the AI can assess the distance to food and other
            // disks, and choose a direction to move in.
            for (Player player : players) {
                player.step (gameObjects);
            }

            Log.d(TAG, "Updated " + players.size() + "players.");

            //Perks only for single player mode
            if(gameMode.equals("SinglePlayer")) {
                generatePerks();
            }
            constructVisibleObjectsList();
            frameDelay();
            //Exits the game if local player has been eaten
            if(!localPlayer.isAlive())
                isPlaying = false;
        }

        gameEndTime = System.nanoTime();
        gameRunTime = gameEndTime - gameStartTime;
        //Write game details to the database
        if(!localPlayer.isAlive() || localPlayer.getDisks()==0){

            clearDbScore();
            List<Player> gameScores = this.getPlayerScores();
            for(int i = 0 ; i < gameScores.size() ; i++){
                Player player = (Player) gameScores.get(i);
                saveGameScore(player);
            }

            session.createGameStateSession(GameConstants.FINISHED_STATE);

            Intent intent = new Intent(getActivity(), SummaryActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        if(gameMode.equals("MultiplayerServer") || gameMode.equals("MultiplayerClient")){
            if(players.size()>1) {
                if (!players.get(1).isAlive() || players.get(1).getDisks() == 0) {
                    clearDbScore();
                    List<Player> gameScores = this.getPlayerScores();
                    for (int i = 0; i < gameScores.size(); i++) {
                        Player player = (Player) gameScores.get(i);
                        saveGameScore(player);
                    }

                    session.createGameStateSession(GameConstants.FINISHED_STATE);

                    Intent intent = new Intent(getActivity(), SummaryActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        }
        Log.d(TAG, "Exited from main game loop.");
    }

    /**
     *  Delay for the correct period of time given the frame rate.
     */
    private void frameDelay () {
        try {
            Thread.sleep (UPDATE_DELAY);
        } catch (InterruptedException e) {
            Log.wtf (TAG, "Sleep call interrupted", e);
        }
    }

    /**
     *  Create food particles at random locations on game startup.
     *
     *  TODO: refactor. This method duplicates spawnFood; need a single
     *  method.
     */
    private void createInitialFood () {
        Food food;
        Log.d(TAG, "Creating initial food pellets.");

        for (int i = 0; i < NUM_INITIAL_FOOD;) {
            Vector spawnPosition = Vector.random (worldDimensions);
            boolean spawnEntity = true;
            for(int j=0; j<gameObjects.size() && spawnEntity; j++)
                if(objectOverlaps(spawnPosition, gameObjects.get(j).getPosition()))
                    spawnEntity = false;
            if(spawnEntity) {
                Log.d(TAG, "Adding food pellet "+i);
                food = new Food(spawnPosition);
                addObject(food);
                CURR_TOTAL_FOOD++;
                i++;
            }
        }
    }


    /**
     *Create viruses that pop player disks
     *
     */
    private void createInitialVirus(){
        Virus virus;
        Log.d(TAG, "Creating initial virus pellets");

        for (int i = 0; i < NUM_INITIAL_VIRUS; i++){
            virus = new Virus(Vector.random(worldDimensions), mContext);
            addObject(virus);
        }
    }


    /**
     *  Create initial AI players on game startup.
     */
    private void createInitialAIPlayers () {
        Log.d (TAG, "Creating initial AI players.");

        for (int i = 0; i < NUM_INITIAL_AI; i ++){
            totalAI = i+1;
            String name = "AI " + totalAI;
            players.add(new AutomatedPlayer(this, name));
        }
    }

    /**
     *  Spawn food particles. This method is called during each update of
     *  the game, and will create a fixed number of food particles at
     *  random positions around the game world.
     */
    private void spawnFood () {
        Food food;

        for (int i = 0; i < FOOD_SPAWNED_PER_ITERATION; i ++) {
            if(CURR_TOTAL_FOOD<=TOTAL_WORLD_FOOD) {
                food = new Food(Vector.random(worldDimensions));
                addObject(food);
                CURR_TOTAL_FOOD++;
            }
        }
    }

    /**
     * Spawn virus particles. This method is called during each update of the
     * game and will create a fixed number of virus particles at random
     * random positions around the game world
     */
    private void spawnVirus(){
        Virus virus;

        for(int i = 0; i <VIRUS_SPAWNED_PER_ITERATION; i++){
            virus = new Virus(Vector.random(worldDimensions), mContext);
            addObject(virus);
        }
    }


    /**
     *  Spawn new AI players. This is rather different to spawning food,
     *  since we do not want to get too many players in the game. Instead,
     *  we will generate a random number, and see if it is greater than a
     *  notional probability of a new player appearing. If a new player is
     *  to be created, we will add them at a random location in the game
     *  world.
     */
    private void spawnAIPlayers () {
        // if there is no new player to be spawned on this iteration, then
        // there is nothing more for this method to do.
        if (Math.random () > PR_NEW_AI_PLAYER)
            return;

        if(CURR_TOTAL_AI<NUM_MAX_AI) {
            totalAI++;
            String name = "AI " + totalAI;
            AutomatedPlayer ai = new AutomatedPlayer(this, name);
            players.add(ai);
            CURR_TOTAL_AI++;
        }
    }

    /**
     *  Step through the list of all game objects, and find out which
     *  ones are visible on the screen.
     */
    private void constructVisibleObjectsList () {
        List <Entity> objects = new ArrayList<Entity>();

        for (Entity object : gameObjects) {
            if (isInsideScreenWindow (object)) {
                objects.add (object);
                setPositionOnScreen (object);
            }
        }

        visibleObjects.clear ();
        visibleObjects.addAll(objects);
    }

    /**
     *  Test if a given game object is visible on the screen. The screen
     *  is rectangular, and centered on the disk of the local device's
     *  player. Any disk, food or virus that overlaps onto that rectangle
     *  is visible, and must be drawn.
     */
    private boolean isInsideScreenWindow (Entity object) {
        Vector position = object.getPosition();
        double radius = object.getRadius ();
        Vector screenBase = getScreenPosition ();

        if ((screenBase.x > position.x + radius) && (position.x - radius >
                (screenBase.x + screenWidth) % WORLD_WIDTH))
            return false;

        if ((screenBase.y > position.y + radius) && (position.y - radius >
                (screenBase.y + screenHeight) % WORLD_HEIGHT))
            return false;

        return true;
    }

    /**
     *  Tell an object that is visible on the screen what coordinates it
     *  should use to draw itself at. This method will calculate the
     *  coordinates of the object on the screen, and then call the object's
     *  setScreenPosition method to set those coordinates as the position
     *  where the object will draw itself.
     */
    private void setPositionOnScreen (Entity object) {
        Vector coordinates = getScreenPosition();
        double objectRadius = object.getRadius ();

        coordinates.scale (-1);
        coordinates.addComponents (object.getPosition ());

        // check if the object is on a section of screen that overlaps a
        // boundary of the world.
        if (coordinates.x + objectRadius < 0)
            coordinates.x += worldDimensions.x;

        if (coordinates.y + objectRadius < 0)
            coordinates.y += worldDimensions.y;

        // tell the object where to draw itself at.
        object.setScreenPosition(coordinates);
    }

    /**
     *  Calculate what factor to multiply object sizes by so that the
     *  player's disk is drawn at a reasonable size.
     */
    synchronized private void calculateZoomFactor () {
        // we want the player's disk to be about 0.1 times the width of
        // the main diagonal across the screen in radius. Note that we
        // use a shortcut to calculate the approximate diagonal by adding
        // the two short sides, to avoid a slow sqrt operation, which is
        // not necessary her; we just need a heuristic.
        zoom = ZOOM_FACTOR * (double) (screenWidth + screenHeight);
        zoom /= localPlayer.sumDiskRadius ();

        zoom = ZOOM_FACTOR * (double) (screenWidth + screenHeight);
        zoom /= localPlayer.sumDiskRadius ();

        if(Double.isInfinite(zoom))
            Log.d(TAG, "zoom =" + zoom);



    }

    synchronized public double getZoom () {
        return zoom;
    }

    /**
     *  Returns the vector position of the top left corner of the device
     *  screen in the game world coordinates.
     */
    public Vector getScreenPosition () {
        Vector screenPosition = new Vector ();
        Vector playerPosition = localPlayer.getPosition();

        screenPosition.x = playerPosition.x - screenWidth / 2.0;
        screenPosition.y = playerPosition.y - screenHeight / 2.0;

        if (screenPosition.x < 0)
            screenPosition.x += WORLD_WIDTH;

        if (screenPosition.y < 0)
            screenPosition.y += WORLD_HEIGHT;

        return screenPosition;
    }

    /**
     *  Returns a list of objects that are visible on screen.
     */
    public List <Entity> getVisibleObjects () {
        return visibleObjects;
    }

    /**
     *  This method is called when an object has been created by one of
     *  the players, such as when a new player is created with a new disk,
     *  or if a player splits their disk.
     */
    public void addObject (Entity object) {
        gameObjects.add (object);
        if(gameMode.equals("MultiplayerClient"))
            if(object.getType()== Entity.EntityType.FOOD)
                CURR_TOTAL_FOOD++;
    }

    /**
     *  This is called when some object has been removed from the game,
     *  eg when food or a disk gets eaten. And reduces number of AI count
     */
    public void removeObject (Entity object) {
        if(object.getType()== Entity.EntityType.DISK)
            CURR_TOTAL_AI--;

        if(gameMode.equals("SinglePlayer"))
            if(object.getType()== Entity.EntityType.FOOD)
                CURR_TOTAL_FOOD--;
        
        gameObjects.remove(object);
    }

    /**
     *  Returns the dimensions of the game world in a vector object.
     */
    public Vector getWorldSize () {
        return worldDimensions;
    }


    /**
     * Returns the all the game players' current scores in order
     */
    public List<Player> getPlayerScores() {
        ArrayList<Player> scoreList = new ArrayList<Player>();
        for (Player player : players) {
            scoreList.add(player);
            //player.step (gameObjects);
        }
        Collections.sort(scoreList, new Comparator<Player>() {
            public int compare(Player one, Player other) {
                return Double.compare(other.getScore(), one.getScore());
            }
        });
        return scoreList;
    }

    /**
     * Returns the width of phone screen
     */
    public int getScreenWidth() {
        return screenWidth;
    }

    /**
     * Returns the height of phone screen
     */
    public int getScreenHeight() {
        return screenHeight;
    }

    /**
     * This method is what generates perks for the player
     */
    public void generatePerks(){
        //Award Virus perk if at least 1 mins of play has been done
        perkTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                gainedPerks.get(1).setActive(true);
            }
        }, 1*60*1000,1*60*1000);

        //Award shield perk if at least every 120 seconds of play has been done
        perkTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                gainedPerks.get(0).setActive(true);
            }
        }, 1*60*1000,1*60*1000);

        //Award teleportation if user has eaten 20 food items
        if(players.get(0).getScore()==10)
            gainedPerks.get(2).setActive(true);

        //Award invisibility if player has eaten 2 player disks
        if(players.get(0).getDisksEaten()==2)
            gainedPerks.get(3).setActive(true);

    }
    /**
     * Returns list of perks player has gained
     */
    public List getGainedPerks(){
        return gainedPerks;
    }

    /**
     * This portion is to use a perk
     */
    public void usePerk(String perk){
        //This is for the player to use the shield for 10 seconds
        if(gainedPerks.get(0).getIsActive() && perk.equals("Shield")){
            players.get(0).setIsShielded(true);
            gainedPerks.get(0).setActive(false);
            gainedPerks.get(0).setInUse(true);
            perkTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    players.get(0).setIsShielded(false);
                    gainedPerks.get(0).setInUse(false);
                }
            }, gainedPerks.get(3).getDuration() * 1000);
            //This drops a virus
        }else if(gainedPerks.get(1).getIsActive() && perk.equals("Virus")){
            Virus virus;
            Vector playerPosition = players.get(0).getPosition();
            Vector playerDragForce = players.get(0).getVelocity();
            double playerRadius = players.get(0).sumDiskRadius();
            if(playerDragForce.x>0 && playerDragForce.y>0){
                double x = playerPosition.x + (playerRadius*zoom) + (2.5*zoom);
                double y = playerPosition.y + (playerRadius*zoom) + (2.5*zoom);
                Vector spawPosition = new Vector(x,y);
                virus = new Virus(spawPosition, mContext);
                addObject(virus);
            }else if(playerDragForce.x<0 && playerDragForce.y>0){
                double x = playerPosition.x - (playerRadius*zoom) - (2.5*zoom);
                double y = playerPosition.y + (playerRadius*zoom) + (2.5*zoom);
                Vector spawPosition = new Vector(x,y);
                virus = new Virus(spawPosition, mContext);
                addObject(virus);
            }else if(playerDragForce.x<0 && playerDragForce.y<0){
                double x = playerPosition.x - (playerRadius*zoom) - (2.5*zoom);
                double y = playerPosition.y - (playerRadius*zoom) - (2.5*zoom);
                Vector spawPosition = new Vector(x,y);
                virus = new Virus(spawPosition, mContext);
                addObject(virus);
            }else if(playerDragForce.x>0 && playerDragForce.y<0){
                double x = playerPosition.x + (playerRadius*zoom) + (2.5*zoom);
                double y = playerPosition.y - (playerRadius*zoom) - (2.5*zoom);
                Vector spawPosition = new Vector(x,y);
                virus = new Virus(spawPosition, mContext);
                addObject(virus);
            }else if(playerDragForce.x == -0.0 && playerDragForce.y == -0.0){
                double x = playerPosition.x + (playerRadius*zoom) + (2.5*zoom);
                double y = playerPosition.y + (playerRadius*zoom) + (2.5*zoom);
                Vector spawPosition = new Vector(x,y);
                virus = new Virus(spawPosition, mContext);
                addObject(virus);
            }
            gainedPerks.get(1).setActive(false);
            //This teleports the player to a random place
        }else if(gainedPerks.get(2).getIsActive() && perk.equals("Teleportation")){
            players.get(0).teleport();
            gainedPerks.get(2).setActive(false);
            //This makes the player invisible for 10 seconds
        }else if((gainedPerks.get(3).getIsActive()) && perk.equals("Invisibility")){
            players.get(0).setIsVisible(false);
            gainedPerks.get(3).setActive(false);
            gainedPerks.get(3).setInUse(true);
            if(gainedPerks.get(0).getIsActive()) {
                perkTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        players.get(0).setIsVisible(true);
                        gainedPerks.get(3).setInUse(false);
                    }
                }, gainedPerks.get(3).getDuration() * 1000);
            }
        }
    }

    /**
     * This returns the amount of time the player has been alive
     */
    public float getTimeAlive(){
        return gameStartTime;
    }


    /**
     * These are the multiplayer methods
     */
    /**
     * This method returns all the game objects in the world
     */
    public List<Entity> getGameObjects(){return gameObjects;}


    /**
     * This method returns the local player of the game as player object
     */
    public Player getLocalPlayer(){return localPlayer;}

    /**
     * This method returns the local player of the game as a human player object
     */
    public HumanPlayer getLocalHumanPlayer(){return localPlayer;}

    /**
     * This method creates a new player using coordinates given
     */
    public void addNewPlayer(Vector coords){
        players.add(new OtherPlayer(this,"Other Player", coords.x, coords.y));
    }

    /**
     * This method removes a player from the world
     */
    public void removePlayer(Player object){
        players.remove(object);
    }
    /**
     * This sets the client player's position in the world
     */
    public void setPlayerPosition(Vector coords){
        players.get(1).setPosition(coords);
    }

    public int getPlayers(){
        return players.size();
    }

    /**
     * This returns current total food
     */
    public int getCurrTotalFood(){return CURR_TOTAL_FOOD;}

    /**
     * Save Game Summary to db
     */
    public  void saveGameScore(Player player)
    {
        DatabaseHandlerScore dbHandler = new DatabaseHandlerScore(this.getActivity());

        Date played_date = new Date();
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
        try {
            played_date = formater.parse(played_date.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String player_type = "";
        if( player.getName().contains("AI"))
        {
            player_type = GameConstants.AI_PLAYER;
        }
        else
        {
            player_type = GameConstants.HUMAN_PLAYER;
        }

        GameScore gameScore = new GameScore(1, 1, player.getName(), player_type, player.getScore(), player.getDisksEaten(), 1, played_date, GameConstants.FINISHED_STATE);
        dbHandler.addScore(gameScore);

    }

    public  void clearDbScore() {
        DatabaseHandlerScore scoreHandler = new DatabaseHandlerScore(this.getActivity());
        scoreHandler.clearGameScores();
    }


    /**
     * This method ensures that game objects initially created don't collide with each other
     */
    private boolean objectOverlaps (Vector p, Vector q) {
        double zoom = getZoom();
        // get the centres of the two objects.
        Vector pPos = p;
        Vector qPos = q;
        Vector qPos2 = new Vector(pPos);
        // and get the radii.
        double pRadius = 1.0 * zoom;
        double qRadius = 1.0 * zoom;

        // get the distance between the centers of the two objects.
        Vector distance = Vector.torusDistance(pPos, qPos, getWorldSize());

        // and compare to the sum of the radii.
        if (distance.abs() < pRadius + qRadius) {
            return true;
        }
        return false;
    }
}

// vim: ts=4 sw=4 et
