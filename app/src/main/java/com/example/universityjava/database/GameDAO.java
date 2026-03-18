package com.example.universityjava.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import java.util.List;

@Dao
public interface GameDAO {
    @Insert
    void insert(Game game);

    @Query("DELETE FROM Game")
    void deleteAll();

    @Query("SELECT * FROM Game ORDER BY title")
    List<Game> getAllGames();

    @Query("SELECT * FROM Game WHERE title LIKE :name")
    List<Game> getGameByName(String name);

    @Query("SELECT * FROM Game WHERE id = :id")
    Game getGameByID(long id);
}
