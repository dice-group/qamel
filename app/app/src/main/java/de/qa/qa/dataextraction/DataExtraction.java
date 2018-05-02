package de.qa.qa.dataextraction;

import android.content.Context;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Florian Speer on 17.07.2017.
 */

public class DataExtraction {

    private final ContactsExtraction contactsExtraction;
    private final CalendarExtraction calendarExtraction;

    Context context;

    public DataExtraction(Context context) {
        this.context = context;

        contactsExtraction = new ContactsExtraction();
        // Initialize calendar extraction
        calendarExtraction = new CalendarExtraction();
    }


    JSONObject getData() {

        ArrayList<String> contacts = contactsExtraction.getContacts();
        String iCalendar = calendarExtraction.getCalendar();

        // Building the JSON
        JSONObject json = new JSONObject();
        try {

            json.put("vcards", contacts);
            json.put("ical", iCalendar);
        } catch (Exception e) {
        }

        // Returning the JSON String
        return json;
    }
}