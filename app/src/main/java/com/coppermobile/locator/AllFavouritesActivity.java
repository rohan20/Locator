package com.coppermobile.locator;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AllFavouritesActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    String title;
    String coordinates;

    ArrayList<FavouriteLocation> favourites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_favourites);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        favourites = new ArrayList<>();
        favourites.addAll(FavouriteLocation.listAll(FavouriteLocation.class));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        LatLngBounds INDIA = new LatLngBounds(
                new LatLng(7.2, 67.8), new LatLng(36.5, 93.8));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INDIA.getCenter(), 4));

        Toast.makeText(AllFavouritesActivity.this, "favourites.size() = " + favourites.size(), Toast.LENGTH_SHORT).show();

        addMarkers();

    }


    public void addMarkers() {

        if (mMap != null) {
            for (int i = 0; i < favourites.size(); i++) {
                title = favourites.get(i).getTitle();
                coordinates = "Lat: " + String.valueOf(favourites.get(i).getLatitude()).substring(0, 5) + " Lng: " + String.valueOf(favourites.get(i).getLongitude()).substring(0, 5);
                LatLng location = new LatLng(favourites.get(i).getLatitude(), favourites.get(i).getLongitude());

                int height = 150;
                int width = 150;
                BitmapDrawable bitmapdraw = (BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.favlocation, null);
                Bitmap b = null;
                if (bitmapdraw != null) {
                    b = bitmapdraw.getBitmap();
                }

                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);


                Geocoder geocoder = new Geocoder(AllFavouritesActivity.this);
                List<Address> address = null;
                try {
                    address = geocoder.getFromLocation(favourites.get(i).getLatitude(), favourites.get(i).getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String title = null;
                String desc = null;
                if (address != null) {
                    title = address.get(0).getAddressLine(0);
                    desc = address.get(0).getAddressLine(1);

                    if (address.get(0).getAddressLine(2) != null)
                        desc += " " + address.get(0).getAddressLine(2);
                }

                Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(title).snippet(desc).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                marker.showInfoWindow();
            }

        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));


        return true;
    }
}
