package com.example.universityjava.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PickupPointDAO {

    @Query("SELECT * FROM pickup_points")
    List<PickupPoint> getAllPickupPoints();

    @Query("SELECT * FROM pickup_points WHERE id = :id")
    PickupPoint getPickupPointById(String id);

    @Query("SELECT * FROM pickup_points WHERE courier = :courier")
    List<PickupPoint> getPickupPointsByCourier(String courier);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPickupPoint(PickupPoint pickupPoint);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPickupPoints(List<PickupPoint> pickupPoints);

    @Query("DELETE FROM pickup_points")
    void clearAll();
}
