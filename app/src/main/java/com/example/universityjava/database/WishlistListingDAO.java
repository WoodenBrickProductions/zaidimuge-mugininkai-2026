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

    @Query("DELETE FROM WishlistListing WHERE fk_userid = :userid AND" +
            " fk_listingid = :listid")
    void removeWListingByListingAndUserID(long listid, long userid);
    @Query("SELECT * FROM WishlistListing")
    List<WishlistListing> getAllWListings();

    @Query("SELECT * FROM WishlistListing WHERE fk_userid LIKE :userid")
    List<WishlistListing> getWListingsByUserId(long userid);

    @Query("SELECT * FROM WishlistListing WHERE fk_listingid = :id")
    List<WishlistListing> getWListingByListingID(long id);

    @Query("SELECT * FROM WishlistListing WHERE fk_listingid = :listid AND fk_userid = :userid")
    List<WishlistListing> getWListingByListingAndUserID(long listid, long userid);
}
