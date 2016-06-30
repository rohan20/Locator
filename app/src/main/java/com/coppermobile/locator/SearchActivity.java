package com.coppermobile.locator;

import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements OnMapReadyCallback, SearchView.OnQueryTextListener, GoogleMap.OnMarkerClickListener {

    Geocoder geocoder;
    SearchView searchView;
    GoogleMap mMap;
    String descriptionOfLocation;
    String titleOfLocation;

    List<Address> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setTitle("Search Location");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map3);
        mapFragment.getMapAsync(this);

        geocoder = new Geocoder(SearchActivity.this, Locale.getDefault());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
    }

    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                Toast.makeText(SearchActivity.this, "Search", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getLocationFromAddress(String searchQuery) {

        try {
            addresses = geocoder.getFromLocationName(searchQuery, 1);
        } catch (IOException e) {
            Toast.makeText(SearchActivity.this, "No such address found!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }

        if (addresses.size() == 0) {
            Toast.makeText(SearchActivity.this, "No such address found!", Toast.LENGTH_SHORT).show();
        } else {
            Address address = addresses.get(0);
            Double latitude = address.getLatitude();
            Double longitude = address.getLongitude();

            titleOfLocation = "" + address.getAddressLine(0);
            descriptionOfLocation = address.getAddressLine(1) + " ";

            if (address.getAddressLine(2) != null) {
                descriptionOfLocation += address.getAddressLine(2);
            }

            LatLng positionFromQuery = new LatLng(latitude, longitude);
            showOnMap(positionFromQuery);
        }

    }

    public void showOnMap(LatLng position) {

        mMap.clear();
        Marker marker = mMap.addMarker(new MarkerOptions().position(position).title(titleOfLocation).snippet(descriptionOfLocation));
        marker.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        getLocationFromAddress(query);
        searchView.setQuery("", false);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View alertDialog = inflater.inflate(R.layout.add_to_favourites_alert_dialog, null);
        builder.setView(alertDialog);

        final EditText title = (EditText) alertDialog.findViewById(R.id.titleOfFavourite);
        final LatLng position = marker.getPosition();

        builder.setTitle("Add to favourites?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = title.getText().toString();
                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(SearchActivity.this, "Title cannot be blank", Toast.LENGTH_SHORT).show();
                } else {
                    FavouriteLocation fav = new FavouriteLocation(text, position.latitude, position.longitude);
                    fav.save();
                    Toast.makeText(SearchActivity.this, "Added to favourites", Toast.LENGTH_SHORT).show();
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
}
