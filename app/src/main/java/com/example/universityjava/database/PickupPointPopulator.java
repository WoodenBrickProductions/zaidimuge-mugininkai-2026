package com.example.universityjava.database;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

public class PickupPointPopulator {
    public static List<PickupPoint> loadPickupPoints(Context context) {
        try {
            InputStream is = context.getAssets().open("pickup_points.json");

            int size = is.available();
            byte[] buffer = new byte[size];

            is.read(buffer);
            is.close();

            String json = new String(buffer);
            Gson gson = new Gson();
            Type listType = new TypeToken<List<PickupPoint>>(){}.getType();

            return gson.fromJson(json, listType);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}