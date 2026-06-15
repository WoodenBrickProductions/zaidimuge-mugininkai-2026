package com.example.universityjava.database;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PhysicalListingAttributesDAO {
    @Insert
    void insert(PhysicalListingAttributes listingAttr);

    @Query("DELETE FROM PhysicalListingAttributes")
    void deleteAll();

    @Query("DELETE FROM PhysicalListingAttributes WHERE fk_listingid = :listingId")
    void deleteByListingId(long listingId);

    @Query("SELECT * FROM PhysicalListingAttributes")
    List<PhysicalListingAttributes> getAllListingsPhysAttr();

    @Query("SELECT * FROM PhysicalListingAttributes WHERE fk_listingid = :listingid LIMIT 1")
    PhysicalListingAttributes getPhysAttrByListingId(long listingid);
}
