package unimelb.comp90018.metastasis.ui;


import android.content.Intent;

import android.content.SharedPreferences;

import android.os.Bundle;

import android.os.Handler;


import java.util.List;

import unimelb.comp90018.metastasis.gamedata.DatabaseHandlerScore;
import unimelb.comp90018.metastasis.gamedata.DatabaseHandlerSetting;
import unimelb.comp90018.metastasis.gamedata.DatabaseHandlerUser;
import unimelb.comp90018.metastasis.gamedata.GameConstants;
import unimelb.comp90018.metastasis.gamedata.GameSetting;
import unimelb.comp90018.metastasis.gamedata.SessionManager;
import unimelb.comp90018.metastasis.gamedata.UserSetting;



/**
 * This is SplashScreenActivity it has immersive mode inherent from ImmersiveActivity
 */
public class SplashScreenActivity extends ImmersiveActivity {

    //TODO the time should be changed accordingly with the loading time for settings later on
    private static final int SPLASH_TIME = 5000;


    SessionManager session;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                init_session();

                Intent mainActivityIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                SplashScreenActivity.this.startActivity(mainActivityIntent);
                SplashScreenActivity.this.finish();


            }
        }, SPLASH_TIME);

    }

    // Initialize session variables
    public void init_session()
    {
        DatabaseHandlerUser userHandler = new DatabaseHandlerUser(this);
        List<UserSetting> userList = userHandler.getUserList();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("GAME_PREFERENCES", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        boolean userInDatabase = false;
        String loginUserName = "";
        String loginUserEmail = "";
        int loginUserId = 1;
        if(!userList.isEmpty())
        {
            userInDatabase = true;
            loginUserName = userList.get(0).getUsername();
            loginUserEmail = userList.get(0).getEmail();
            loginUserId = userList.get(0).getId();

        }

        DatabaseHandlerSetting settingHandler = new DatabaseHandlerSetting(this);
        List<GameSetting> settingList = settingHandler.getGameSettingByEmail(loginUserEmail);

        // Session Manager
        session = new SessionManager(getApplicationContext());
        session.createUserSession(userInDatabase, loginUserId, loginUserName, loginUserEmail);

        if(!settingList.isEmpty()) {
            session.createGameSettingSession(settingList.get(0));
        }

        session.createGameStateSession(GameConstants.IN_ACTIVE_STATE);
        DatabaseHandlerScore scoreHandler = new DatabaseHandlerScore(this);
        scoreHandler.clearGameScores();
    }

}
