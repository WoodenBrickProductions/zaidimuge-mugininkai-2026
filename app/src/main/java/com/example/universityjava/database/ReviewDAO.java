package com.example.universityjava.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReviewDAO {
    @Insert
    void insert(Review user);

    @Query("DELETE FROM Review")
    void deleteAll();

    @Query("SELECT * FROM Review")
    List<Review> getAllReviews();

    @Query("SELECT * FROM Review WHERE fk_listingid = :id")
    Review getReviewByID(long id);
}
