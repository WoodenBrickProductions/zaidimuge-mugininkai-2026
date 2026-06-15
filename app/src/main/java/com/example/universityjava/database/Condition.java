package com.example.universityjava.database;
import android.app.Application;

import androidx.annotation.StringRes;
import androidx.room.TypeConverter;

import com.example.universityjava.AppActivity;
import com.example.universityjava.MainActivity;
import com.example.universityjava.R;

public enum Condition {
    New(R.string.condition_new),
    Like_new(R.string.condition_like_new),
    Very_good(R.string.condition_very_good),
    Good(R.string.condition_good),
    Acceptable(R.string.condition_acceptable);

    private int mResourceId;

    private Condition(int id) {
        mResourceId = id;
    }

    public int getResourceId() {
        return mResourceId;
    }
}

