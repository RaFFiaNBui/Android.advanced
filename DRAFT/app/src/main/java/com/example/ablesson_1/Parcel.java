package com.example.ablesson_1;

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
