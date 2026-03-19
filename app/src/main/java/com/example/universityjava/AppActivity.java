package com.example.universityjava;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
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
    static SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "my_app_db")
                .setQueryCallback((sqlQuery, bindArgs) -> {
                    Log.d("RoomQueryLog", "SQL Query: " + sqlQuery + " SQL Args: " + bindArgs);
                }, Executors.newSingleThreadExecutor())
                .createFromAsset("my_app_db.db")
                .allowMainThreadQueries().build();
        // use .fallbackToDestructiveMigration() before .setQueryCallback()
        // so the database can be updated - new tables added and such
        // will delete the data inside them though
        // setQueryCallback adds SQL messages to Logcat
        //.createFromAsset("my_app_db.db") to use pre-made database in assets folder

        prefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        generateTestData();
    }

    public static AppDatabase getDatabase() { return db; }

    public static long getCurrentUserID() {
        return prefs.getLong("user_id", -1);
    }

    private void generateTestData() {

        final int DUMMY_ID = 100000;

        User user1 = new User();
        User user2 = new User();

        user1.generateTestData(DUMMY_ID);
        user2.generateTestData(DUMMY_ID + 1);

        for(int i = 0; i < 10; i++)
        {
            Game game = new Game();
            game.generateTestData(DUMMY_ID + i);

            if (db.gameDAO().getGameByID(game.getId()) == null) {
                db.gameDAO().insert(game);
            }
        }


        Platform platform = new Platform();
        platform.setId(DUMMY_ID);
        platform.setName("PC");

        Listing listing = new Listing();
        listing.generateTestData(DUMMY_ID, DUMMY_ID, user1.getId(), platform.getId());

        Review review = new Review();
        review.generateTestData(listing.getId(), user2.getId());

        //PhysicalListingAttributes attributes = new PhysicalListingAttributes();

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
