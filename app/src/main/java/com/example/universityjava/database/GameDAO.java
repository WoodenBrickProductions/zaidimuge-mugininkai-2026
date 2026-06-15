package com.example.universityjava.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import java.util.List;

@Dao
public interface GameDAO {
    @Insert
    long insert(Game game);

    @Update
    void update(Game game);

    @Query("DELETE FROM Game")
    void deleteAll();

    @Query("SELECT * FROM Game ORDER BY title")
    List<Game> getAllGames();

    @Query("SELECT * FROM Game WHERE title LIKE '%' || :query || '%' ORDER BY title")
    List<Game> getGamesByTitle(String query);

    @Query("""
        SELECT g.*
        FROM game g
        INNER JOIN listing l ON l.fk_gameid = g.id
        WHERE l.issold = 0
        GROUP BY g.id
        ORDER BY COUNT(l.id) DESC
        LIMIT :limit
    """)
    List<Game> getMostPopularGames(int limit);

    @Query("""
        SELECT g.*
        FROM game g
        INNER JOIN listing l ON l.fk_gameid = g.id
        WHERE l.issold = 0
          AND l.isdigital = 0
        GROUP BY g.id
        ORDER BY COUNT(l.id) DESC
        LIMIT :limit
    """)
    List<Game> getMostPopularPhysicalGames(int limit);

    @Query("""
    SELECT *
    FROM game
    ORDER BY id DESC
    LIMIT :limit
""")
    List<Game> getNewestGames(int limit);

    @Query("""
    SELECT DISTINCT g.*
    FROM game g
    INNER JOIN listing l ON l.fk_gameid = g.id
    WHERE l.issold = 0
    ORDER BY g.id DESC
    LIMIT :limit
""")
    List<Game> getNewestGamesWithActiveListings(int limit);

    @Query("""
    SELECT *
    FROM listing
    WHERE fk_gameid = :gameId
      AND issold = 0
    ORDER BY price ASC
""")
    List<Listing> getActiveListingsForGameSortedByPrice(long gameId);

    @Query("SELECT * FROM Game WHERE title LIKE :name")
    List<Game> getGameByName(String name);

    @Query("SELECT * FROM Game WHERE id = :id LIMIT 1")
    Game getGameByID(long id);

    @Query("SELECT MIN(price) FROM Listing INNER JOIN Game ON Listing.fk_gameid = :id")
    double getGameMinPriceById(long id);

    @Query("SELECT * FROM Game INNER JOIN WishlistGame ON Game.id = fk_gameid WHERE fk_userid = :id")
    List<Game> getWishlistGameByUserId(long id);
}
