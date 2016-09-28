package unimelb.comp90018.metastasis.ui;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import unimelb.comp90018.metastasis.Player;
import unimelb.comp90018.metastasis.gamedata.DatabaseHandlerScore;
import unimelb.comp90018.metastasis.gamedata.GameConstants;
import unimelb.comp90018.metastasis.gamedata.GameScore;
import unimelb.comp90018.metastasis.gamedata.SessionManager;
import unimelb.comp90018.metastasis.network.GameDataTransferClient;
import unimelb.comp90018.metastasis.network.GameDataTransferServer;

/**
 *  Main activity for the game as well as inherent ImmersiveActivity for immersive mode.
 */
public class GameActivity extends ImmersiveActivity {
    private static final String TAG = "GameActivity";


    private GamePanelFragment  game_fragment;
    private SessionManager session;
    private String gameMode;
    private int connection_id = 0;
    private GameDataTransferServer mGameDataTransferServer;
    private GameDataTransferClient mGameDataTransferClient;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
           connection_id = bundle.getInt("connection_id");

        Log.d(TAG, "Connection id: "+connection_id);

        if(connection_id == 2) {
            Log.d(TAG, "group owner starting server");
            mGameDataTransferServer = (GameDataTransferServer)bundle.getSerializable("socket_instance");
            game_fragment = new GamePanelFragment(this, mGameDataTransferServer);

        } else if(connection_id == 1) {
            Log.d(TAG, "client starting data transfer");
            mGameDataTransferClient = (GameDataTransferClient)bundle.getSerializable("socket_instance");
            game_fragment = new GamePanelFragment(this, mGameDataTransferClient);
        }else if(connection_id == 0) {
            game_fragment = new GamePanelFragment(this, null);
        }

        session = new SessionManager(getApplicationContext());
        gameMode = session.getGameMode();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        fragmentTransaction.add(R.id.game_main_relativeLayout, game_fragment);


        if(gameMode.equals("SinglePlayer")) {
            CapabilityPanelFragment capability_fragment = new CapabilityPanelFragment();
            fragmentTransaction.add(R.id.game_main_relativeLayout, capability_fragment);
        }

        fragmentTransaction.commit();

        setContentView(R.layout.activity_game);
    }

    public void onClickPause(View view) {

        clearDbScore();
        List<Player> gameScores = game_fragment.getPlayerScores();
        for(int i = 0 ; i < gameScores.size() ; i++){
            Player player = (Player) gameScores.get(i);
            saveGameScore(player);
        }

        sessionManager = new SessionManager(this);
        sessionManager.createGameStateSession(GameConstants.PAUSED_STATE);


        Intent gamePauseActivityIntent = new Intent(this, PauseActivity.class);
        startActivity(gamePauseActivityIntent);
        finish();

    }

    public void onClickShield(View view){
        game_fragment.usePerk("Shield");
    }

    public void onClickVirus(View view){
        game_fragment.usePerk("Virus");
    }

    public void onClickTeleportation(View view){
        game_fragment.usePerk("Teleportation");
    }

    public void onClickInvisibility(View view){game_fragment.usePerk("Invisibility");}

    public  void saveGameScore(Player player)
    {
        DatabaseHandlerScore dbHandler = new DatabaseHandlerScore(this);

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

        GameScore gameScore = new GameScore(1, 1, player.getName(), player_type, player.getScore(), player.getDisksEaten(), 1, played_date, GameConstants.PAUSED_STATE);
        dbHandler.addScore(gameScore);

    }

    public  void clearDbScore() {
        DatabaseHandlerScore scoreHandler = new DatabaseHandlerScore(this);
        scoreHandler.clearGameScores();
    }


}

// vim: ts=4 sw=4 et
