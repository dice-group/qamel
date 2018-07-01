package de.qa.qa.dataextraction;

/**
 * Created by paramjot on 6/29/18.
 */


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.qa.R;

import static android.content.Context.SENSOR_SERVICE;

public class OrientationExtraction extends Fragment implements SensorEventListener {
    SensorManager sensorManager;
    Sensor sensor;
    private View view;
    public Double heading;
    TextView OrientationText;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_orientation, container, false);
        OrientationText = (TextView) view.findViewById(R.id.orientation);
        // Getting the system sensor 'rotation vector'
        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        startOrientationUpdates();
        return view;
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
            OrientationText.setText(heading.toString());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    Double getHeading() {
        startOrientationUpdates();
        // Returning the current heading of the device
        return heading;
    }
}
