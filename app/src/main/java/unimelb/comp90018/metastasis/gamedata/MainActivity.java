package unimelb.comp90018.metastasis.gamedata;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import unimelb.comp90018.metastasis.ui.R;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);

    }

    public void addNewGameScore (View view) {
        DatabaseHandlerScore dbHandler = new DatabaseHandlerScore(this);

        Date datePlayed = new Date();
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
        try {
            datePlayed = formater.parse(datePlayed.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        GameScore gameScore = new GameScore(1,1, "Player2", GameConstants.HUMAN_PLAYER, 0,15,14,datePlayed, "Active");

        dbHandler.addScore(gameScore);
        textView.setText("success");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
