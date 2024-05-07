package com.cmp354.ausvalet;

import android.net.Uri;

public class User {

    private String first;
    private String last;
    private String id;
    private String number;

    private boolean available;

    private boolean captain;
    private int points;

    public User(String first, String last, String id, String number, boolean available, boolean captain, int points) {
        this.first = first;
        this.last = last;
        this.id = id;
        this.number = number;
        this.available = available;
        this.captain = captain;
        this.points = points;
    }

    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "first='" + first + '\'' +
                ", last='" + last + '\'' +
                ", id='" + id + '\'' +
                ", captain=" + captain +
                '}';
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean getAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean getCaptain() {
        return captain;
    }

    public void setCaptain(boolean captain) {
        this.captain = captain;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

}
