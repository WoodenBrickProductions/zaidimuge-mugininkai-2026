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

    @Query("DELETE FROM WishlistGame WHERE fk_userid = :userid AND" +
            " fk_gameid = :gameid")
    void removeWGameByGameAndUserID(long gameid, long userid);

    @Query("SELECT * FROM WishlistGame")
    List<WishlistGame> getAllWGames();

    @Query("SELECT * FROM WishlistGame WHERE fk_userid LIKE :userid")
    List<WishlistGame> getWGameByUserId(long userid);

    @Query("SELECT * FROM WishlistGame WHERE fk_gameid = :id")
    List<WishlistGame> getWGameByGameID(long id);

    @Query("SELECT * FROM WishlistGame WHERE fk_gameid = :gameid AND fk_userid = :userid")
    List<WishlistGame> getWGameByListingAndUserID(long gameid, long userid);
}
