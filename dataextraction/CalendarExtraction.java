import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Florian on 11.08.2017.
 */

public class CalendarExtraction {
    private static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR= 10;
    private String iCalendar = null;

    public CalendarExtraction(MainActivity context) {
        // Checking for system permissions to access contacts data
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            
            // Requesting system permission if it wasn't granted
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.READ_CALENDAR},
                    MY_PERMISSIONS_REQUEST_READ_CALENDAR);

        } else {
            // Defining which event data will be extracted
            String[] INSTANCE_PROJECTION = new String[]{
                    CalendarContract.Instances.EVENT_TIMEZONE,
                    CalendarContract.Instances.TITLE,
                    CalendarContract.Instances.DTSTART,
                    CalendarContract.Instances.DTEND,
                    CalendarContract.Instances.DESCRIPTION,
                    CalendarContract.Instances.EVENT_LOCATION
            };
            // Defining the Strings that will be used as keys in the iCal String
            String[] keys = new String[]{
                    "TZID", //time zone id
                    "SUMMARY", //title
                    "DTSTART", //date time start
                    "DTEND", //date time end
                    "DESCRIPTION",
                    "LOCATION"
            };

            // Setting the time span which we're interested in (36 h)
            long timeSpan = 36 * 60 * 60 * 1000; //36 h in ms
            long startMillis = System.currentTimeMillis();
            long endMillis = startMillis + timeSpan;

            // Extracting the events, storing them in a cursor
            Cursor cursor;
            ContentResolver contentResolver = context.getContentResolver();

            Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
            ContentUris.appendId(builder, startMillis);
            ContentUris.appendId(builder, endMillis);

            cursor = contentResolver.query(builder.build(), INSTANCE_PROJECTION, null, null, null);

            // Building the iCal String
            String iCalendar = "BEGIN:VCALENDAR\n" +
                    "VERSION:2.0\n" +
                    "PRODID:-//aksw//qamel//data-extraction//EN\n" +
                    "CALSCALE:GREGORIAN\n";
            
            while (cursor.moveToNext()) {
                // Extracting the event data
                String vEvent = "BEGIN:VEVENT\n";
                for (int i = 1; i < INSTANCE_PROJECTION.length; i++) {
                    if (keys[i]=="DTSTART"||keys[i]=="DTEND"){
                        // Parsing the start (respectively the end) time from epoch millis to the suiting iCal format
                        String timeMillis = cursor.getString(cursor.getColumnIndex(INSTANCE_PROJECTION[i]));
                        Date date = new Date(Long.parseLong(timeMillis));
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                        String dateTime = simpleDateFormat.format(date);
                        // Adding key and value (including time zone information)
                        vEvent = vEvent + keys[i] + ";TZID=/" + cursor.getString(cursor.getColumnIndex(INSTANCE_PROJECTION[0]))
                                + ":" + dateTime + "\n";
                    } else {
                        // Adding key and value
                        vEvent = vEvent + keys[i] + ":" + cursor.getString(cursor.getColumnIndex(INSTANCE_PROJECTION[i])) + "\n";
                    }
                }
                vEvent = vEvent + "END:VEVENT\n";
                iCalendar = iCalendar + vEvent;
            }
            iCalendar = iCalendar + "END:VCALENDAR";
            this.iCalendar = iCalendar;
            // TODO: There were some errors validating the iCal String on icalendar.org/validator.html
            // eg time stamp and event id are missing, time zone seems to be incorrect
        }
    }

    public String getCalendar() {
        // Returning the iCal String
        return iCalendar;
    }
}
