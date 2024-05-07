package com.cmp354.ausvalet;

public class Request {
    private String customerId;
    private String captainId;
    private String status;
    private Boolean isDropped;

    private String dropOffLocation;
    private String parkingLocation;

    public Boolean getDropped() {
        return isDropped;
    }

    public void setDropped(Boolean dropped) {
        isDropped = dropped;
    }

    public String getDropOffLocation() {
        return dropOffLocation;
    }

    public void setDropOffLocation(String dropOffLocation) {
        this.dropOffLocation = dropOffLocation;
    }

    public String getParkingLocation() {
        return parkingLocation;
    }

    public void setParkingLocation(String parkingLocation) {
        this.parkingLocation = parkingLocation;
    }

    public Request(String customerId, String captainId, String status, Boolean isDropped, String dropOffLocation, String parkingLocation) {
        this.customerId = customerId;
        this.captainId = captainId;
        this.status = status;
        this.isDropped = isDropped;
        this.dropOffLocation = dropOffLocation;
        this.parkingLocation = parkingLocation;
    }

    public Request() {
    }

    @Override
    public String toString() {
        return
                "\nFrom Drop Off Location=" + dropOffLocation + '\n' +
                "To Parking Location=" + parkingLocation + "\n\n" ;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCaptainId() {
        return captainId;
    }

    public void setCaptainId(String captainId) {
        this.captainId = captainId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
