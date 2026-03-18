package com.example.universityjava;

import android.app.Application;
import android.util.Log;

import androidx.room.Room;
import androidx.room.RoomDatabase;

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
    }

    public static AppDatabase getDatabase() { return db; }
}
