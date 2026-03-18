package com.example.universityjava.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WishlistGameDAO {
    @Insert
    void insert(WishlistGame listing);

    @Query("DELETE FROM WishlistGame")
    void deleteAll();

    @Query("SELECT * FROM WishlistGame")
    List<WishlistGame> getAllListings();

    @Query("SELECT * FROM WishlistGame WHERE fk_userid LIKE :userid")
    List<WishlistGame> getListingsByUserId(long userid);

    @Query("SELECT * FROM WishlistGame WHERE fk_gameid = :id")
    List<WishlistGame> getListingByGameID(long id);
}
