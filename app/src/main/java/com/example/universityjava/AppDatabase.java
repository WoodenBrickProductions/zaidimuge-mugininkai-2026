package com.example.universityjava;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.universityjava.database.*;


@Database(entities = {User.class, Game.class,Listing.class,
        PhysicalListingAttributes.class, Platform.class,
        Review.class, CartListing.class, WishlistListing.class,
        WishlistGame.class
}, version = 2)
//@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDAO userDAO();
    //public abstract ConditionDAO conditionDAO();
    public abstract GameDAO gameDAO();
    public abstract ListingDAO listingDAO();
    public abstract PhysicalListingAttributesDAO physicalListingAttributesDAO();
    public abstract PlatformDAO platformDAO();
    public abstract ReviewDAO reviewDAO();
    public abstract CartListingDAO cartListingDAO();
    public abstract WishlistListingDAO wishlistListingDAO();
    public abstract WishlistGameDAO wishlistGameDAO();
}
