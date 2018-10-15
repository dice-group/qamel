package de.qa.qa.dataextraction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import de.qa.R;
import ezvcard.Ezvcard;
import ezvcard.VCard;

public class ContactsExtraction extends Fragment implements AdapterView.OnItemClickListener{

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 10;
    private static final int PERMISSION_REQUEST_CODE = 1;
    ArrayList<String> vCards;
    private View view;
    ListView contactList;
    ArrayList<String> numberItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;
    VCard vcard;
    String number;
    String fullName;
    String name;
    Statement nameStatement;
    private String filename = "SampleFile.txt";
    File myExternalFile;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contacts, container, false);
        contactList = view.findViewById(R.id.contacts);
        contactList.setOnItemClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS );
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Storing contacts data in a cursorPERMISSION_REQUEST_CODE
            Cursor cursor = getActivity().getContentResolver().query(
                    ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

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
                        try {
                            vcard = Ezvcard.parse(vCard).first();
                            name= vcard.getStructuredName().getFamily();
                            if (name==null){
                                name = "No Family name";
                            }
                            fullName = vcard.getFormattedName().getValue();
                            number = vcard.getTelephoneNumbers().get(0).getText();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        vCards.add(name+"\n"+fullName+"\n"+number);
                        numberItems.add(number);
                        adapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item,vCards);
                        contactList.setAdapter(adapter);
                        for(int x=0; x<numberItems.size();x++) {
                            ValueFactory factory = SimpleValueFactory.getInstance();
                            Resource r = factory.createIRI("http://qamel.org/id#" + x);
                            String firstname = fullName.replace(" ", "_");
                            IRI p = factory.createIRI("http://www.w3.org/2006/vcard/ns#" + firstname);
                            Literal o = factory.createLiteral(number+"^^<http://www.w3.org/2001/XMLSchema#string>");
                            nameStatement = factory.createStatement(r, p, o);
                        }
                            RDFWriter writer = Rio.createWriter(RDFFormat.NTRIPLES,System.out);
                            writer.startRDF();
                            Statement st = nameStatement;
                            writer.handleStatement(st);
                            System.out.println("Statement: "+nameStatement);
                            writer.endRDF();

                    } catch (IOException e) {
                    }
                } while (cursor.moveToNext());
            } finally {
                cursor.close();
            }
        }
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //   Toast.makeText(getActivity(), "position: "+position +number, Toast.LENGTH_SHORT).show();
        if (checkPermission()) {
            Log.e("permission", "Permission already granted.");
            if(checkPermission()== true) {
                String dial = "tel:" + numberItems.get(position);
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        } else {
            requestPermission();
        }
    }

    public boolean checkPermission() {

        int CallPermissionResult = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
        return CallPermissionResult == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]
                {
                        Manifest.permission.CALL_PHONE
                }, PERMISSION_REQUEST_CODE);

    }
    private int checkSelfPermission(String readContacts) {
        return 0;
    }
    ArrayList<String> getContacts() {
        // Returning the vCards ArrayList
        return vCards;
    }


}