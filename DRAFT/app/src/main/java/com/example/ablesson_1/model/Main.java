package com.example.ablesson_1.model;

import android.util.Log;

public class Main {
    private float temp;
    private int pressure;
    private int humidity;

    public int getTemp() {
        return Math.round(temp);
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }
}
