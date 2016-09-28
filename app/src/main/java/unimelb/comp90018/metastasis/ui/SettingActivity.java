package unimelb.comp90018.metastasis.ui;



import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import unimelb.comp90018.metastasis.Utils;
import unimelb.comp90018.metastasis.gamedata.DatabaseHandlerSetting;
import unimelb.comp90018.metastasis.gamedata.DatabaseHandlerUser;
import unimelb.comp90018.metastasis.gamedata.GameSetting;
import unimelb.comp90018.metastasis.gamedata.SessionManager;


/**
 * This is SettingActivity and inherent from ImmersiveActivity for immersive mode. It is to for game setting
 */
public class SettingActivity extends ImmersiveActivity {

    // Initialize components
    private SeekBar sound_seekBar;
    private Switch orientation_switch;
    private Switch advanced_ai_switch;
    private Switch accelerometer_switch;
    private Switch gesture_switch;
    private Switch sensors_switch;
    SessionManager session;
    private int activity_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sound_seekBar = (SeekBar) this.findViewById(R.id.sound_seekBar);
        orientation_switch = (Switch) this.findViewById(R.id.orientation_switch);
        advanced_ai_switch = (Switch) this.findViewById(R.id.advanced_ai_switch);
        accelerometer_switch = (Switch) this.findViewById(R.id.accelerometer_switch);
        gesture_switch = (Switch) this.findViewById(R.id.gesture_switch);
        sensors_switch = (Switch) this.findViewById(R.id.Sensors_switch);

        //TODO make sure all the behaviour of codes still working fine
        if(!Utils.isSensorSupported(this, Sensor.TYPE_LIGHT))
            sensors_switch.setEnabled(false);

        if(!Utils.isSensorSupported(this, Sensor.TYPE_ACCELEROMETER)) {
            accelerometer_switch.setEnabled(false);

        }

        loadGameSetting();

        Bundle bundle = getIntent().getExtras();
        activity_id = bundle.getInt("activity_id");
        ImageButton pauseImageButton = (ImageButton)findViewById(R.id.pause_imageButton);

        if(activity_id == 0) {
            pauseImageButton.setImageResource(R.mipmap.home_96);

        } else if(activity_id == 1) {
            pauseImageButton.setImageResource(R.mipmap.undo_96);

        }

    }

    public void loadGameSetting()
    {
        session = new SessionManager(getApplicationContext());
        GameSetting gameSetting = session.getGameSettingSession();

        sound_seekBar.setProgress(gameSetting.getVolume());
        orientation_switch.setChecked(gameSetting.isOrientation());
        advanced_ai_switch.setChecked(gameSetting.isAdvancedAI());
        accelerometer_switch.setChecked(gameSetting.isAccelerometer());
        gesture_switch.setChecked(gameSetting.isGesture());
        sensors_switch.setChecked(gameSetting.isSensors());
    }

    public void onClickHome(View view) {
        DatabaseHandlerSetting dbHandler = new DatabaseHandlerSetting(this);

        Date created_date = new Date();
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
        try {
            created_date = formater.parse(created_date.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int volume = sound_seekBar.getProgress();
        boolean orientation = false;//orientation_switch.isChecked();
        boolean advancedAI = advanced_ai_switch.isChecked();
        boolean accelerometer = accelerometer_switch.isChecked();
        boolean gesture = false; //gesture_switch.isChecked();
        boolean sensors = sensors_switch.isChecked();

        session = new SessionManager(getApplicationContext());
        String userEmail = session.getLoginUserEmail();
        int user_id = session.getLoginUserId();

        // Add game setting values in database

        GameSetting gameSetting = new GameSetting(1, user_id, userEmail, volume,orientation, advancedAI, accelerometer, gesture, sensors, created_date);
        dbHandler.addGameSetting(gameSetting);

            // Session Manager - vreate game setting session values
            session = new SessionManager(this);
            session.createGameSettingSession(gameSetting);


        if(activity_id == 0) {
            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            startActivity(mainActivityIntent);
            finish();

        } else if(activity_id == 1) {
            Intent pauseActivityIntent = new Intent(this, PauseActivity.class);
            startActivity(pauseActivityIntent);
            finish();

        }


    }

    public void onClickCredit(View view) {
        Intent creditActivityIntent = new Intent(this, CreditActivity.class);
        startActivity(creditActivityIntent);
    }

}
