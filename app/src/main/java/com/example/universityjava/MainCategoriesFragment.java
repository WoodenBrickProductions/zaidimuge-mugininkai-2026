package com.example.universityjava;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.universityjava.database.Game;
import com.example.universityjava.database.Listing;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MainCategoriesFragment extends Fragment{

    private String name;
    private int categoryType;
    private long gameId;
    private List<Game> list;
    private List<Listing> listingList;
    private boolean _isGameList = false;
    private RecyclerView recyclerView;

    public MainCategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            gameId = args.getLong("game_id");
            if (gameId > 0)
                _isGameList = true;
            name = args.getString("name");
            categoryType = args.getInt("category_type");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_categories, container, false);
        TextView textViewName = view.findViewById(R.id.textViewCategoryName);
        textViewName.setText(name);
        TextView textViewEmpty = view.findViewById(R.id.textViewCategoriesEmpty);
        textViewEmpty.setVisibility(View.GONE);

        SearchView searchView = view.findViewById(R.id.searchMainCategories);
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

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewMainCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        if (!_isGameList) {
            switch (categoryType) {
                case 1:
                    list = AppActivity.getDatabase().gameDAO().getMostPopularGames(30);
                    break;
                case 2:
                    list = AppActivity.getDatabase().gameDAO().getNewestGames(30);;
                    break;
                case 3:
                    list = AppActivity.getDatabase().gameDAO().getMostPopularPhysicalGames(30);
                    break;
                default:
                    list = AppActivity.getDatabase().gameDAO().getAllGames();
            }

            if(!list.isEmpty()) {
                var listingItemAdapter = new GameItemAdapter(list, (MainActivity)getActivity(), GameItemAdapter.ListingMode.ADDABLE);
                recyclerView.setAdapter(listingItemAdapter);
            }
            else {
                textViewEmpty.setVisibility(View.VISIBLE);
                textViewEmpty.setText("This category currently has no games");
            }
        }
        else {
            textViewName.setVisibility(View.GONE);
            listingList = AppActivity.getDatabase().gameDAO().getActiveListingsForGameSortedByPrice(gameId);
            if(!listingList.isEmpty()) {
                var listingItemAdapter = new ListingItemAdapter(listingList, (MainActivity)getActivity(), ListingItemAdapter.ListingMode.ADDABLE);
                recyclerView.setAdapter(listingItemAdapter);
            }
            else {
                textViewEmpty.setVisibility(View.VISIBLE);
                textViewEmpty.setText(name + " currently has no listings");
            }
        }


        return view;
    }

}