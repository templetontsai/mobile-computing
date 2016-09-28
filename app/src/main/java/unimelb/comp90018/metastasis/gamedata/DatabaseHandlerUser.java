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

public class DatabaseHandlerUser extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "AgarioGame";

    // User table name
    private static final String TABLE_USER_TABLE= "User";

    // GameScore table name
    private static final String TABLE_GAME_SCORE = "GameScore";

    // UserSetting Table Columns names
    private static final String KEY_USERID = "id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME= "username";
    private static final String KEY_FIRSTNAME= "firstname";
    private static final String KEY_LASTNAME = "last_name";


    private static final String TABLE_GAME_SETTING_TABLE = "GameSetting";

    // GameSetting Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER__EMAIL = "user_email";
    private static final String KEY_ORIENTATION = "orientation";
    private static final String KEY_VOLUME= "volume";
    private static final String KEY_ADVANCED_AI= "advanced_ai";
    private static final String KEY_ACCELEROMETER = "accelerometer";
    private static final String KEY_GESTURE = "gesture";
    private static final String KEY_SENSORS = "sensors";
    private static final String KEY_CREATED_DATE = "created_date";

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


    public DatabaseHandlerUser(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;

    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER_TABLE + "("
                + KEY_USERID + " INTEGER PRIMARY KEY, " + KEY_USERNAME + " TEXT, " + KEY_FIRSTNAME + " TEXT, "
                + KEY_LASTNAME + " TEXT, " + KEY_EMAIL + " TEXT, " + KEY_CREATED_DATE +" DATETIME " + ")";

        String CREATE_GAME_SETTING_TABLE = "CREATE TABLE " + TABLE_GAME_SETTING_TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "  + KEY_USER_ID + " INTEGER, "
                + KEY_USER__EMAIL + " TEXT, " + KEY_VOLUME + " INTEGER, " + KEY_ORIENTATION + " INTEGER, "
                + KEY_ADVANCED_AI + " INTEGER, " + KEY_ACCELEROMETER + " INTEGER, "
                + KEY_GESTURE + " INTEGER, " + KEY_SENSORS + " INTEGER, " + KEY_CREATED_DATE + " DATETIME " + ")";

        String CREATE_SCORE_TABLE = "CREATE TABLE " + TABLE_GAME_SCORE + "("
                + KEY_GID + " INTEGER PRIMARY KEY, " +  KEY_GAME_ID + " INTEGER, "
                + KEY_PLAYER_NAME + " TEXT, " + KEY_PLAYER_TYPE + " TEXT, "
                + KEY_SCORE + " FLOAT, " + KEY_DISKS_EATEN + " INTEGER, "
                + KEY_PLAYED_TIME + " FLOAT, "
                + KEY_PLAYED_DATE + " DATETIME, " + KEY_GAME_STATE + " TEXT " + ")";



        db.execSQL(CREATE_GAME_SETTING_TABLE);
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_SCORE_TABLE);


    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_TABLE);

        // Create tables again
        onCreate(db);
    }

    // Adding new User
    public long addUser(UserSetting userSetting) {

        ContentValues values = new ContentValues();

        values.put(KEY_USERNAME, userSetting.getUsername());

        values.put(KEY_FIRSTNAME, userSetting.getFirstname());

        values.put(KEY_LASTNAME, userSetting.getLastname());

        values.put(KEY_EMAIL, userSetting.getEmail());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String DateToStr = format.format(userSetting.getCreated_date());
        values.put(KEY_CREATED_DATE, DateToStr);

        db = this.getWritableDatabase();

        // Inserting Row
        long last_key =  db.insert(TABLE_USER_TABLE , null, values);
        db.close(); // Closing database connection

        return last_key;
    }

    // Getting User By username
    public List<UserSetting> getUserByEmail(String email) {
        List<UserSetting> userList = new ArrayList<UserSetting>();
        // Select  Query
        String selectQuery = "SELECT  * FROM " + TABLE_USER_TABLE + " WHERE EMAIL = '" + email + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UserSetting userSetting = new UserSetting();
                userSetting.setId(Integer.parseInt(cursor.getString(0)));
                userSetting.setUsername(cursor.getString(1));
                userSetting.setFirstname(cursor.getString(2));
                userSetting.setLastname(cursor.getString(3));
                userSetting.setEmail(cursor.getString(4));

                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

                Date convertedDate = new Date();
                try {
                    convertedDate = formatter.parse(cursor.getString(4));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                userSetting.setCreated_date(convertedDate);

                // Adding score to list
                userList.add(userSetting);
            } while (cursor.moveToNext());
        }

        // return user list
        return userList;
    }

    public List<UserSetting> getUserList() {
        List<UserSetting> userList = new ArrayList<UserSetting>();
        // Select  Query
        String selectQuery = "SELECT  * FROM " + TABLE_USER_TABLE ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UserSetting userSetting = new UserSetting();
                userSetting.setId(Integer.parseInt(cursor.getString(0)));
                userSetting.setUsername(cursor.getString(1));
                userSetting.setFirstname(cursor.getString(2));
                userSetting.setLastname(cursor.getString(3));
                userSetting.setEmail(cursor.getString(4));

                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

                Date convertedDate = new Date();
                try {
                    convertedDate = formatter.parse(cursor.getString(5));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                userSetting.setCreated_date(convertedDate);

                // Adding score to list
                userList.add(userSetting);
            } while (cursor.moveToNext());
        }

        // return user list
        return userList;
    }


}