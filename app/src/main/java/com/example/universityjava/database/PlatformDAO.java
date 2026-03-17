package com.example.universityjava.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlatformDAO {
    @Insert
    void insert(Platform platform);

    @Query("DELETE FROM Platform")
    void deleteAll();

    @Query("SELECT * FROM Platform ORDER BY name")
    List<Platform> getAllPlatforms();

    @Query("SELECT * FROM Platform WHERE name LIKE :name")
    List<Platform> getPlatformByName(String name);

    @Query("SELECT * FROM Platform WHERE id = :id")
    Platform getPlatformByID(int id);
}
