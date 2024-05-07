package com.cmp354.ausvalet;

public class Car {

    private String id;
    private Boolean isAutomatic;
    private String make;


    private String model;

    private String plate;


    private String year;

    public Car(String id, Boolean isAutomatic, String make, String model, String plate, String year) {
        this.id = id;
        this.isAutomatic = isAutomatic;
        this.make = make;
        this.model = model;
        this.plate = plate;
        this.year = year;
    }

    @Override
    public String toString() {
        return "\nCar Specs =" +
                "make=" + make + '\n' +
                ", model=" + model + '\n' +
                ", plate=" + plate  +
                '\n';
    }

    public Car() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getAutomatic() {
        return isAutomatic;
    }

    public void setAutomatic(Boolean automatic) {
        isAutomatic = automatic;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
