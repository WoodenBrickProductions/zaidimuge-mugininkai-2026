package com.example.universityjava;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.universityjava.database.Game;
import com.example.universityjava.database.Listing;
import com.example.universityjava.database.Platform;
import com.example.universityjava.database.Review;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
        Themes.applyTheme(this);
        prepopulateGameIconCache(getApplicationContext());
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

        var gameIconsDir = new File(getApplicationContext().getCacheDir(), "game_icons");

        if(!gameIconsDir.exists()) {
            System.out.println("game_icons dir does not exist");
        }

        var gameIcons = gameIconsDir.listFiles();

        for(int i = 0; i < 10; i++)
        {
            Game game = new Game();
            game.generateTestData(DUMMY_ID + i);
            if(gameIcons.length > 0) {
                var fileName = gameIcons[i % gameIcons.length].getName();
                game.setImage(fileName.substring(0, fileName.lastIndexOf('.')));
            }

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

    private static final String GAME_ASSETS_FOLDER = "game_icons";

    public static void prepopulateGameIconCache(Context context) {
        File iconsDir = getOrCreateCacheFolder(context, GAME_ASSETS_FOLDER);

        try {
            String[] assetFiles = context.getAssets().list(GAME_ASSETS_FOLDER);
            if (assetFiles == null) return;

            for (String fileName : assetFiles) {
                File dest = new File(iconsDir, fileName);
                if (!dest.exists()) {
                    copyAssetToCache(context, GAME_ASSETS_FOLDER + "/" + fileName, dest);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getOrCreateCacheFolder(Context context, String folderName) {
        File folder = new File(context.getCacheDir(), folderName);
        folder.mkdirs();
        return folder;
    }

    private static void copyAssetToCache(Context context, String assetPath, File dest) {
        try (InputStream in = context.getAssets().open(assetPath);
             FileOutputStream out = new FileOutputStream(dest)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface OnImageSavedListener {
        void onSaved(@Nullable File savedFile);
    }

    public static ActivityResultLauncher<String> registerImagePickerLauncher(
            AppCompatActivity activity,
            String saveName,
            OnImageSavedListener listener
    ) {
        return activity.registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    File saved = (uri != null) ? savePickedImageToCache(activity, uri, saveName) : null;
                    if (listener != null) listener.onSaved(saved);
                }
        );
    }

    @Nullable
    public static File savePickedImageToCache(Context context, Uri uri, String saveName) {
        File dest = new File(context.getCacheDir(), saveName + ".png"); // ← swap to getFilesDir() for permanence
        try (InputStream in = context.getContentResolver().openInputStream(uri);
             FileOutputStream out = new FileOutputStream(dest)) {
            if (in == null) return null;
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            return dest;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static File getCachedImageFile(Context context, String name) {
        File file = new File(context.getCacheDir(), GAME_ASSETS_FOLDER + "/" + name + ".jpg");
        if (file.exists()) return file;

        file = new File(context.getCacheDir(), GAME_ASSETS_FOLDER + "/" + name + ".png");
        if (file.exists()) return file;

        return null;
    }

    @Nullable
    public static String getCachedImagePath(Context context, String name) {
        File file = getCachedImageFile(context, name);
        return file != null ? file.getAbsolutePath() : null;
    }
}
