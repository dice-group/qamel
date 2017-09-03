import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Florian Speer on 08.08.2017.
 */

public class ContactsExtraction {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 10;
    ArrayList<String> vCards;

    ContactsExtraction(MainActivity context) {
        // Checking for system permissions to access contacts data
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Requesting system permission if it wasn't granted
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            // Storing contacts data in a cursor
            Cursor cursor = context.getContentResolver().query(
                    ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            // Extracting the vCard of every contact, storing them in an ArrayList
            vCards = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) try {
                do {
                    // Getting the lookup key (specific vCard uri for this contact)
                    String lookupKey = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));

                    // Building the vCard uri
                    Uri uri = Uri.withAppendedPath(
                            ContactsContract.Contacts.CONTENT_VCARD_URI,
                            lookupKey);

                    // Reading the vCard data
                    AssetFileDescriptor mAssetFileDescriptor;
                    try {
                        mAssetFileDescriptor = context.getContentResolver().openAssetFileDescriptor(uri, "r");
                        FileInputStream mFileInputStream = mAssetFileDescriptor.createInputStream();

                        Integer length = mFileInputStream.available();

                        String vCard = "";
                        for (int i = 0; i < length; i++) {
                            vCard = vCard + (char) mFileInputStream.read();
                        }
                        
                        vCards.add(vCard);
                    } catch (IOException e) {
                    }
                } while (cursor.moveToNext());
            } finally {
                cursor.close();
            }
        }
    }

    ArrayList<String> getContacts() {
        // Returning the vCards ArrayList
        return vCards;
    }
}
