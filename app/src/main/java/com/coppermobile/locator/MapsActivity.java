package com.coppermobile.locator;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, View.OnClickListener {

    private GoogleMap mMap;

    private GoogleApiClient mGoogleApiClient;

    LatLng position;

    Menu mapsActivityMenu;

    Marker marker;

    android.support.design.widget.FloatingActionButton mSearchFloatingActionButton;

    List<Address> address;

    double latitude;
    double longitude;

    Location currentLocationShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent receivedIntent = getIntent();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (receivedIntent.getExtras() == null) {
            initialize();
        } else {
            initialize();
            position = new LatLng(receivedIntent.getDoubleExtra(Constants.LATITUDE, 0), receivedIntent.getDoubleExtra(Constants.LONGITUDE, 0));
        }

    }

    public void initialize() {
        mSearchFloatingActionButton = (android.support.design.widget.FloatingActionButton) findViewById(R.id.searchFloatingActionButton);
        mSearchFloatingActionButton.setOnClickListener(this);
        buildGoogleApiClient();
    }


    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mapsActivityMenu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.share:

                getLocation();

                Geocoder geocoder = new Geocoder(MapsActivity.this);
                try {
                    address = geocoder.getFromLocation(currentLocationShare.getLatitude(), currentLocationShare.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String title = address.get(0).getAddressLine(0);
                String desc = address.get(0).getAddressLine(1);
                if (address.get(0).getAddressLine(2) != null)
                    desc += " " + address.get(0).getAddressLine(2);

                String shareBody = "My Location:\n\n" + title + "\n" + desc + "\n\nLatitude: " + currentLocationShare.getLatitude() + "\nLongitude: " + currentLocationShare.getLongitude();

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Location");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share Using"));
                return true;

            case R.id.favourites:

                Intent i = new Intent(MapsActivity.this, FavouritesActivity.class);
                startActivity(i);
                return true;

            case R.id.getMyLocation:
                mMap.clear();
                getLocation();
                displayLocation();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);

        if (position == null) {
            //New Delhi
            position = new LatLng(28.6139, 77.2090);
            Toast.makeText(MapsActivity.this, "Default Location: New Delhi", Toast.LENGTH_SHORT).show();
        }

        if (position != null) {
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            try {
                address = geocoder.getFromLocation(position.latitude, position.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String title = address.get(0).getAddressLine(0);
            String desc = address.get(0).getAddressLine(1);
            if (address.get(0).getAddressLine(2) != null)
                desc += " " + address.get(0).getAddressLine(2);

            marker = mMap.addMarker(new MarkerOptions().position(position).title(title).snippet(desc));
            marker.showInfoWindow();
            marker.setDraggable(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            return;
        }

    }

    private void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    getLocation();
                }
        }
    }

    //launch alert to enable GPS
    public void buildAlertMessageNoGps() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Toast.makeText(MapsActivity.this, "Unable to access your location. Requires GPS.", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

        builder.create().show();
    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0);

            Toast.makeText(MapsActivity.this, "No Permission", Toast.LENGTH_SHORT).show();
            return;
        }

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


        if (mLastLocation == null) {

            //check if GPS is disabled
            if (!(((LocationManager) getSystemService(LOCATION_SERVICE)).isProviderEnabled((LocationManager.GPS_PROVIDER)))) {
                buildAlertMessageNoGps();
            }

            return;

        }

        //else if mLocation is not null
        currentLocationShare = mLastLocation;

        latitude = mLastLocation.getLatitude();
        longitude = mLastLocation.getLongitude();
    }

    public void displayLocation() {
        position = new LatLng(latitude, longitude);

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        try {
            address = geocoder.getFromLocation(position.latitude, position.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String title = address.get(0).getAddressLine(0);
        String desc = address.get(0).getAddressLine(1);
        if (address.get(0).getAddressLine(2) != null)
            desc += " " + address.get(0).getAddressLine(2);

        marker = mMap.addMarker(new MarkerOptions().position(position).title(title).snippet(desc));
        marker.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View alertDialog = inflater.inflate(R.layout.add_to_favourites_alert_dialog, null);
        builder.setView(alertDialog);

        final EditText title = (EditText) alertDialog.findViewById(R.id.titleOfFavourite);
        position = marker.getPosition();

        builder.setTitle("Add to favourites?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = title.getText().toString();
                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(MapsActivity.this, "Title cannot be blank", Toast.LENGTH_SHORT).show();
                } else {
                    FavouriteLocation fav = new FavouriteLocation(text, position.latitude, position.longitude);
                    fav.save();
                    Toast.makeText(MapsActivity.this, "Added to favourites", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();


        return true;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        position = marker.getPosition();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.searchFloatingActionButton:
                Intent i = new Intent(MapsActivity.this, SearchActivity.class);
                startActivity(i);
                break;
        }

    }
}