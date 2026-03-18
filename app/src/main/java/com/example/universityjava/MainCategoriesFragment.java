package com.example.universityjava;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainCategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainCategoriesFragment extends Fragment {

    String name;

    public MainCategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null)
            name = args.getString("name");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_categories, container, false);
        TextView textViewName = view.findViewById(R.id.textViewCategoryName);
        textViewName.setText(name);
        return view;
    }
}