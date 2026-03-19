package com.example.universityjava;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.universityjava.database.Game;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsFragment extends Fragment implements RecyclerViewEvent {
    String _query;
    private List<Game> list;
    private RecyclerView recyclerView;

    public SearchResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null)
            _query = args.getString("query");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);
        SearchView searchView = view.findViewById(R.id.searchViewResult);
        searchView.setQuery(_query, false);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                Fragment fragment = new SearchResultsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("query", s);
                fragment.setArguments(bundle);
                ((MainActivity)getActivity()).replaceFragment(fragment);
                return true;
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewSearchResults);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        List<Integer> images = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            images.add(R.drawable.ic_game_test_icon);
        }
        list = AppActivity.getDatabase().gameDAO().getGamesByTitle(_query);
        ImageAdapter adapter = new ImageAdapter(getContext(), images);

//        recyclerView.setAdapter(new ListingItemAdapter(list, this));
        if(!list.isEmpty()) {
            var listingItemAdapter = new ListingItemAdapter(list, this, ListingItemAdapter.ListingMode.ADDABLE);
            recyclerView.setAdapter(listingItemAdapter);
        }

        return view;
    }

    @Override
    public void onItemClick(int position) {
        Toast toast = Toast.makeText(getContext(), list.get(position).getTitle(), Toast.LENGTH_SHORT);
        toast.show();
    }
}