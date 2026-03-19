package com.example.universityjava;

import android.app.Application;
import android.util.Log;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.universityjava.database.Game;
import com.example.universityjava.database.Listing;
import com.example.universityjava.database.Platform;
import com.example.universityjava.database.Review;

import java.util.concurrent.Executors;

public class AppActivity extends Application {
    static AppDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "my_app_db")
                .setQueryCallback((sqlQuery, bindArgs) -> {
                    Log.d("RoomQueryLog", "SQL Query: " + sqlQuery + " SQL Args: " + bindArgs);
                }, Executors.newSingleThreadExecutor())
                .allowMainThreadQueries().build();
        // use .fallbackToDestructiveMigration() before .setQueryCallback()
        // so the database can be updated - new tables added and such
        // will delete the data inside them though
        // setQueryCallback adds SQL messages to Logcat
        //.createFromAsset("my_app_db.db") to use pre-made database in assets folder

        generateTestData();
    }

    public static AppDatabase getDatabase() { return db; }

    private void generateTestData() {

        final int DUMMY_ID = 100000;

        User user1 = new User();
        User user2 = new User();

        user1.setId(DUMMY_ID);
        user2.setId(DUMMY_ID + 1);

        Game game = new Game();

        Listing listing = new Listing();

        Platform platform = new Platform();

        Review review = new Review();

        game.setId(DUMMY_ID);

        platform.setId(DUMMY_ID);
        platform.setName("PC");

        listing.setId(DUMMY_ID);
        listing.setFk_seller(user1.getId());
        listing.setFk_platform(platform.getId());
        listing.setFk_gameid(game.getId());

        review.setFk_listingid(listing.getId());
        review.setFk_buyerid(user2.getId());

        //PhysicalListingAttributes attributes = new PhysicalListingAttributes();

        if (db.gameDAO().getGameByID(game.getId()) == null) {
            db.gameDAO().insert(game);
        }

        if (db.platformDAO().getPlatformByID(platform.getId()) == null) {
            db.platformDAO().insert(platform);
        }

        if (db.userDAO().getUserByID(user1.getId()) == null) {
            db.userDAO().insert(user1);
        }

        if (db.userDAO().getUserByID(user2.getId()) == null) {
            db.userDAO().insert(user2);
        }

        if (db.listingDAO().getListingByID(listing.getId()) == null) {
            db.listingDAO().insert(listing);
        }

        if (db.reviewDAO().getReviewByID(review.getFk_listingid()) == null) {
            db.reviewDAO().insert(review);
        }
    }
}
