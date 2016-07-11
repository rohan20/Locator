package com.coppermobile.locator;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;

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


/**
 * @author Rohan Taneja
 *         This class displays all favourites on the map with markers.
 */
public class AllFavouritesActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private String title;
    private String coordinates;

    private ArrayList<FavouriteLocation> favourites;

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

        LatLngBounds WORLD = new LatLngBounds(
                new LatLng(25.393661, -136.230469), new LatLng(30.968189, 157.324219));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(WORLD.getCenter(), 1));

        addMarkers();

    }


    private void addMarkers() {

        if (mMap != null) {
            for (FavouriteLocation favouriteLocation : favourites) {
                title = favouriteLocation.getTitle();
                coordinates = "Lat: " + String.valueOf(favouriteLocation.getLatitude()).substring(0, 5) + " Lng: " + String.valueOf(favouriteLocation.getLongitude()).substring(0, 5);
                LatLng location = new LatLng(favouriteLocation.getLatitude(), favouriteLocation.getLongitude());

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
                    address = geocoder.getFromLocation(favouriteLocation.getLatitude(), favouriteLocation.getLongitude(), 1);
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
