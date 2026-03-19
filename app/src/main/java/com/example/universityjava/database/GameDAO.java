package com.example.universityjava.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import java.util.List;

@Dao
public interface GameDAO {
    @Insert
    long insert(Game game);

    @Query("DELETE FROM Game")
    void deleteAll();

    @Query("SELECT * FROM Game ORDER BY title")
    List<Game> getAllGames();

    @Query("SELECT * FROM Game WHERE title LIKE :name")
    List<Game> getGameByName(String name);

    @Query("SELECT * FROM Game WHERE id = :id")
    Game getGameByID(long id);

    @Query("SELECT MIN(price) FROM Listing INNER JOIN Game ON Listing.fk_gameid = :id")
    double getGameMinPriceById(long id);

    @Query("SELECT * FROM Game INNER JOIN WishlistGame ON Game.id = fk_gameid WHERE fk_userid = :id")
    List<Game> getWishlistGameByUserId(long id);
}
