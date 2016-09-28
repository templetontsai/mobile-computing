package unimelb.comp90018.metastasis.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * This is LeaderBoardActivity it has immersive mode and it is the page to showcase the ranking among all the users
 */
public class LeaderBoardActivity extends ImmersiveActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
    }

    public void onClickBack(View view) {
        Intent gameCenterActivityIntent = new Intent(this, GameCenterActivity.class);
        startActivity(gameCenterActivityIntent);
        finish();
    }
}
