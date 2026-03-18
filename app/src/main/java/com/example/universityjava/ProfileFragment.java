package com.example.universityjava;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        SharedPreferences prefs = getContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);

        AppDatabase db = AppActivity.getDatabase();
        TextView textViewUsername = view.findViewById(R.id.textViewUsername);
        TextView textViewRating = view.findViewById(R.id.textViewUserRating);
        ImageView imageView = view.findViewById(R.id.imageViewProfile);
        imageView.setImageResource(R.drawable.baseline_person_outline_24);
        Button _buttonMyListings = view.findViewById(R.id.buttonMyListings);
        _buttonMyListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        Button _buttonMyHistory = view.findViewById(R.id.buttonMyHistory);
        _buttonMyHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        Button _buttonLogOut = view.findViewById(R.id.buttonLogOut);
//        _buttonLogOut.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_favorite_border_24, 0,0,0);

        _buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getBaseContext(), LoginActivity.class);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("user_id", -1);
                editor.apply();
                startActivity(intent);
            }
        });

        long userID = prefs.getLong("user_id", -1);
        if (userID >= 0) {
            User user = db.userDAO().getUserByID(userID);
            textViewUsername.setText(user.getName());
            textViewRating.setText("");
        }


        return view;
    }
}