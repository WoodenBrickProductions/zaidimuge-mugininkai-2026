package com.example.universityjava;

import static android.app.PendingIntent.getActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universityjava.database.CartListing;
import com.example.universityjava.database.Listing;
import com.example.universityjava.database.WishlistListing;

import java.io.File;
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
    public boolean isInWishlistFragment;
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
        private final Animator wishlistBounce;
        private final Animator cartBounce;
        private final Animator editBounce;
        private final Animator deleteBounce;
        private final Animator fadeOutListing;
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
            view.setOnClickListener(this);
            fadeOutListing = AnimatorInflater.loadAnimator(view.getContext(),R.animator.listing_fade_out);
            fadeOutListing.setTarget(itemHolder);
            wishlistBounce = AnimatorInflater.loadAnimator(view.getContext(), R.animator.overshoot_bounce);
            cartBounce = AnimatorInflater.loadAnimator(view.getContext(), R.animator.overshoot_bounce);
            editBounce = AnimatorInflater.loadAnimator(view.getContext(), R.animator.overshoot_bounce);
            deleteBounce = AnimatorInflater.loadAnimator(view.getContext(), R.animator.overshoot_bounce);
            cartBounce.setTarget(cartButton);
            editBounce.setTarget(editButton);
            deleteBounce.setTarget(deleteButton);
            wishlistBounce.setTarget(wishlistButton);
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
        holder.getPrice().setText(listing.getPrice() + " €");
        long userId = AppActivity.getCurrentUserID();
        setImage(holder.getImage(), db.gameDAO().getGameByID(listing.getFk_gameid()).getImage());
        if(listingMode == ListingMode.EDITABLE)
        {
            holder.editButton.setVisibility(showEdit ? ViewGroup.VISIBLE : View.GONE);
            holder.deleteButton.setVisibility(showDelete ? ViewGroup.VISIBLE : View.GONE);
            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "Editing!", Toast.LENGTH_SHORT).show();
                    holder.editBounce.start();
                    listener.onEditClick(listing);
                    // TODO: Add listing edit
                }
            });
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "Deleting!", Toast.LENGTH_SHORT).show();
                    holder.deleteBounce.start();
                    if(isInWishlistFragment){
                        db.wishlistListingDAO().removeWListingByListingAndUserID(listing.getId(),userId);
                    }else{
                        db.cartListingDAO().removeCListingByListingAndUserID(listing.getId(),userId);
                    }
                    holder.fadeOutListing.start();
                    listings.remove(listing);
                    notifyItemRemoved(holder.getBindingAdapterPosition());
                    notifyItemRangeChanged(holder.getBindingAdapterPosition(), listings.size());
                }
            });
        }
        else
        {
            holder.wishlistButton.setVisibility(showWishlist ? ViewGroup.VISIBLE : View.GONE);
            holder.cartButton.setVisibility(showCart ? ViewGroup.VISIBLE : View.GONE);

            if(!db.cartListingDAO().getCListingByListingAndUserID(listing.getId(),userId).isEmpty()){
                holder.cartButton.setSelected(true);
            }
            if(!db.wishlistListingDAO().getWListingByListingAndUserID(listing.getId(),userId).isEmpty()){
                holder.wishlistButton.setSelected(true);
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
                    holder.wishlistBounce.start();
                    if(db.wishlistListingDAO().getWListingByListingAndUserID(listing.getId(),userId).isEmpty()) {
                        db.wishlistListingDAO().insert(wListing);
                        holder.wishlistButton.setSelected(true);
                    }else{
                        db.wishlistListingDAO().removeWListingByListingAndUserID(listing.getId(), userId);
                        holder.wishlistButton.setSelected(false);
                    }
                }
            });
            holder.cartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    var wListing = new CartListing();
                    wListing.setFk_listingid(listing.getId());
                    wListing.setFk_userid(userId);
                    holder.cartBounce.start();
                    if(db.cartListingDAO().getCListingByListingAndUserID(listing.getId(),userId).isEmpty()) {
                        db.cartListingDAO().insert(wListing);
                        holder.cartButton.setSelected(true);
                    }else{
                        db.cartListingDAO().removeCListingByListingAndUserID(listing.getId(),userId);
                        holder.cartButton.setSelected(false);
                    }
                    //Toast.makeText(view.getContext(), "Cart!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(listings != null)
            return listings.size();
        return -1;
    }

    private void setImage(ImageView imageView, String imageName) {
        Bitmap bitmap;
        File imageFile = AppActivity.getCachedImageFile(
                imageView.getContext(), imageName);

        if(imageFile != null) {
            bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_background);
        }
    }
}
