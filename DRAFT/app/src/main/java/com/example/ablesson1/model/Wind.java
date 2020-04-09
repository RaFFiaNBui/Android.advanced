package com.example.ablesson1.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wind {

    @SerializedName("speed")
    @Expose
    private float speed;

    public int getSpeed() {
        return Math.round(speed);
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
