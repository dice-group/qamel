package de.qa.qa.dataextraction;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import de.qa.R;

/**
 * Created by paramjot on 5/8/18.
 */

public class LocationExtraction extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private View view;
    private LocationCallback locationCallback;
    private Location location = null;
    TextView getLocation;
    OrientationExtraction orientation = new OrientationExtraction();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_location, container, false);
        getLocation = (TextView) view.findViewById(R.id.locationText);

        // Checking for system permissions to access location data
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Requesting system permission if it wasn't granted
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        } else {
            // Using the Google location API
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

            // Getting last known location (probably incorrect or too old, but better than nothing)
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location lastLocation) {
                    if (lastLocation != null) {
                        location = lastLocation;
                        getLocation.setText(location.toString());
                    }
                }
            });

            // Building a location request
            locationRequest = new LocationRequest();
            // Setting the standard interval for getting location data of 5 seconds
            locationRequest.setInterval(5000);
            // Setting the fastest interval
            locationRequest.setFastestInterval(1000);
            // Setting the priority - high accuracy uses information from gps, wifi and network
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            // Defining the callback void which updates the location using the new location data
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    location = locationResult.getLastLocation();
                    getLocation.setText(locationResult.toString());
                }
            };

            // TODO eventually delete the followin settings request because it's probably displaced
            // The following lines of code check if the location settings are satisfied (eg if locating over gps, wifi and network are all activated)
            // If not, a dialog will pop up, asking the user to change the settings
            // This might happen at an unfitting moment (eg while typing the question)
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            SettingsClient client = LocationServices.getSettingsClient(getActivity());
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

            task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    // Location settings are satisfied
                }
            });

            task.addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case CommonStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(getActivity(), 0x1);
                            } catch (IntentSender.SendIntentException sendEx) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way
                            // to fix the settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
        return view;

    }

    public Location getLocation() {
        // Returning the current location
        return location;
    }

    public void startLocationUpdates() {
        // Starting the location updates
        // Checking for system permissions
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Requesting the location updates (which are defined by the location request and callback void)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    public void stopLocationUpdates() {
        // Stopping (pausing) the location updates
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }
}