package de.bell.permissionmanagement;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.location.Location;

import org.json.JSONException;
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

        // Initialize calendar extraction
        calendarExtraction = new CalendarExtraction(context);

        // Initialize contact extraction
        contactsExtraction = new ContactsExtraction(context);

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
        String iCalendar = calendarExtraction.getCalendar();
        ArrayList<String> contacts = contactsExtraction.getContacts();

        context.textView.setText(context.textView.getText()+ "Data" + "\n" + String.valueOf(location.getLatitude()) + "\n" + String.valueOf(orientation) + "\n" + String.valueOf(contacts.get(0))  + iCalendar + "\n");

        // Building the JSON
        JSONObject json = new JSONObject();
        try {
            json.put("lat", String.valueOf(location.getLatitude()));
            json.put("long", String.valueOf(location.getLongitude()));
            json.put("orient", String.valueOf(orientation));
            json.put("ical", iCalendar);
            json.put("vcards", contacts);
        } catch (Exception e) {
        }
        context.textView.setText(json.toString());
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("temp", json.toString());
        clipboard.setPrimaryClip(clip);
        // Returning the JSON String
        return json.toString();
    }
}
