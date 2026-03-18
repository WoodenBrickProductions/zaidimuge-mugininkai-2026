package com.example.universityjava.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
@Dao
public interface CartListingDAO {
    @Insert
    void insert(CartListing listing);

    @Query("DELETE FROM CartListing")
    void deleteAll();

    @Query("SELECT * FROM CartListing")
    List<CartListing> getAllListings();

    @Query("SELECT * FROM CartListing WHERE fk_userid LIKE :userid")
    List<CartListing> getListingsByUserId(long userid);

    @Query("SELECT * FROM CartListing WHERE fk_listingid = :id")
    List<CartListing> getListingByListingID(long id);
}
