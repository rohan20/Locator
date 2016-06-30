package com.coppermobile.locator;

import com.orm.SugarRecord;

public class FavouriteLocation extends SugarRecord {

    String title;
    double latitude;
    double longitude;

    public FavouriteLocation(String title, double latitude, double longitude) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getTitle() {
        return title;
    }

    public FavouriteLocation() {

    }

}
