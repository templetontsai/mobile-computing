package unimelb.comp90018.metastasis.ui;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


import unimelb.comp90018.metastasis.gamedata.DatabaseHandlerScore;
import unimelb.comp90018.metastasis.gamedata.GameScore;


/**
 * This is GameCenterActivity it has immersive mode and it is the main entry for user to access their achievement/statistics
 */
public class GameCenterActivity extends ImmersiveActivity {

    ListView list;
    ListViewAdapter listviewadapter;
    List<GameScore> scoreList = new ArrayList<GameScore>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_center);

        DatabaseHandlerScore scoreHandler = new DatabaseHandlerScore(this);
        scoreList = scoreHandler.getLatestGameScore();

        // Locate the ListView in listview_main.xml
        list = (ListView) findViewById(R.id.listviewScore);
        // Pass results to ListViewAdapter Class
        listviewadapter = new ListViewAdapter(this, R.layout.listview_item, scoreList);
        // Binds the Adapter to the ListView
        list.setAdapter(listviewadapter);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

    }

    public void onClickHome(View view) {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }
}
