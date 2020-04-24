package com.example.ablesson1;

import java.io.Serializable;

class Parcel implements Serializable {

    private String cityName;

    String getCityName() {
        return cityName;
    }

    Parcel(String cityName) {
        this.cityName = cityName;
    }
}
