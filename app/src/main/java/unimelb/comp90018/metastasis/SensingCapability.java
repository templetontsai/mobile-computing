package unimelb.comp90018.metastasis;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;



import java.util.List;
import java.util.Random;

import unimelb.comp90018.metastasis.ui.MainGamePanel;


/**
 * This class defines all the sensor related codes such as ambient light sensor and accelerometer and is a singleton
 */
public class SensingCapability implements SensorEventListener{

    public static final String TAG = "SensingCapability";

    private SensorManager mSensorManager = null;
    private List<Sensor> mSensorList;
    private Context mContext = null;
    private MainGamePanel mView = null;
    private static SensingCapability instance = null;

    private static final int MAX_LUX_SENSITIVITY = 2000;
    private static final int INIT_LUX_LEVEL = 100;
    private int lightLevel[] = new int[5];


    private HumanPlayer mHumanPlayer = null;
    private final static double X_ACC_MOVING_POSITIVE_THRESHOLD = 1.0f;
    private final static double X_ACC_MOVING_NEGATIVE_THRESHOLD = -1.0f;
    private final static double Y_ACC_MOVING_POSITIVE_THRESHOLD = 1.0f;
    private final static double Y_ACC_MOVING_NEGATIVE_THRESHOLD = -1.0f;
    private final static int COORDINATE = 100;
    private double xPos = 0f;
    private double yPos = 0f;
    private int direction = 0;


    protected SensingCapability() {}

    public static SensingCapability getInstance() {
        if(instance == null) {
            instance = new SensingCapability();
        }
        return instance;
    }

    public boolean init(Context context, MainGamePanel view) {

        boolean result = false;

        if(context != null && view != null) {

            mContext = context;
            mView = view;
            initSensors();

        }
        return result;
    }

    public boolean registerSensors(int sensorType) {

        boolean result = false;

        if(mSensorList != null) {

            for(Sensor s : mSensorList){
                if(s.getType() == sensorType && sensorType == Sensor.TYPE_LIGHT) {

                    result = mSensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
                    //Init lightLevel
                    initLightLevel((int)s.getMaximumRange());
                } else if(s.getType() == sensorType && sensorType == Sensor.TYPE_ACCELEROMETER) {
                    result = mSensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_GAME);
                }
            }
        }

        return result;
    }

    private void initLightLevel(int maxRange) {
        for(int i = 0; i < lightLevel.length; i++) {
            Log.d(TAG, "Max = " + maxRange);
            if(MAX_LUX_SENSITIVITY <= maxRange)
                lightLevel[i] = INIT_LUX_LEVEL + (MAX_LUX_SENSITIVITY/lightLevel.length) * i;
            else
                lightLevel[i] = INIT_LUX_LEVEL + (maxRange/lightLevel.length) * i;
            Log.d(TAG, "lightLevel = " + lightLevel[i]);
        }
    }

    public void unregisterSensors() {

        mSensorManager.unregisterListener(this);
    }



    private void initSensors() {

        mSensorManager = (SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE);
        mSensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
    }

    public void setPlayer(HumanPlayer mHumanPlayer) {
        this.mHumanPlayer = mHumanPlayer;

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;


        switch(sensor.getType()){
            case Sensor.TYPE_LIGHT:
                int lux = (int)event.values[0];
                int index = 0;
                for(int i = 0; i < lightLevel.length; i++) {

                    if(i+1 < lightLevel.length) {
                        if (lux > lightLevel[i] && lux < lightLevel[i + 1])
                            index = i;
                    } else {
                        if(lux > lightLevel[i])
                            index = i;
                    }
                }

                mView.setLightLevel(index);



                break;

            case Sensor.TYPE_ACCELEROMETER:
                //TODO Improve this hardcoded with a better algorithm to map acc vector to simulate coordinates as touch pos

                double xAcc = event.values[0];
                double yAcc = event.values[1];
                double xMax = mView.getScreenWidth();
                double yMax = mView.getScreenHeight();
                Random random = new Random();



                if((xAcc >= X_ACC_MOVING_POSITIVE_THRESHOLD || xAcc <= X_ACC_MOVING_NEGATIVE_THRESHOLD)
                            || (yAcc >= Y_ACC_MOVING_POSITIVE_THRESHOLD || yAcc <= Y_ACC_MOVING_NEGATIVE_THRESHOLD)) {


                    if(yAcc <= -1.5 && xAcc <= 0.0) {
                        //Left

                        if(direction != 1) {
                                Log.d(TAG, "Left");
                                xPos = xMax / 2 - random.nextInt(COORDINATE);
                                yPos = yMax / 2;
                                direction = 1;
                                mHumanPlayer.onAccSensorChanged(xPos, yPos);
                            }
                        } else if(yAcc >= 1.5 && xAcc >= 0.0) {
                            //Right

                            if(direction != 2) {
                                Log.d(TAG, "Right");
                                xPos = xMax/2 + random.nextInt(COORDINATE);
                                yPos = yMax/2;
                                direction = 2;
                                mHumanPlayer.onAccSensorChanged(xPos, yPos);
                            }

                        } else if(xAcc <= -1.5 && yAcc <= 0.0) {
                            //Up
                            if(direction != 3) {
                                Log.d(TAG, "Up");
                                xPos = xMax/2;
                                yPos = yMax/2 - random.nextInt(COORDINATE);
                                direction = 3;
                                mHumanPlayer.onAccSensorChanged(xPos, yPos);
                            }

                        } else if(xAcc >= 1.0 && yAcc <= 0.0) {
                            //Down
                            if(direction != 4) {
                                Log.d(TAG, "Down");
                                xPos = xMax/2;
                                yPos = yMax/2 + random.nextInt(COORDINATE);
                                direction = 4;
                                mHumanPlayer.onAccSensorChanged(xPos, yPos);
                            }

                        }


                }


                break;

        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
