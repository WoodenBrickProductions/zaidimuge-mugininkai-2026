package com.example.universityjava;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.universityjava.database.Game;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    Button _buttonCreateListing;
    Button _buttonPopular;
    Button _buttonNewest;
    Button _buttonPhysical;
    DrawableDecoration _decoration;
    TextView mainText;
    ImageButton star1;
    ImageButton star2;
    ImageButton star3;
    ObjectAnimator rotation;
    List<Game> _popularGames;
    List<Game> _newestGames;
    List<Game> _physicalGames;

    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        _decoration = view.findViewById(R.id.view3);
        mainText = view.findViewById(R.id.textView2);
        star1 = view.findViewById(R.id.star1);
        star2 = view.findViewById(R.id.star2);
        star3 = view.findViewById(R.id.star3);

        Animator anim = AnimatorInflater.loadAnimator(getContext(), R.animator.logo_anim);
        anim.setTarget(mainText);
        anim.start();

        Animator anim1 = AnimatorInflater.loadAnimator(getContext(), R.animator.star_anim);
        anim1.setTarget(star1);

        Animator anim2 = AnimatorInflater.loadAnimator(getContext(), R.animator.star_anim);
        anim2.setTarget(star2);

        Animator anim3 = AnimatorInflater.loadAnimator(getContext(), R.animator.star_anim);
        anim3.setTarget(star3);

        Animator.AnimatorListener listener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                anim1.start();
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }
        };

        anim.addListener(listener);

        Animator.AnimatorListener listener2 = new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                anim2.start();
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }
        };

        anim1.addListener(listener2);

        Animator.AnimatorListener listener3 = new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                anim3.start();
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }
        };

        anim2.addListener(listener3);

        _buttonCreateListing = (Button) view.findViewById(R.id.buttonCreateListing);
        _buttonCreateListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new AddListingFragment();
                ((MainActivity)getActivity()).replaceFragment(fragment);
            }
        });

        //anim.addListener(listener);

        _buttonPopular = (Button) view.findViewById(R.id.buttonPopular);
        _buttonPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new MainCategoriesFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", getResources().getString(R.string.popular_items));
                bundle.putInt("category_type", 1);
                fragment.setArguments(bundle);
                ((MainActivity)getActivity()).replaceFragment(fragment);
            }
        });

        _buttonNewest = (Button) view.findViewById(R.id.buttonNewest);
        _buttonNewest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new MainCategoriesFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", getResources().getString(R.string.newest_items));
                bundle.putInt("category_type", 2);
                fragment.setArguments(bundle);
                ((MainActivity)getActivity()).replaceFragment(fragment);
            }
        });

        _buttonPhysical = (Button) view.findViewById(R.id.buttonPhysical);
        _buttonPhysical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new MainCategoriesFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", getResources().getString(R.string.physical_items));
                bundle.putInt("category_type", 3);
                fragment.setArguments(bundle);
                ((MainActivity)getActivity()).replaceFragment(fragment);
            }
        });

        SearchView searchView = view.findViewById(R.id.searchMain);
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

        RecyclerView popularRW = view.findViewById(R.id.listPopular);
        RecyclerView newestRW = view.findViewById(R.id.listNewest);
        RecyclerView physicalRW = view.findViewById(R.id.listPhysical);

        popularRW.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        newestRW.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        physicalRW.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

//        popularRW.setNestedScrollingEnabled(false);
//        newestRW.setNestedScrollingEnabled(false);
//        physicalRW.setNestedScrollingEnabled(false);

        List<Integer> images = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            images.add(R.drawable.ic_game_test_icon);
        }
        ImageAdapter popularAdapter = new ImageAdapter(getContext(), images);
        ImageAdapter newestAdapter = new ImageAdapter(getContext(), images);
        ImageAdapter physicalAdapter = new ImageAdapter(getContext(), images);

        _popularGames = AppActivity.getDatabase().gameDAO().getMostPopularGames(10);
        _newestGames = AppActivity.getDatabase().gameDAO().getNewestGames(10);
        _physicalGames = AppActivity.getDatabase().gameDAO().getMostPopularPhysicalGames(10);
        GameImageAdapter popularGameAdapter = new GameImageAdapter(_popularGames, (MainActivity)getActivity());
        GameImageAdapter newestGameAdapter = new GameImageAdapter(_newestGames, (MainActivity)getActivity());
        GameImageAdapter physicalGameAdapter = new GameImageAdapter(_physicalGames, (MainActivity)getActivity());

        popularRW.setAdapter(popularGameAdapter);
        newestRW.setAdapter(newestGameAdapter);
        physicalRW.setAdapter(physicalGameAdapter);

        rotation = ObjectAnimator.ofFloat(_decoration,"rotationY", 360);
        rotation.setInterpolator(new LinearInterpolator());
        rotation.setDuration(8000);
        if (AnimationSettings.areAnimationsEnabled())
            rotation.start();
        rotation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rotation.start();
            }
        });

        LinearLayout banner = view.findViewById(R.id.bannerHome);
        ((MainActivity) requireActivity()).gyroController.addBanner(banner);
//        ((MainActivity) requireActivity()).gyroController.addBanner(_buttonPopular);
//        ((MainActivity) requireActivity()).gyroController.addBanner(_buttonNewest);
//        ((MainActivity) requireActivity()).gyroController.addBanner(_buttonPhysical);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rotation != null) rotation.cancel();
    }
}