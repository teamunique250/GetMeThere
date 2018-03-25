package com.corelabsplus.getmethere.utils;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by victor on 3/24/18.
 */

public class Bus {
    public String plateNumber;
    public String Agency;
    public String origin;
    public String destination;
    public int seats;
    public MLatLng currentLocation;

    public Bus(String plateNumber, String agency, String origin, String destination, int seats, MLatLng currentLocation) {
        this.plateNumber = plateNumber;
        Agency = agency;
        this.origin = origin;
        this.destination = destination;
        this.seats = seats;
        this.currentLocation = currentLocation;
    }

    public Bus() {
    }
}
