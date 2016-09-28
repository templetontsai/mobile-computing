package unimelb.comp90018.metastasis.gamedata;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import unimelb.comp90018.metastasis.Player;

/**
 * Created by Purathani on 18/08/15.
 */

public class DatabaseHandlerScore extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "AgarioGame";

    // GameScore table name
    private static final String TABLE_GAME_SCORE = "GameScore";

    // GameScore table name
    private static final String TABLE_USER_TABLE= "User";

    // GameScore Table Columns names
    private static final String KEY_GID = "id";
    private static final String KEY_GAME_ID = "game_id";
    private static final String KEY_PLAYER_NAME = "player_name";
    private static final String KEY_PLAYER_TYPE = "player_type";
    private static final String KEY_SCORE = "score";
    private static final String KEY_DISKS_EATEN = "disks_eaten";
    private static final String KEY_PLAYED_TIME = "played_time";
    private static final String KEY_PLAYED_DATE = "played_date";
    private static final String KEY_GAME_STATE = "game_state";


    SQLiteDatabase db;
    private Context mContext;


    public DatabaseHandlerScore(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;

    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SCORE_TABLE = "CREATE TABLE " + TABLE_GAME_SCORE + "("
                + KEY_GID + " INTEGER PRIMARY KEY, " +  KEY_GAME_ID + " INTEGER, "
                + KEY_PLAYER_NAME + " TEXT, " + KEY_PLAYER_TYPE + " TEXT, "
                + KEY_SCORE + " FLOAT, " + KEY_DISKS_EATEN + " INTEGER, "
                + KEY_PLAYED_TIME + " FLOAT, "
                + KEY_PLAYED_DATE + " DATETIME, " + KEY_GAME_STATE + " TEXT " + ")";


        db.execSQL(CREATE_SCORE_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME_SCORE);

        // Create tables again
        onCreate(db);
    }

    /**
     * Create, Read, Update, Delete Operations
     */

    // Adding new score
    public void addScore(GameScore score) {

        ContentValues values = new ContentValues();
        values.put(KEY_GAME_ID, score.getGame_id());
        values.put(KEY_PLAYER_NAME, score.getPlayer_name());
        values.put(KEY_PLAYER_TYPE, score.getPlayer_type());
        values.put(KEY_SCORE, score.getScore());
        values.put(KEY_DISKS_EATEN, score.getDisks_eaten());
        values.put(KEY_PLAYED_TIME, score.getPlayed_time());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String DateToStr = format.format(score.getPlayed_date());
        values.put(KEY_PLAYED_DATE, DateToStr);
        values.put(KEY_GAME_STATE, score.getGame_state());

        db = this.getWritableDatabase();

        // Inserting Row
        db.insert(TABLE_GAME_SCORE, null, values);
        db.close(); // Closing database connection
    }

    // Getting single Score
    GameScore getScore(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_GAME_SCORE, new String[]{
                        KEY_GID, KEY_GAME_ID, KEY_PLAYER_NAME, KEY_PLAYER_TYPE, KEY_SCORE, KEY_DISKS_EATEN, KEY_PLAYED_TIME, KEY_PLAYED_TIME, KEY_GAME_STATE}, KEY_GID + "=?",
                new String[]{String.valueOf(id)}, null,  null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        Date convertedDate = new Date();
        try {
            convertedDate = formatter.parse(cursor.getString(7));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        GameScore score = new GameScore(Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(1)),
                cursor.getString(2), cursor.getString(3), Float.parseFloat(cursor.getString(4)), Integer.parseInt(cursor.getString(5)), Float.parseFloat(cursor.getString(6)),convertedDate, cursor.getString(8));
        // return GameScore
        return score;
    }

    // Getting All Score
    public List<GameScore> getAllScores() {
        List<GameScore> scoreList = new ArrayList<GameScore>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_GAME_SCORE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                GameScore score = new GameScore();
                score.setID(Integer.parseInt(cursor.getString(0)));
                score.setGame_id(Integer.parseInt(cursor.getString(1)));
                score.setPlayer_name(cursor.getString(2));
                score.setPlayer_type(cursor.getString(3));
                score.setScore(Float.parseFloat(cursor.getString(4)));
                score.setID(Integer.parseInt(cursor.getString(5)));
                score.setPlayed_time(Float.parseFloat(cursor.getString(6)));

                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

                Date convertedDate = new Date();
                try {
                    convertedDate = formatter.parse(cursor.getString(7));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                score.setPlayed_date(convertedDate);
                score.setGame_state(cursor.getString(8));

                // Adding score to list
                scoreList.add(score);
            } while (cursor.moveToNext());
        }

        // return score list
        return scoreList;
    }

    // Updating single score
    public int updateScore(GameScore score) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GAME_ID, score.getGame_id());
        values.put(KEY_PLAYER_NAME, score.getPlayer_name());
        values.put(KEY_PLAYER_TYPE, score.getPlayer_type());
        values.put(KEY_SCORE, score.getScore());
        values.put(KEY_DISKS_EATEN, score.getDisks_eaten());
        values.put(KEY_PLAYED_TIME, score.getPlayed_time());
        values.put(KEY_PLAYED_DATE, score.getPlayed_date().toString());
        values.put(KEY_GAME_STATE, score.getGame_state());

        // updating row
        return db.update(TABLE_GAME_SCORE, values, KEY_GID + " = ?",
                new String[]{String.valueOf(score.getID())});
    }

    // Deleting single score
    public void deleteScore(GameScore score) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GAME_SCORE, KEY_GID + " = ?", new String[]{String.valueOf(score.getID())});
        db.close();
    }

    // Clear Score table
    public void clearGameScores() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_GAME_SCORE);
        //db.delete(TABLE_GAME_SCORE, KEY_GID + " = ?", new String[]{String.valueOf(score.getID())});
        db.close();
    }



    // Getting scores Count
    public int getScoresCount() {
        String countQuery = "SELECT  * FROM " + TABLE_GAME_SCORE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

    // Getting Latest Score
    public List<GameScore> getLatestGameScore() {
        List<GameScore> scoreList = new ArrayList<GameScore>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_GAME_SCORE ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                GameScore score = new GameScore();
                score.setID(Integer.parseInt(cursor.getString(0)));
                score.setGame_id(Integer.parseInt(cursor.getString(1)));
                score.setPlayer_name(cursor.getString(2));
                score.setPlayer_type(cursor.getString(3));
                score.setScore(Double.parseDouble(cursor.getString(4)));
                score.setDisks_eaten(Double.parseDouble(cursor.getString(5)));
                score.setPlayed_time(Float.parseFloat(cursor.getString(6)));

                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

                //Date convertedDate = new Date();
                try {
                    Date convertedDate  = formatter.parse(cursor.getString(7));
                    score.setPlayed_date(convertedDate);

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
             //   score.setPlayed_date(convertedDate);
                score.setGame_state(cursor.getString(8));

                // Adding score to list
                scoreList.add(score);
            } while (cursor.moveToNext());
        }

        Collections.sort(scoreList, new Comparator<GameScore>() {
            public int compare(GameScore one, GameScore other) {
                return Double.compare(other.getScore(), one.getScore());
            }
        });
        // return score list
        return scoreList;
    }
}