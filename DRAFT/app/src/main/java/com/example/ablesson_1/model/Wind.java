package com.example.ablesson_1.model;

public class Wind {
    private float speed;

    public int getSpeed() {
        return Math.round(speed);
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
