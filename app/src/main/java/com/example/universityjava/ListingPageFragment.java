package com.example.universityjava;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.universityjava.database.Game;
import com.example.universityjava.database.Listing;
import com.example.universityjava.database.PhysicalListingAttributes;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListingPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListingPageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private long listingID;
    private User seller;
    private Listing listing;
    private PhysicalListingAttributes physical;
    private Game game;
    private String mParam2;
    private View view;

    private ImageView listingImage;
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


    public ListingPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListingPageFragment.
     */
    // TODO: Rename and change types and number of parameters
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
            //View view = this.getView();



            listing = AppActivity.getDatabase().listingDAO().getListingByID(listingID);
            //Toast.makeText(getContext(),String.valueOf(listing.getId()), Toast.LENGTH_SHORT).show();
            if(listing.getIsdigital()){
                physical = AppActivity.getDatabase().physicalListingAttributesDAO().getPhysAttrByListingId(listingID);
            }
            seller = AppActivity.getDatabase().userDAO().getUserByID(listing.getFk_seller());
            game = AppActivity.getDatabase().gameDAO().getGameByID(listing.getFk_gameid());

        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View view = inflater.inflate(R.layout.fragment_home, container, false);
        view = inflater.inflate(R.layout.fragment_listing_page, container, false);

        listingImage = view.findViewById(R.id.listingImage);
        title = view.findViewById(R.id.listing_page_title);
        description = view.findViewById(R.id.listing_description);
        conditionStateTitle = view.findViewById(R.id.stateTitle);
        conditionStateTitle.append(":");
        platforms = view.findViewById(R.id.listing_platforms);
        TextView platformsTitle = view.findViewById(R.id.listing_platforms_title);
        platformsTitle.append(":");
        conditionState = view.findViewById(R.id.listing_condition);
        conditionDescription= view.findViewById(R.id.listing_condition_description);
        listingPrice= view.findViewById(R.id.page_listing_price);
        sellerImage =view.findViewById(R.id.user_profile_icon);
        sellerName = view.findViewById(R.id.username);
        sellerScore = view.findViewById(R.id.user_rating);
        wishlistButton = view.findViewById(R.id.add_to_wishlist_button);
        cartButton = view.findViewById(R.id.add_to_cart_button);

        if (getArguments() != null) {
            listingID = getArguments().getLong(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            listing = AppActivity.getDatabase().listingDAO().getListingByID(listingID);
            //Toast.makeText(getContext(),String.valueOf(listing.getId()), Toast.LENGTH_SHORT).show();
            if (!listing.getIsdigital()) {
                physical = AppActivity.getDatabase().physicalListingAttributesDAO().getPhysAttrByListingId(listingID);
            }
            else physical = null;
            seller = AppActivity.getDatabase().userDAO().getUserByID(listing.getFk_seller());
            game = AppActivity.getDatabase().gameDAO().getGameByID(listing.getFk_gameid());

            Bitmap bitmap;
            File imageFile = AppActivity.getCachedImageFile(
                    listingImage.getContext(), game.getImage());

            if(imageFile != null) {
                bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                listingImage.setImageBitmap(bitmap);
            } else {
                listingImage.setImageResource(R.drawable.ic_launcher_background);
            }

            String titletext = game.getTitle();
            title.setText(titletext);
            description.setText(game.getDescription());
            listingPrice.setText(String.valueOf(listing.getPrice()));
            String pl = AppActivity.getDatabase().listingDAO().getPlatformNameByListingId(listingID);
            platforms.setText(pl);
            Log.i("Listing: ", "IsDigital: "+ listing.getIsdigital()+" physicalAttr:"+physical);
            if (listing.getIsdigital()) {
                conditionStateTitle.setVisibility(View.GONE);
                conditionState.setVisibility(View.GONE);
                conditionDescription.setVisibility(View.GONE);
            } else if(physical != null) {
                conditionState.setText(physical.getFk_condition().getResourceId());
                conditionDescription.setText(physical.getCondition_description());
            }
            sellerName.setText(seller.getName());
            sellerScore.setText("0");
            if(listing.getIssold()){
                wishlistButton.setEnabled(false);
                cartButton.setEnabled(false);
            }
            if(!AppActivity.getDatabase().wishlistListingDAO().getWListingByListingAndUserID(listingID,
                    AppActivity.getCurrentUserID()).isEmpty()){
                wishlistButton.setSelected(true);
            }else wishlistButton.setSelected(false);
            if(!AppActivity.getDatabase().cartListingDAO().getCListingByListingAndUserID(listingID,
                    AppActivity.getCurrentUserID()).isEmpty()){
                cartButton.setSelected(true);
            } else cartButton.setSelected(false);
        }

        Button _buttonPhysical = (Button) view.findViewById(R.id.buttonSeller);
        _buttonPhysical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = SellerFragment.newInstance(listing.getFk_seller(), "");
                ((MainActivity)getActivity()).replaceFragment(fragment);
            }
        });

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cartButton.isSelected()){
                    cartButton.setSelected(false);
                }
                else cartButton.setSelected(true);
            }
        });
        wishlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wishlistButton.isSelected()){
                    wishlistButton.setSelected(false);
                }
                else wishlistButton.setSelected(true);
            }
        });
        return view;
    }
}