package eu.qamel.dataextraction;

import android.location.Location;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Florian on 17.07.2017.
 */

public class DataExtraction {
    private final LocationExtraction locationExtraction;
    private final OrientationExtraction orientationExtraction;
    private final ContactsExtraction contactsExtraction;
    private final CalendarExtraction calendarExtraction;
    MainActivity context;

    DataExtraction(MainActivity context) {
        this.context = context;
        // Initialize extraction of location + orientation
        locationExtraction = new LocationExtraction(context);
        orientationExtraction = new OrientationExtraction(context);

        // Initialize contact extraction

        contactsExtraction = new ContactsExtraction(context);

        // Initialize calendar extraction
        calendarExtraction = new CalendarExtraction(context);

        start();
    }

    void start() {
        // Start location and orientation updates
        locationExtraction.startLocationUpdates();
        orientationExtraction.startOrientationUpdates();
        context.textView.setText(context.textView.getText()+ "Started" + "\n");
    }

    void stop() {
        // Stop location and orientation updates
        locationExtraction.stopLocationUpdates();
        orientationExtraction.stopOrientationUpdates();
        context.textView.setText(context.textView.getText()+ "Stopped" + "\n");
    }

    String getData() {
        // Get location, orientation, contacts and calendar data
        Location location = locationExtraction.getLocation();
        Double orientation = orientationExtraction.getHeading();
        ArrayList<String> contacts = contactsExtraction.getContacts();
        String calendar = calendarExtraction.getCalendar();

        context.textView.setText(context.textView.getText()+ "Data" + "\n" + String.valueOf(location.getLatitude()) + "\n" + String.valueOf(orientation) + "\n" + String.valueOf(contacts.get(0))  + calendar + "\n");

        //TODO Build JSON object
        JSONObject object = new JSONObject();
        //object.put()

        // return JSON object (perhaps as String)
        return null;
    }
}
