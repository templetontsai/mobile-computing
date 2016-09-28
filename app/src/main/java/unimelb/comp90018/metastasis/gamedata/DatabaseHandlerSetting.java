package unimelb.comp90018.metastasis.gamedata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Purathani on 18/08/15.
 */

public class DatabaseHandlerSetting extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "AgarioGame";

    // User table name
    private static final String TABLE_GAME_SETTING_TABLE = "GameSetting";

    // GameSetting Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER__EMAIL = "user_email";
    private static final String KEY_VOLUME= "volume";
    private static final String KEY_ORIENTATION = "orientation";
    private static final String KEY_ADVANCED_AI= "advanced_ai";
    private static final String KEY_ACCELEROMETER = "accelerometer";
    private static final String KEY_GESTURE = "gesture";
    private static final String KEY_SENSORS = "sensors";
    private static final String KEY_CREATED_DATE = "created_date";


    SQLiteDatabase db;
    private Context mContext;


    public DatabaseHandlerSetting(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;

    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_GAME_SETTING_TABLE = "CREATE TABLE " + TABLE_GAME_SETTING_TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "  + KEY_USER_ID + " INTEGER, "
                + KEY_USER__EMAIL + " TEXT, " + KEY_ORIENTATION + " INTEGER, "
                + KEY_ADVANCED_AI + " INTEGER, " + KEY_ACCELEROMETER + " INTEGER, "
                + KEY_GESTURE + " INTEGER, " + KEY_SENSORS + " INTEGER, " + KEY_CREATED_DATE + " DATETIME " + ")";

        db.execSQL(CREATE_GAME_SETTING_TABLE);
        System.out.println(CREATE_GAME_SETTING_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME_SETTING_TABLE);

        // Create tables again
        onCreate(db);
    }

    // Adding Game Setting
    public void addGameSetting(GameSetting gameSetting) {

        ContentValues values = new ContentValues();

        values.put(KEY_USER_ID, gameSetting.getUser_id());
        values.put(KEY_USER__EMAIL, gameSetting.getUser_email());
        values.put(KEY_VOLUME, gameSetting.getVolume());
        values.put(KEY_ORIENTATION, ( gameSetting.isOrientation()) ? 1:0);
        values.put(KEY_ADVANCED_AI, ( gameSetting.isAdvancedAI()) ? 1:0);
        values.put(KEY_ACCELEROMETER, (gameSetting.isAccelerometer()) ? 1:0);
        values.put(KEY_GESTURE, (gameSetting.isGesture()) ? 1:0);
        values.put(KEY_SENSORS, (gameSetting.isSensors()) ? 1:0);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String DateToStr = format.format(gameSetting.getCreatedDate());
        values.put(KEY_CREATED_DATE, DateToStr);

        db = this.getWritableDatabase();

        // Inserting Row
        db.insert(TABLE_GAME_SETTING_TABLE, null, values);
        db.close(); // Closing database connection
    }

    // Getting Setting By useremail
    public List<GameSetting> getGameSettingByEmail(String email) {
        List<GameSetting> settingList = new ArrayList<GameSetting>();
        // Select  Query
        String selectQuery = "SELECT  *  FROM " + TABLE_GAME_SETTING_TABLE + " WHERE USER_EMAIL = '" + email + "' ORDER BY ID DESC LIMIT 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                GameSetting gameSetting = new GameSetting();
                gameSetting.setId(Integer.parseInt(cursor.getString(0)));
                gameSetting.setUser_id(Integer.parseInt(cursor.getString(1)));
                gameSetting.setUser_email(cursor.getString(2));
                gameSetting.setVolume(Integer.parseInt(cursor.getString(3)));
                gameSetting.setOrientation("1".equals(cursor.getString(4)));
                gameSetting.setAdvancedAI("1".equals(cursor.getString(5)));
                gameSetting.setAccelerometer("1".equals(cursor.getString(6)));
                gameSetting.setGesture("1".equals(cursor.getString(7)));
                gameSetting.setSensors("1".equals(cursor.getString(8)));


                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

                Date convertedDate = new Date();
                try {
                    convertedDate = formatter.parse(cursor.getString(9));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                gameSetting.setCreatedDate(convertedDate);

                // Adding setting to list
                settingList.add(gameSetting);
            } while (cursor.moveToNext());
        }

        // return setting list
        return settingList;
    }

}