package com.example.universityjava.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pickup_points")
public class PickupPoint {

    @PrimaryKey
    @NonNull
    private String id;

    private String name;

    private String address;

    private String courier;

    private double latitude;

    private double longitude;

    public PickupPoint(
            @NonNull String id,
            String name,
            String address,
            String courier,
            double latitude,
            double longitude
    ) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.courier = courier;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCourier() {
        return courier;
    }

    public void setCourier(String courier) {
        this.courier = courier;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
