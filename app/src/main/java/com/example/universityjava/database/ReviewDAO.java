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

    @Query("DELETE FROM Review WHERE fk_listingid = :listingId")
    void deleteByListingId(long listingId);

    @Query("SELECT * FROM Review")
    List<Review> getAllReviews();

    @Query("SELECT * FROM Review WHERE fk_listingid = :id")
    Review getReviewByID(long id);

    @Query("SELECT Review.* FROM Review " +
            "INNER JOIN Listing ON Review.fk_listingid = Listing.id " +
            "WHERE Listing.fk_seller = :sellerId")
    List<Review> getReviewsBySellerID(long sellerId);
}
