import android.content.Context;
import android.location.Location;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Florian Speer on 17.07.2017.
 */

public class DataExtraction {
    private final LocationExtraction locationExtraction;
    private final OrientationExtraction orientationExtraction;
    private final ContactsExtraction contactsExtraction;
    private final CalendarExtraction calendarExtraction;
    Context context;

    DataExtraction(Context context) {
        this.context = context;
        
        // Initialize extraction of location + orientation
        locationExtraction = new LocationExtraction(context);
        orientationExtraction = new OrientationExtraction(context);

        // Initialize calendar extraction
        calendarExtraction = new CalendarExtraction(context);

        // Initialize contact extraction
        contactsExtraction = new ContactsExtraction(context);
    }

    void start() {
        // Start location and orientation updates
        locationExtraction.startLocationUpdates();
        orientationExtraction.startOrientationUpdates();
    }

    void stop() {
        // Stop location and orientation updates
        locationExtraction.stopLocationUpdates();
        orientationExtraction.stopOrientationUpdates();
    }

    JSONObject getData() {
        // Get location, orientation, contacts and calendar data
        Location location = locationExtraction.getLocation();
        Double orientation = orientationExtraction.getHeading();
        String iCalendar = calendarExtraction.getCalendar();
        ArrayList<String> contacts = contactsExtraction.getContacts();

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
        
        // Returning the JSON String
        return json;
    }
}
