import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Florian Speer on 17.07.2017.
 */

public class OrientationExtraction implements SensorEventListener {
    SensorManager sensorManager;
    Sensor sensor;
    private Double heading = null;

    OrientationExtraction(MainActivity context) {
        // Getting the system sensor 'rotation vector'
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    void startOrientationUpdates() {
        // Registering the listener
        sensorManager.registerListener(this, sensor, 1000);
    }

    void stopOrientationUpdates() {
        // Unregistering the listener
        sensorManager.unregisterListener(this, sensor);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Getting the devices orientation when it has changed
        if (event.sensor.getType()==Sensor.TYPE_ROTATION_VECTOR) {
            // Extracting the sensor data
            float[] rotationMatrix = new float[9];
            float[] orientation = new float[3];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            SensorManager.getOrientation(rotationMatrix, orientation);

            // Extracting the horizontal heading
            // The heading is a double value between -180° and +180°
            // North: 0°, East: 90°, West: -90°, South: either 180° or -180°
            heading = Math.toDegrees(orientation[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    Double getHeading() {
        // Returning the current heading of the device
        return heading;
    }
}
