package com.example.universityjava;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.universityjava.database.Condition;
import com.example.universityjava.database.Game;
import com.example.universityjava.database.Listing;
import com.example.universityjava.database.PhysicalListingAttributes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListingPageFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private long listingID;
    private User seller;
    private Listing listing;
    private PhysicalListingAttributes physical;
    private Game game;
    private String mParam2;
    private View view;

    private ViewPager2 imagePager;
    private TextView title;
    private TextView description;
    private TextView conditionStateTitle;
    private TextView conditionState;
    private TextView conditionDescription;
    private TextView listingPrice;
    private TextView platforms;
    private ImageView sellerImage;
    private TextView sellerName;
    private TextView sellerScore;
    private Button wishlistButton;
    private Button cartButton;

    public ListingPageFragment() {}

    public static ListingPageFragment newInstance(long listingID, String param2) {
        ListingPageFragment fragment = new ListingPageFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, listingID);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listingID = getArguments().getLong(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            listing = AppActivity.getDatabase().listingDAO().getListingByID(listingID);
            if (!listing.getIsdigital()) {
                physical = AppActivity.getDatabase().physicalListingAttributesDAO().getPhysAttrByListingId(listingID);
            }
            seller = AppActivity.getDatabase().userDAO().getUserByID(listing.getFk_seller());
            game = AppActivity.getDatabase().gameDAO().getGameByID(listing.getFk_gameid());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_listing_page, container, false);

        imagePager = view.findViewById(R.id.listingImagePager);
        title = view.findViewById(R.id.listing_page_title);
        description = view.findViewById(R.id.listing_description);
        conditionStateTitle = view.findViewById(R.id.stateTitle);
        conditionStateTitle.append(":");
        platforms = view.findViewById(R.id.listing_platforms);
        ((TextView) view.findViewById(R.id.listing_platforms_title)).append(":");
        conditionState = view.findViewById(R.id.listing_condition);
        conditionDescription = view.findViewById(R.id.listing_condition_description);
        listingPrice = view.findViewById(R.id.page_listing_price);
        sellerImage = view.findViewById(R.id.user_profile_icon);
        sellerName = view.findViewById(R.id.username);
        sellerScore = view.findViewById(R.id.user_rating);
        wishlistButton = view.findViewById(R.id.add_to_wishlist_button);
        cartButton = view.findViewById(R.id.add_to_cart_button);

        if (getArguments() != null) {
            listingID = getArguments().getLong(ARG_PARAM1);
            listing = AppActivity.getDatabase().listingDAO().getListingByID(listingID);
            if (!listing.getIsdigital()) {
                physical = AppActivity.getDatabase().physicalListingAttributesDAO().getPhysAttrByListingId(listingID);
            } else physical = null;
            seller = AppActivity.getDatabase().userDAO().getUserByID(listing.getFk_seller());
            game = AppActivity.getDatabase().gameDAO().getGameByID(listing.getFk_gameid());

            title.setText(game.getTitle());
            description.setText(game.getDescription());
            listingPrice.setText(listing.getPrice() + " €");
            platforms.setText(AppActivity.getDatabase().listingDAO().getPlatformNameByListingId(listingID));

            conditionStateTitle.setVisibility(View.GONE);
            conditionState.setVisibility(View.GONE);
            conditionDescription.setVisibility(View.GONE);

            Log.i("Listing: ", "IsDigital: " + listing.getIsdigital() + " physicalAttr:" + physical);
            if (!listing.getIsdigital() && physical != null && physical.getFk_condition() != null) {
                conditionStateTitle.setVisibility(View.VISIBLE);
                conditionState.setVisibility(View.VISIBLE);
                conditionState.setText(physical.getFk_condition().getResourceId());
                if (physical.getCondition_description() != null
                        && !physical.getCondition_description().isEmpty()) {
                    conditionDescription.setText(physical.getCondition_description());
                    conditionDescription.setVisibility(View.VISIBLE);
                }
            }
            sellerName.setText(seller.getName());
            double rating = AppActivity.getDatabase().reviewDAO().getAverageRatingBySellerID(seller.getId());
            sellerScore.setText(String.format("%,.2f",rating));

            if (listing.getFk_seller() == AppActivity.getCurrentUserID()) {
                wishlistButton.setVisibility(View.GONE);
                cartButton.setVisibility(View.GONE);
            } else {
                if (listing.getIssold()) {
                    wishlistButton.setEnabled(false);
                    cartButton.setEnabled(false);
                }
                wishlistButton.setSelected(!AppActivity.getDatabase().wishlistListingDAO()
                        .getWListingByListingAndUserID(listingID, AppActivity.getCurrentUserID()).isEmpty());
                cartButton.setSelected(!AppActivity.getDatabase().cartListingDAO()
                        .getCListingByListingAndUserID(listingID, AppActivity.getCurrentUserID()).isEmpty());
            }

            loadImages();

            List<String> downloadNames = new ArrayList<>();
            if (game.getImage() != null) downloadNames.add(game.getImage());
            if (!listing.getIsdigital()) {
                if (listing.getPhysicalPhoto1() != null) downloadNames.add(listing.getPhysicalPhoto1());
                if (listing.getPhysicalPhoto2() != null) downloadNames.add(listing.getPhysicalPhoto2());
                if (listing.getPhysicalPhoto3() != null) downloadNames.add(listing.getPhysicalPhoto3());
            }
            if (seller.getProfileImage() != null) downloadNames.add(seller.getProfileImage());

            ImageManager.downloadBatch(requireContext(), downloadNames, () -> {
                if (isAdded()) loadImages();
            });
        }

        view.findViewById(R.id.back_button).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        view.findViewById(R.id.buttonSeller).setOnClickListener(v ->
                ((MainActivity) requireActivity()).replaceFragment(
                        SellerFragment.newInstance(listing.getFk_seller(), "")));

        Button deleteButton = view.findViewById(R.id.buttonDeleteListing);
        ImageButton editButton = view.findViewById(R.id.buttonEditListing);
        if (listing != null && listing.getFk_seller() == AppActivity.getCurrentUserID()) {
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(v ->
                    new AlertDialog.Builder(requireContext())
                            .setMessage("Remove this listing?")
                            .setPositiveButton("Yes", (d, w) -> {
                                AppActivity.deleteListing(listing.getId());
                                requireActivity().getSupportFragmentManager().popBackStack();
                            })
                            .setNegativeButton("No", null)
                            .show());
            editButton.setVisibility(View.VISIBLE);
            editButton.setOnClickListener(v ->
                    ((MainActivity) requireActivity()).replaceFragment(
                            EditListingFragment.newInstance(listing.getId())));
        }

        cartButton.setOnClickListener(v -> cartButton.setSelected(!cartButton.isSelected()));
        wishlistButton.setOnClickListener(v -> wishlistButton.setSelected(!wishlistButton.isSelected()));

        return view;
    }

    private void loadImages() {
        List<String> imagePaths = new ArrayList<>();
        if (game != null) {
            File iconFile = AppActivity.getCachedImageFile(requireContext(), game.getImage());
            imagePaths.add(iconFile != null ? iconFile.getAbsolutePath() : null);
        }

        if (listing != null && !listing.getIsdigital()) {
            String[] photos = {listing.getPhysicalPhoto1(), listing.getPhysicalPhoto2(), listing.getPhysicalPhoto3()};
            for (String photoName : photos) {
                if (photoName != null) {
                    File f = AppActivity.getCachedImageFile(requireContext(), photoName);
                    if (f != null) imagePaths.add(f.getAbsolutePath());
                }
            }
        }

        imagePager.setAdapter(new ImagePagerAdapter(imagePaths));
        imagePager.setUserInputEnabled(imagePaths.size() > 1);

        if (seller != null && seller.getProfileImage() != null) {
            File f = AppActivity.getCachedImageFile(requireContext(), seller.getProfileImage());
            if (f != null) sellerImage.setImageURI(Uri.fromFile(f));
        }
    }

    // Minimal ViewPager2 adapter — creates ImageViews programmatically, no separate layout needed
    private static class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.VH> {
        private final List<String> paths;

        ImagePagerAdapter(List<String> paths) { this.paths = paths; }

        static class VH extends RecyclerView.ViewHolder {
            ImageView imageView;
            VH(ImageView v) { super(v); imageView = v; }
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView iv = new ImageView(parent.getContext());
            iv.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return new VH(iv);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            String path = paths.get(position);
            if (path != null) {
                Bitmap bm = BitmapFactory.decodeFile(path);
                if (bm != null) { holder.imageView.setImageBitmap(bm); return; }
            }
            holder.imageView.setImageResource(R.drawable.ic_launcher_background);
        }

        @Override public int getItemCount() { return paths.size(); }
    }
}
