package com.example.universityjava.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WishlistListingDAO {
    @Insert
    void insert(WishlistListing listing);

    @Query("DELETE FROM WishlistListing")
    void deleteAll();

    @Query("SELECT * FROM WishlistListing")
    List<WishlistListing> getAllListings();

    @Query("SELECT * FROM WishlistListing WHERE fk_userid LIKE :userid")
    List<WishlistListing> getListingsByUserId(long userid);

    @Query("SELECT * FROM WishlistListing WHERE fk_listingid = :id")
    List<WishlistListing> getListingByListingID(long id);
}
