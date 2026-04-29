package com.example.universityjava;

import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universityjava.database.CartListing;
import com.example.universityjava.database.Listing;
import com.example.universityjava.database.WishlistListing;

import java.util.List;

public class ListingItemAdapter extends RecyclerView.Adapter<ListingItemAdapter.ListingViewHolder> {

    public enum ListingMode {
        EDITABLE,
        ADDABLE
    }
    private List<Listing> listings;
    private static ItemRecyclerViewEvent listener;
    public ListingMode listingMode;
    public boolean showDelete = true;
    public boolean showEdit = true;
    public boolean showWishlist = true;
    public boolean showCart = true;
    private final AppDatabase db = AppActivity.getDatabase();
    /// Listing list or Game list to work
    public ListingItemAdapter(List<Listing> list, ItemRecyclerViewEvent listener, ListingMode mode){
        ListingItemAdapter.listener = listener;
        this.listingMode = mode;
        if (list != null && !list.isEmpty()){
            this.listings = list;
        }
    }
    public static class ListingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final LinearLayout itemHolder;
        private Listing listing;
        private final TextView title;
        private final TextView price;
        private final ImageView image;
        private final TextView listingsFrom;
        private final ImageButton editButton;
        private final ImageButton deleteButton;
        private final ImageButton wishlistButton;
        private final ImageButton cartButton;
        private final ObjectAnimator CartAnimFill;
        private final ObjectAnimator CartAnimEmpty;
        private final ObjectAnimator WishlistAnimFill;
        private final ObjectAnimator WishlistAnimEmpty;
        int buttonTransDur = 300;
        public ListingViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.listing_name);
            price = (TextView) view.findViewById(R.id.listing_price);
            image = (ImageView) view.findViewById(R.id.listing_image);
            listingsFrom = (TextView) view.findViewById(R.id.listings_from);
            editButton = view.findViewById(R.id.buttonFirst);
            deleteButton = view.findViewById(R.id.buttonSecond);
            cartButton = view.findViewById(R.id.buttonCart);
            wishlistButton = view.findViewById(R.id.buttonWishlist);
            itemHolder = view.findViewById(R.id.item_holder);
            CartAnimFill = ObjectAnimator.ofInt(cartButton, "imageResource",
                    R.drawable.ic_shopping_cart_nofill_48, R.drawable.ic_shopping_cart_fill_48);
            CartAnimFill.setDuration(buttonTransDur);
            CartAnimEmpty = ObjectAnimator.ofInt(cartButton, "imageResource",
                    R.drawable.ic_shopping_cart_fill_48, R.drawable.ic_shopping_cart_nofill_48);
            CartAnimEmpty.setDuration(buttonTransDur);
            WishlistAnimFill = ObjectAnimator.ofInt(wishlistButton, "imageResource",
                    R.drawable.ic_favorite_nofill_38, R.drawable.ic_favorite_fill_38);
            WishlistAnimFill.setDuration(buttonTransDur);
            WishlistAnimEmpty = ObjectAnimator.ofInt(wishlistButton, "imageResource",
                    R.drawable.ic_favorite_fill_38, R.drawable.ic_favorite_nofill_38);
            WishlistAnimEmpty.setDuration(buttonTransDur);
            view.setOnClickListener(this);
        }

        public TextView getTitle(){
            return title;
        }
        public TextView getPrice(){
            return price;
        }
        public ImageView getImage(){
            return image;
        }
        public TextView getListingsFrom(){
            return listingsFrom;
        }
        public LinearLayout getItemHolder() {
            return itemHolder;
        }
        public ImageButton getWishlistButton(){
            return wishlistButton;
        }
        public ImageButton getEditButton(){
            return editButton;
        }
        public ImageButton getDeleteButton(){
            return deleteButton;
        }
        public ImageButton getCartButton(){
            return cartButton;
        }
        public void SetItem(Listing listing){
            this.listing = listing;
        }

        @Override
        public void onClick(View v) {
            int position = getAbsoluteAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                listener.onItemClick(listing);
            }
        }
    }
    @NonNull
    @Override
    public ListingItemAdapter.ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (listingMode) {
            case EDITABLE -> {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listing_item_editable, parent, false);
                return new ListingViewHolder(view);
            }
            default -> { // ADDABLE
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listing_item_addable, parent, false);
                return new ListingViewHolder(view);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ListingItemAdapter.ListingViewHolder holder, int position) {
        Listing listing = listings.get(position);
        holder.SetItem(listing);
        holder.getTitle().setText(db.listingDAO().getGameNameByListingId(listing.getId()));
        holder.getListingsFrom().setVisibility(View.GONE);
        holder.getImage().setImageResource(R.drawable.ic_launcher_background);
        holder.getPrice().setText(listing.getPrice() + " €");
        long userId = AppActivity.getCurrentUserID();
        if(listingMode == ListingMode.EDITABLE)
        {
            holder.editButton.setVisibility(showEdit ? ViewGroup.VISIBLE : View.GONE);
            holder.deleteButton.setVisibility(showDelete ? ViewGroup.VISIBLE : View.GONE);
            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "Editing!", Toast.LENGTH_SHORT).show();
                    Fragment fragment = new EditListingFragment();

                    //((MainActivity).getActivity()).replaceFragment(fragment);
                    // TODO: Add listing edit
                }
            });
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "Deleting!", Toast.LENGTH_SHORT).show();
                    // TODO: Add listing delete
                }
            });
        }
        else
        {
            holder.wishlistButton.setVisibility(showWishlist ? ViewGroup.VISIBLE : View.GONE);
            holder.cartButton.setVisibility(showCart ? ViewGroup.VISIBLE : View.GONE);

            if(!db.cartListingDAO().getCListingByListingAndUserID(listing.getId(),userId).isEmpty()){
                holder.cartButton.setImageResource(R.drawable.ic_shopping_cart_fill_48);
            }
            if(!db.wishlistListingDAO().getWListingByListingAndUserID(listing.getId(),userId).isEmpty()){
                holder.wishlistButton.setImageResource(R.drawable.ic_favorite_fill_38);
            }
            if(listing.getIssold()){
                holder.wishlistButton.setEnabled(false);
                holder.cartButton.setEnabled(false);
            }
            holder.wishlistButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "Wishlist!", Toast.LENGTH_SHORT).show();
                    var wListing = new WishlistListing();
                    wListing.setFk_listingid(listing.getId());
                    wListing.setFk_userid(userId);
                    if(db.wishlistListingDAO().getWListingByListingAndUserID(listing.getId(),userId).isEmpty()) {
                        db.wishlistListingDAO().insert(wListing);
                        holder.WishlistAnimFill.start();
                    }else{
                        db.wishlistListingDAO().removeWListingByListingAndUserID(listing.getId(), userId);
                        holder.WishlistAnimEmpty.start();
                    }
                }
            });
            holder.cartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    var wListing = new CartListing();
                    wListing.setFk_listingid(listing.getId());
                    wListing.setFk_userid(userId);
                    if(db.cartListingDAO().getCListingByListingAndUserID(listing.getId(),userId).isEmpty()) {
                        db.cartListingDAO().insert(wListing);
                        holder.CartAnimFill.start();
                    }else{
                        db.cartListingDAO().removeCListingByListingAndUserID(listing.getId(),userId);
                        holder.CartAnimEmpty.start();
                    }
                    Toast.makeText(view.getContext(), "Cart!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }
}
