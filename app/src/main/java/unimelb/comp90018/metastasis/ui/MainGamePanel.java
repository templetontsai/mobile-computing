package unimelb.comp90018.metastasis.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageButton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import java.util.logging.LogRecord;

import unimelb.comp90018.metastasis.Entity;
import unimelb.comp90018.metastasis.Food;
import unimelb.comp90018.metastasis.Perk;
import unimelb.comp90018.metastasis.Player;
import unimelb.comp90018.metastasis.Vector;
import unimelb.comp90018.metastasis.gamedata.SessionManager;
import unimelb.comp90018.metastasis.network.GameDataTransferClient;
import unimelb.comp90018.metastasis.network.GameDataTransferServer;


/**
 *  This class is responsible for drawing objects on the screen. The
 *  GameActivity class will pass this class a list of which objects are
 *  visible, and then this class will call a method on each object for it
 *  to draw itself on the canvas.
 */
public class MainGamePanel extends SurfaceView implements Runnable,  android.os.Handler.Callback {
    private static final String TAG = "MainGamePanel";


    // number of milliseconds to sleep before refreshing the screen.
    private static final int FRAME_DELAY = 30;

    private GamePanelFragment game;
    private SurfaceHolder surfaceHolder;
    private boolean running;
    private int lightLevel = 0;

    private SessionManager session;
    private String gameMode;

    //Responsible for client/server communication

    private static String SERVER_PORT = "4545";
    public static final int GET_GAMEDATA = 1;
    private GameDataTransferClient clientData;
    private GameDataTransferServer serverData;

    public MainGamePanel (Context context, GamePanelFragment game, Thread threadType) {
        super (context);
        this.game = game;
        setFocusable (true);
        surfaceHolder = getHolder ();
        running = false;
        session = new SessionManager(context);

        gameMode = session.getGameMode();
        if(session.getGameMode().equals("MultiplayerClient")){
            try{
                InetAddress hostName = InetAddress.getByName(session.getServerAddress());
                clientData = (GameDataTransferClient)threadType;
                clientData.start();
            }catch(IOException e){
                System.out.println(e);
            }

        }else if(session.getGameMode().equals("MultiplayerServer")){
            serverData = (GameDataTransferServer) threadType;
            serverData.start();
        }
    }

    /**
     *  This method is invoked by the runtime, and should not be called
     *  manually.
     */
    @Override
    public void draw (Canvas canvas) {
        if (canvas == null)
            return;

        super.draw(canvas);

        Bitmap backgroundTile = BitmapFactory.decodeResource(getResources(), R.drawable.tile);


        //TODO hard code color code should change to meaningful valuables later on
        switch(lightLevel) {
            case 0:
               // canvas.drawRGB(0, 0, 0);
                drawTiledBackground(canvas, backgroundTile);

                break;
            case 1:
                canvas.drawRGB(64, 64, 64);
                break;
            case 2:
                canvas.drawRGB(160, 160, 160);
                break;
            case 3:
                canvas.drawRGB(192, 192, 192);
                break;
            case 4:
                canvas.drawRGB(255, 255, 255);
                break;
        }
        List <Entity> visibleObjects = game.getVisibleObjects ();
        double zoomFactor = game.getZoom ();

        if (visibleObjects == null) {
            Log.wtf (TAG, "No objects to display.");
            return;
        }

        for (Entity object : visibleObjects)
            object.draw (canvas, zoomFactor);

        //Call methods to display perks and leader board information
        displayLeaderBoard(canvas);
        displayPerks(canvas);
    }

    @Override
    public void run () {
        Canvas c;
        running = true;

        if(gameMode.equals("MultiplayerServer")){
            sendInitialFood();
            sendInitialVector();
        }

        if(gameMode.equals("MultiplayerClient")){
            //Sends client's initial position to server
            clientData.sendVector(game.getLocalPlayer().getPosition());
        }

        while (running) {
            if(gameMode.equals("MultiplayerClient")) {
                recieveFoodandUpdates();
                if(game.getLocalPlayer().getCurrentVelocity() != null)
                    clientData.sendVector(game.getLocalPlayer().getPosition());
            }

            if(gameMode.equals("MultiplayerServer")){
                receieveClientPlayerUpdates();
                if(game.getLocalPlayer().getCurrentVelocity()!=null)
                  sendServerUpdates();
            }

            c = null;

            // sleep for a period.
            try {
                Thread.sleep (FRAME_DELAY);
            } catch (InterruptedException e) {
                Log.wtf (TAG, e);
            }

            try {
                c = surfaceHolder.lockCanvas (null);

                synchronized (surfaceHolder) {
                    draw (c);
                }
            } finally {
                if (c != null) {
                    surfaceHolder.unlockCanvasAndPost (c);
                }
            }
        }
    }

    /**
     * This method receives the position of the client as it moves in the world
     */
    public void receieveClientPlayerUpdates(){
        if(serverData.getClientVector()!=null) {
            if (game.getPlayers() == 1) {
                Log.d(TAG, "Client player is at: " + serverData.getClientVector().toString() + " You are " +
                        "at: " + game.getLocalPlayer().getPosition().toString());
                game.addNewPlayer(serverData.getClientVector());
            }else if(game.getPlayers() == 2){
                if (serverData.getClientVector()!=null) {
                    Log.d(TAG, "Client player is now at: " + serverData.getClientVector().toString());
                    game.setPlayerPosition(serverData.getClientVector());
                }
            }
        }
    }
    /**
     * This method is responsible for sending the server players position
     */
    public void sendServerUpdates(){
        Log.d(TAG, "Server player is currently at: " + game.getLocalPlayer().getPosition().toString());
        serverData.setVectorList(game.getLocalPlayer().getPosition());
    }

    /**
     * This method is responsible for receieving client updates from the server
     */
    public void recieveFoodandUpdates(){
        Log.d(TAG, "Current Total Food: "+game.getCurrTotalFood());
        if(game.getCurrTotalFood()==0) {
            List<Food> entities = new CopyOnWriteArrayList<>();
            if(clientData.getInitialFood()!=null)
                if(clientData.getInitialFood().size()>0)
                    entities.addAll(clientData.getInitialFood());

            for (int i = 0; i < entities.size(); i++)
                game.addObject(new Food(entities.get(i).getPosition()));

            Log.d(TAG, "Server food has been received and added");
        }

        if(clientData.getServerVectors()!=null) {
            if (clientData.getServerVectors()!=null && game.getPlayers() == 1) {
                Log.d(TAG, "Server player is at: " + clientData.getServerVectors().toString() + " You are " +
                        "at: " + game.getLocalPlayer().getPosition().toString());
                game.addNewPlayer(clientData.getServerVectors());
            }else if(game.getPlayers() == 2){
                Log.d(TAG, "Server player is now at: " + clientData.getServerVectors().toString());
                game.setPlayerPosition(clientData.getServerVectors());
            }
        }
    }

    /**
     * This method is responsible for sending food list to client and server player vector
     */
    public void sendInitialFood(){
        List<Entity> tempList = game.getGameObjects();
        List<Food> foodList = new CopyOnWriteArrayList<>();
        for(int i=0; i<tempList.size(); i++)
            if(tempList.get(i).getType()== Entity.EntityType.FOOD)
                foodList.add((Food)tempList.get(i));

        serverData.setFoodList(foodList);
        Log.d(TAG, "Sending Food List to Client");
    }

    /**
     * Sends a vector list of server player's initial position
     */
    public void sendInitialVector(){
        serverData.setVectorList(game.getLocalPlayer().getPosition());
        Log.d(TAG, "Sending Vector List to Client");
    }


    /**
     * This method displays the leader board for the game
     */
    public void displayLeaderBoard(Canvas canvas){
        //Display the top 5 player scores on screen
        Paint recPaint = new Paint();
        recPaint.setColor(Color.BLACK);
        recPaint.setAlpha(127);
        //Draw the black background rectangle
        int width = game.getScreenWidth()/6;
        int height = game.getScreenHeight()/4;
        canvas.drawRect(0, 0, width, height, recPaint);
        Paint paint = new Paint();
        paint.setARGB(255, 255, 255, 255);
        paint.setTextSize(game.getScreenWidth()/45);
        //Get list of players in order of top score
        List<Player> scoreList = game.getPlayerScores();
        int listItem = (height/7)*2+13;
        canvas.drawText("Leader board", 20, (height/7)*2-10, paint);
        //Ensure only 5 scores are displayed
        int limit = 5;
        if(scoreList.size()<=5)
            limit = scoreList.size();

        //Print out the top 5 scores in the list
        for(int i=0; i < limit; i++, listItem+=(height/7)){
            canvas.drawText(scoreList.get(i).toString(), 20, listItem, paint);
        }
    }

    /**
     * This method is responsible for displaying all the perk information
     */
    public void displayPerks(Canvas canvas){
        //Show active list of perks gained by coloring back of icon
        List<Perk> currentPerks = game.getGainedPerks();

        //Check if the perks are active and display green
        Paint activePaint = new Paint();
        activePaint.setColor(Color.GREEN);
        activePaint.setAlpha(127);
        Paint inUsePaint = new Paint();
        inUsePaint.setColor(Color.BLUE);
        inUsePaint.setAlpha(127);
        if(currentPerks.get(0).getIsActive()){
            canvas.drawRect(game.getScreenWidth()/2-160, game.getScreenHeight()-90, game.getScreenWidth()/2-94, game.getScreenHeight()-10, activePaint);
        }
        if(currentPerks.get(1).getIsActive()){
            canvas.drawRect(game.getScreenWidth()/2-94,  game.getScreenHeight()-90, game.getScreenWidth()/2-26, game.getScreenHeight()-10, activePaint);
        }
        if(currentPerks.get(2).getIsActive()){
            canvas.drawRect(game.getScreenWidth()/2-26, game.getScreenHeight()-90, game.getScreenWidth()/2+49, game.getScreenHeight()-10, activePaint);
        }
        if(currentPerks.get(3).getIsActive()){
            canvas.drawRect(game.getScreenWidth()/2+49, game.getScreenHeight()-90, game.getScreenWidth()/2+118, game.getScreenHeight()-10, activePaint);
        }

        //Give blue background indicating perk is in use for only shield and invisibility
        if(currentPerks.get(0).getIsInUse()){
            canvas.drawRect(game.getScreenWidth()/2-160, game.getScreenHeight()-90, game.getScreenWidth()/2-94, game.getScreenHeight(), inUsePaint);
        }
        if(currentPerks.get(3).getIsInUse()){
            canvas.drawRect(game.getScreenWidth()/2+49, game.getScreenHeight()-90, game.getScreenWidth()/2+118, game.getScreenHeight(), inUsePaint);
        }
    }

    public void drawTiledBackground(Canvas canvas, Bitmap backgroundTile){
        float left = 0, top = 0;
        float bgTileWidth = backgroundTile.getWidth();
        float bgTileHeight = backgroundTile.getHeight();

        while (left < game.getScreenWidth()) {
            while (top < game.getScreenHeight()) {
                canvas.drawBitmap(backgroundTile, left, top, null);
                top += bgTileHeight;
            }
            left += bgTileWidth;
            top = 0;
        }
    }

    /**
     *  Start the thread that refreshes the display.
     */
    public void startDisplay () {
        Thread drawThread = new Thread (this);
        drawThread.start ();
    }

    /**
     *  Stop the thread that is responsible for refreshing the display.
     */
    public void stopDisplay () {
        running = false;
    }

    /**
     *  Set game background color according to the ambient light of the environment
     */
    synchronized public void setLightLevel(int lightLevel) {
        this.lightLevel = lightLevel;
    }

    /**
     * Returns the width of phone screen, this method is for acc to calculate the coordinate of position
     */
    public int getScreenWidth() {
        return game.getScreenWidth();

    }

    /**
     * Returns the height of phone screen, this method is for acc to calculate the coordinate of position
     */
    public int getScreenHeight() {
        return game.getScreenHeight();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {

            case GET_GAMEDATA:
                Object obj = msg.obj;
               // mTextView.setText((String)obj);

        }
        return true;
    }
}

// vim: ts=4 sw=4 et
