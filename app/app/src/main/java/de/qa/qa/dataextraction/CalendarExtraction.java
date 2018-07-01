package de.qa.qa.dataextraction;

/**
 * Created by paramjot on 5/2/18.
 */
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import de.qa.R;


public class CalendarExtraction extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR= 10;
    private String iCalendar=null;
    private View view;
    TextView calendar;
    Date date;
    CalendarView cal;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendar=(TextView) view.findViewById(R.id.calendar);
       // cal=(CalendarView)view.findViewById(R.id.cal);

        // Checking for system permissions to access contacts data
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            // Requesting system permission if it wasn't granted
            ActivityCompat.requestPermissions(getActivity(),
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
            ContentResolver contentResolver = getActivity().getContentResolver();

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
                    if (keys[i] == "DTSTART" || keys[i] == "DTEND") {
                        // Parsing the start (respectively the end) time from epoch millis to the suiting iCal format
                        String timeMillis = cursor.getString(cursor.getColumnIndex(INSTANCE_PROJECTION[i]));
                        date = new Date();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
            calendar.setText(iCalendar);

            // Toast.makeText(getContext(),"Calendar:  "+iCalendar,Toast.LENGTH_LONG).show();
            // TODO: There were some errors validating the iCal String on icalendar.org/validator.html
            // eg time stamp and event id are missing, time zone seems to be incorrect

        }
        return view;
    }
        public String getCalendar() {
            // Returning the iCal String
            return iCalendar;
        }

}