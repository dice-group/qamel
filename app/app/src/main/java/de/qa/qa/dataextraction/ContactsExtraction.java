package de.qa.qa.dataextraction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.qa.R;


/**
 * Created by Florian Speer on 08.08.2017.
 */

public class ContactsExtraction extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 10;
    ArrayList<String> vCards;
    private View view;
    ListView contactList;
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contacts, container, false);
        contactList = (ListView) view.findViewById(R.id.contacts);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Storing contacts data in a cursor
            Cursor cursor = getActivity().getContentResolver().query(
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
                        mAssetFileDescriptor = getActivity().getContentResolver().openAssetFileDescriptor(uri, "r");
                        FileInputStream mFileInputStream = mAssetFileDescriptor.createInputStream();
                        Integer length = mFileInputStream.available();
                        String vCard = "";
                        for (int i = 0; i < length; i++) {
                            vCard = vCard + (char) mFileInputStream.read();
                        }
                        vCards.add(vCard);
                        adapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item,vCards);
                        contactList.setAdapter(adapter);

                    } catch (IOException e) {
                    }
                } while (cursor.moveToNext());
            } finally {
                cursor.close();
            }
        }
        return view;
    }
    private int checkSelfPermission(String readContacts) {
        return 0;
    }


    ArrayList<String> getContacts() {
        // Returning the vCards ArrayList
        return vCards;
    }
}