package unimelb.comp90018.metastasis.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import unimelb.comp90018.metastasis.gamedata.DatabaseHandlerScore;
import unimelb.comp90018.metastasis.gamedata.GameConstants;
import unimelb.comp90018.metastasis.gamedata.SessionManager;

/**
 * This is PauseActivity and inherent from ImmersiveActivity for immersive mode. It is to show options when user pause from game activity
 */
public class PauseActivity extends ImmersiveActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pause);
    }

    public void onClickResume(View view) {

        Intent gameActivityIntent = new Intent(this, GameActivity.class);
        startActivity(gameActivityIntent);
        finish();
    }

    public void onClickRestart(View view) {

        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }

    public void onClickSetting(View view) {
        Intent settingActivityIntent = new Intent(this, SettingActivity.class);
        Bundle bundle = new Bundle();
        //activity_id, 0 is main, 1 is pause
        bundle.putInt("activity_id", 1);
        settingActivityIntent.putExtras(bundle);
        startActivity(settingActivityIntent);
        finish();
    }

    public void onClickExit(View view) {
        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.createGameStateSession(GameConstants.IN_ACTIVE_STATE);
        clearDbScore();
        finish();
    }


    public  void clearDbScore() {
        DatabaseHandlerScore scoreHandler = new DatabaseHandlerScore(this);
        scoreHandler.clearGameScores();
    }

}
