package com.example.universityjava.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ListingDAO {
    @Insert
    void insert(Listing listing);

    @Query("DELETE FROM Listing")
    void deleteAll();

    @Query("SELECT * FROM Listing")
    List<Listing> getAllListings();

    @Query("SELECT * FROM Listing WHERE fk_gameid LIKE :id")
    List<Listing> getListingsByGameId(long id);

    @Query("SELECT * FROM Listing WHERE fk_seller LIKE :userid")
    List<Listing> getListingsByUserId(long userid);

    @Query("SELECT * FROM Listing WHERE id = :id")
    Listing getListingByID(long id);
}
