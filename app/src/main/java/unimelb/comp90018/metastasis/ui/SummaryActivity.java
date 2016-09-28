package unimelb.comp90018.metastasis.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import unimelb.comp90018.metastasis.gamedata.DatabaseHandlerScore;
import unimelb.comp90018.metastasis.gamedata.GameScore;
import unimelb.comp90018.metastasis.gamedata.SessionManager;

/**
 * This activity is responsible for displaying the summary of the match to the player
 */
public class SummaryActivity extends ImmersiveActivity {
    private static final String TAG = "SummaryActivity";

    SessionManager sessionManager;
    ListView list;
    ListViewAdapter listviewadapter;
    List<GameScore> scoreList = new ArrayList<GameScore>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_summary);

        DatabaseHandlerScore scoreHandler = new DatabaseHandlerScore(this);
        scoreList = scoreHandler.getLatestGameScore();

        // Locate the ListView in listview_main.xml
        list = (ListView) findViewById(R.id.listviewSummary);
        // Pass results to ListViewAdapter Class
        listviewadapter = new ListViewAdapter(this, R.layout.listview_item, scoreList);
        // Binds the Adapter to the ListView
        list.setAdapter(listviewadapter);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

    }

    public void onClickHome(View view){
        Intent homeActivityIntent = new Intent(this, MainActivity.class);
        startActivity(homeActivityIntent);
        finish();
    }
}
