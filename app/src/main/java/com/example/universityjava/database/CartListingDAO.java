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

    @Query("DELETE FROM CartListing WHERE fk_listingid = :listingId")
    void deleteByListingId(long listingId);

    @Query("SELECT * FROM CartListing")
    List<CartListing> getAllCListings();

    @Query("DELETE FROM CartListing WHERE fk_userid = :userid AND" +
            " fk_listingid = :listid")
    void removeCListingByListingAndUserID(long listid, long userid);

    @Query("SELECT * FROM CartListing WHERE fk_userid LIKE :userid")
    List<CartListing> getCListingsByUserId(long userid);

    @Query("SELECT * FROM CartListing WHERE fk_listingid = :id")
    List<CartListing> getCListingByListingID(long id);

    @Query("SELECT * FROM CartListing WHERE fk_listingid = :listid AND fk_userid = :userid")
    List<CartListing> getCListingByListingAndUserID(long listid, long userid);
}
