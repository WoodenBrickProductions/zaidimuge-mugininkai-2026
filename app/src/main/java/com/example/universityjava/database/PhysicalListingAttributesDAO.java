package com.example.universityjava.database;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PhysicalListingAttributesDAO {
    @Insert
    void insert(PhysicalListingAttributes listingAttr);

    @Update
    void update(PhysicalListingAttributes listingAttr);

    @Query("UPDATE PhysicalListingAttributes SET condition_description = :desc WHERE fk_listingid = :id")
    void updateDescription(long id, String desc);

    @Query("DELETE FROM PhysicalListingAttributes")
    void deleteAll();

    @Query("DELETE FROM PhysicalListingAttributes WHERE fk_listingid = :listingId")
    void deleteByListingId(long listingId);

    @Query("SELECT * FROM PhysicalListingAttributes")
    List<PhysicalListingAttributes> getAllListingsPhysAttr();

    @Query("SELECT * FROM PhysicalListingAttributes WHERE fk_listingid = :listingid LIMIT 1")
    PhysicalListingAttributes getPhysAttrByListingId(long listingid);
}
