package unimelb.comp90018.metastasis.ui;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.View;

import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import unimelb.comp90018.metastasis.gamedata.DatabaseHandlerScore;
import unimelb.comp90018.metastasis.gamedata.DatabaseHandlerUser;
import unimelb.comp90018.metastasis.gamedata.GameConstants;
import unimelb.comp90018.metastasis.gamedata.SessionManager;
import unimelb.comp90018.metastasis.gamedata.UserSetting;


/**
 * This is MainActivity and entry for user and it has immersive mode inherent from ImmersiveActivity
 * There are two fragments operating in this Activity for main user panel and user registration panel
 */
public class MainActivity extends ImmersiveActivity {
    private static final String TAG = "MainActivity";

    SessionManager session;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        MainUserPanelFragment mainUserFragment = new MainUserPanelFragment();
        MainRegistrationPanelFragment mainUserRegFragment = new MainRegistrationPanelFragment();
        //TODO Have the database result update from SplashActivity to decide which fragment to show


        // Checking session value if user already registered or not exists
        boolean userInDatabase = false;
        session = new SessionManager(getApplicationContext());
        userInDatabase = session.getUserExistStatus();


        if(userInDatabase) {
            fragmentTransaction.add(R.id.main_relativeLayout, mainUserFragment);
        } else {
            fragmentTransaction.add(R.id.main_relativeLayout, mainUserRegFragment);
        }

        fragmentTransaction.commit();
        setContentView(R.layout.activity_main);

    }


    public void onClickPlay(View view) {
        String gameMode = "SinglePlayer";
        session.createGameMode(gameMode);

        clearDbScore();
        session.createGameStateSession(GameConstants.RUNNING_STATE);

        Log.d(TAG, "Starting GameActivity");
        Intent gameActivityIntent = new Intent(this, GameActivity.class);
        startActivity(gameActivityIntent);
        finish();

    }

    public void onClickMultiplayer(View view){
        Log.d(TAG, "Starting Multiplayer Activity");
        Intent multiplayerIntent = new Intent(this, MultiplayerActivity.class);
        startActivity(multiplayerIntent);
        finish();
    }

    public void onClickSettings(View view) {
        Intent settingActivityIntent = new Intent(this, SettingActivity.class);
        Bundle bundle = new Bundle();
        //activity_id, 0 is main, 1 is pause
        bundle.putInt("activity_id", 0);
        settingActivityIntent.putExtras(bundle);
        startActivity(settingActivityIntent);
        finish();

    }

    public void onClickGameCenter(View view) {
        Intent gameCenterActivityIntent = new Intent(this, GameCenterActivity.class);
        startActivity(gameCenterActivityIntent);
        finish();

    }

    public void onClickExit(View view) {
        clearDbScore();
        session.createGameStateSession(GameConstants.IN_ACTIVE_STATE);

        finish();
    }

    public  void clearDbScore() {
        DatabaseHandlerScore scoreHandler = new DatabaseHandlerScore(this.getApplicationContext());
        scoreHandler.clearGameScores();
    }




}
