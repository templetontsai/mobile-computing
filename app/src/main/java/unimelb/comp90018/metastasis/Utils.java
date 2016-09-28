package unimelb.comp90018.metastasis;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.List;

/**
 *  Class to define various constants used in the game.
 */
public class Utils {
    // radius of a disk when it is first spawned.
    public static final double INITIAL_DISK_SIZE = 10;

    // slowest maximum speed. As disks get really big, this is how fast
    // they can move, in pixels per iteration.
    public static final double DISK_MIN_SPEED = 10;

    // number of pixels per unit of disk/food/virus radius.
    public static final double PIXELS_PER_PT = 3;

    // disks bigger than this size, in terms of radius units, can be popped
    // by a virus.
    public static final double VIRUS_POP_THRESHOLD = 200;

    public static final double AI_SPEED = 1;
    public static final double DISK_SPEED = 100;
    public static final double DISK_SIZE_DIFFERENCE = 20;

    public static final double EPSILON = 0.00001;

    /**
     *  Randomly generates a colour.
     */
    public static int randomColour () {
        int red, green, blue;
        int colour = 0xFF000000;

        red = (int) (Math.random () * 255);
        green = (int) (Math.random () * 255);
        blue = (int) (Math.random () * 255);

        colour |= (red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF);

        return colour;
    }

    /**
     *  checking if the sensor is supported in the device or not
     */
    public static boolean isSensorSupported(Context mContext,int sensorType) {

        boolean result = false;
        SensorManager mSensorManager = (SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE);
        List<Sensor> mSensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for(Sensor s : mSensorList){
            if(s.getType() == sensorType) {
                result = true;
            }
        }

        return result;

    }
}

// vim: ts=4 sw=4 et
