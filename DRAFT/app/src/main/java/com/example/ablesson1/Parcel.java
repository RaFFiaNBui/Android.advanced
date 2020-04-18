package com.example.ablesson1;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

class Parcel implements Serializable {

    private String cityName;
    private LatLng latLng;

    String getCityName() {
        return cityName;
    }

    LatLng getLatLng() {
        return latLng;
    }

    Parcel(String cityName) {
        this.cityName = cityName;
    }

    //package-private
    void setCityName(String cityName) {
        this.cityName = cityName;
    }

    //package-private
    void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
