package unimelb.comp90018.metastasis.gamedata;

import java.util.Date;

/**
 * Created by Purathani on 26/08/15.
 */
public class GameSetting {

    //private variables
    int id;
    int user_id;
    String user_email;
    int volume;
    boolean orientation;
    boolean advancedAI;
    boolean accelerometer;
    boolean gesture;
    boolean sensors;
    Date createdDate;


    // Empty constructor
    public GameSetting() {

    }

    // constructor
    public GameSetting(int _id, int _user_id, String _user_email, int _volume, boolean _orientation, boolean _advancedAI, boolean _accelerometer, boolean _gesture, boolean _sensors, Date _create_date) {
        this.id = _id;
        this.user_id = _user_id;
        this.user_email = _user_email;
        this.volume = _volume;
        this.orientation = _orientation;
        this.advancedAI = _advancedAI;
        this.accelerometer = _accelerometer;
        this.gesture = _gesture;
        this.sensors = _sensors;
        this.createdDate = _create_date;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public int getVolume() {return volume; }

    public void setVolume(int volume) { this.volume = volume; }

    public boolean isOrientation() {
        return orientation;
    }

    public void setOrientation(boolean orientation) {
        this.orientation = orientation;
    }

    public boolean isAdvancedAI() {
        return advancedAI;
    }

    public void setAdvancedAI(boolean advancedAI) {
        this.advancedAI = advancedAI;
    }

    public boolean isAccelerometer() {
        return accelerometer;
    }

    public void setAccelerometer(boolean accelerometer) {
        this.accelerometer = accelerometer;
    }

    public boolean isGesture() {
        return gesture;
    }

    public void setGesture(boolean gesture) {
        this.gesture = gesture;
    }

    public boolean isSensors() {
        return sensors;
    }

    public void setSensors(boolean sensors) {
        this.sensors = sensors;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    }
