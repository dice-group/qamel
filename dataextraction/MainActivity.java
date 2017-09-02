package eu.qamel.dataextraction;

import android.Manifest;

import android.content.pm.PackageManager;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 10;
    private static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 10;

    TextView textView;
    boolean temp = false;
    DataExtraction dataExtraction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // requesting the needed permissions at app start
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALENDAR},
                    MY_PERMISSIONS_REQUEST_READ_CALENDAR);
        }


        textView = (TextView) findViewById(R.id.outputPosition);

        dataExtraction = new DataExtraction(this);
    }

    public void getData(View view) {

//        if(temp == false) {
//            dataExtraction.start();
//            temp = true;
//        } else {
//            dataExtraction.stop();
//            temp = false;
//        }
        dataExtraction.getData();

    }
}
