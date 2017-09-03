package eu.qamel.dataextraction;

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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Florian on 11.08.2017.
 */

public class CalendarExtraction {
    private static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR= 10;
    private String iCalendar = null;

    public CalendarExtraction(MainActivity context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.READ_CALENDAR},
                    MY_PERMISSIONS_REQUEST_READ_CALENDAR);

        } else {
            String[] INSTANCE_PROJECTION = new String[]{
                    CalendarContract.Instances.EVENT_TIMEZONE,
                    CalendarContract.Instances.TITLE,
                    CalendarContract.Instances.DTSTART,
                    CalendarContract.Instances.DTEND,
                    CalendarContract.Instances.DESCRIPTION,
                    CalendarContract.Instances.EVENT_LOCATION
            };

            String[] Columns = new String[]{
                    "TZID",
                    "SUMMARY",
                    "DTSTART",
                    "DTEND",
                    "DESCRIPTION",
                    "LOCATION"
            };

            long timeSpan = 36 * 60 * 60 * 1000; //36 h in ms
            long startMillis = System.currentTimeMillis();
            long endMillis = startMillis + timeSpan;

            Cursor cursor;
            ContentResolver contentResolver = context.getContentResolver();

            Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
            ContentUris.appendId(builder, startMillis);
            ContentUris.appendId(builder, endMillis);

            cursor = contentResolver.query(builder.build(), INSTANCE_PROJECTION, null, null, null);

            String iCalendar = "BEGIN:VCALENDAR\n" +
                    "VERSION:2.0\n" +
                    "PRODID:-//aksw//qamel//data-extraction//EN\n" +
                    "CALSCALE:GREGORIAN\n";

            while (cursor.moveToNext()) {
                String vEvent = "BEGIN:VEVENT\n";
                for (int i = 1; i < INSTANCE_PROJECTION.length; i++) {
                    if (Columns[i]=="DTSTART"||Columns[i]=="DTEND"){
                        String timeMillis = cursor.getString(cursor.getColumnIndex(INSTANCE_PROJECTION[i]));
                        Date date = new Date(Long.parseLong(timeMillis));
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                        String dateTime = simpleDateFormat.format(date);
                        vEvent = vEvent + Columns[i] + ";TZID=/" + cursor.getString(cursor.getColumnIndex(INSTANCE_PROJECTION[0]))
                                + ":" + dateTime + "\n";
                    } else {
                        vEvent = vEvent + Columns[i] + ":" + cursor.getString(cursor.getColumnIndex(INSTANCE_PROJECTION[i])) + "\n";
                    }
                }
                vEvent = vEvent + "END:VEVENT\n";
                iCalendar = iCalendar + vEvent;
            }
            iCalendar = iCalendar + "END:VCALENDAR";
            this.iCalendar = iCalendar;
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("temp", iCalendar);
            clipboard.setPrimaryClip(clip);
        }
    }

    public String getCalendar() {
        return iCalendar;
    }
}
