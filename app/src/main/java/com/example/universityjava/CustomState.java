package com.example.universityjava;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Dictionary;
import java.util.List;
import java.util.stream.IntStream;

public class CustomState implements Parcelable {
    public ArrayList<String> data;

    public CustomState()
    {
        data = new ArrayList<String>();
    }

    protected CustomState(Parcel in) {
        data = in.createStringArrayList();
    }

    public static final Creator<CustomState> CREATOR = new Creator<CustomState>() {
        @Override
        public CustomState createFromParcel(Parcel in) {
            return new CustomState(in);
        }

        @Override
        public CustomState[] newArray(int size) {
            return new CustomState[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(data);
    }
}