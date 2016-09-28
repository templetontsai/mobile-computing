package unimelb.comp90018.metastasis.gamedata;
import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;

/**
 * Created by Purathani on 9/9/15.
 */
public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AGARIO_PREFERENCE";

    // All Shared Preferences Keys
    private static final String KEY_LOGIN_USERNAME = "LoginUsername";
    public static final String KEY_LOGIN_USER_ID = "LoginUserIdl";
    public static final String KEY_IS_USER_EXIST = "IsUserExist";
    public static final String KEY_LOGIN_USER_EMAIL = "LoginUserEmail";

    public static final String KEY_VOLUME_SETTING = "Volume";
    public static final String KEY_ORIENTATION_SETTING = "Orientation";
    public static final String KEY_ADVANCEDAI_SETTING = "AdvancedAI";
    public static final String KEY_ACCELEROMETER_SETTING = "Accelerometer";
    public static final String KEY_GESTURE_SETTING = "Gesture";
    public static final String KEY_SENSORS_SETTING = "Sensors";

    public static final String KEY_GAME_STATE = "GameState";

    public static final String GAME_MODE = "GameMode";
    public static final String SERVER_ADDRESS = "ServerAddress";
    public static final Set<String> clientAddresses = new TreeSet<>();
    public static final String CLIENT_ADDRESS = "ClientAddress";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createUserSession(boolean IsUserExist, int LoginUserId, String LoginUsername, String loginUserEmail ) {
        // Storing name in pref
        editor.putBoolean(KEY_IS_USER_EXIST, IsUserExist);
        editor.putInt(KEY_LOGIN_USER_ID, LoginUserId);
        editor.putString(KEY_LOGIN_USERNAME, LoginUsername);
        editor.putString(KEY_LOGIN_USER_EMAIL, loginUserEmail);

        // commit changes
        editor.commit();
    }

    public void createGameSettingSession(GameSetting gameSetting ) {
        // Storing name in pref
        editor.putInt(KEY_VOLUME_SETTING, gameSetting.getVolume());
        editor.putBoolean(KEY_ORIENTATION_SETTING, gameSetting.isOrientation());
        editor.putBoolean(KEY_ADVANCEDAI_SETTING, gameSetting.isAdvancedAI());
        editor.putBoolean(KEY_ACCELEROMETER_SETTING, gameSetting.isAccelerometer());
        editor.putBoolean(KEY_GESTURE_SETTING, gameSetting.isGesture());
        editor.putBoolean(KEY_SENSORS_SETTING, gameSetting.isSensors());

        // commit changes
        editor.commit();
    }

    public void createGameStateSession(String gameState) {
        // Storing name in pref
        editor.putString(KEY_GAME_STATE, gameState);
        // commit changes
        editor.commit();
    }
    /**
     * Get stored session data
     */
    public boolean getUserExistStatus() {

        boolean IsUserExist = pref.getBoolean(KEY_IS_USER_EXIST, false);

        return IsUserExist;

    }
    public String getLoginUsername() {

        String LoginUsername = pref.getString(KEY_LOGIN_USERNAME, "");

        return LoginUsername;

    }
    public String getLoginUserEmail() {

        String LoginUserEmail = pref.getString(KEY_LOGIN_USER_EMAIL, "");

        return LoginUserEmail;

    }
    public int getLoginUserId() {

        int LoginUserId = pref.getInt(KEY_LOGIN_USER_ID, 1);

        return LoginUserId;

    }


    public GameSetting getGameSettingSession() {
        // Storing name in pref
        int volume = pref.getInt(KEY_VOLUME_SETTING, 0);
        boolean orientation = pref.getBoolean(KEY_ORIENTATION_SETTING, false);
        boolean advancedAI = pref.getBoolean(KEY_ADVANCEDAI_SETTING, false);
        boolean accelerometer = pref.getBoolean(KEY_ACCELEROMETER_SETTING, false);
        boolean gesture = pref.getBoolean(KEY_GESTURE_SETTING, false);
        boolean sensors = pref.getBoolean(KEY_SENSORS_SETTING, false);

        GameSetting gameSetting = new GameSetting(1,1,this.getLoginUsername(),volume,orientation,advancedAI,accelerometer,gesture,sensors,null);

        // commit changes
        editor.commit();
        return  gameSetting;
    }

    public String getGameStateSession() {

        String gameState = pref.getString(KEY_GAME_STATE, GameConstants.IN_ACTIVE_STATE);

        return gameState;

    }


    public void createGameMode(String gameMode) {
        editor.putString(GAME_MODE, gameMode);

        // commit changes
        editor.commit();
    }

    public String getGameMode(){
        String gameMode =  pref.getString(GAME_MODE, "");
        return gameMode;
    }

    public void setServerAddress(String address){
        editor.putString(SERVER_ADDRESS, address);
        editor.commit();
    }

    public String getServerAddress(){
        String address = pref.getString(SERVER_ADDRESS, "");
        return address;
    }

    public void setClientAddress(String clientAddress){
        editor.putStringSet(clientAddress, clientAddresses);
        editor.putString(CLIENT_ADDRESS, clientAddress);
        editor.commit();
    }

    public Set<String> getClientAddresses(){
        Set<String> peers = pref.getStringSet("", clientAddresses);
        return peers;
    }

    public String getClientAddress(){
        String clientAddress =  pref.getString(CLIENT_ADDRESS, "");
        return clientAddress;
    }

}
