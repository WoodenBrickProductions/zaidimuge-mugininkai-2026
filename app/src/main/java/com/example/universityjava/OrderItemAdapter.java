package com.example.universityjava;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.universityjava.database.CartListing;
import com.example.universityjava.database.Listing;
import com.example.universityjava.database.Order;
import com.example.universityjava.database.OrderState;
import com.example.universityjava.database.WishlistListing;

import java.io.File;
import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderViewHolder> {

    public enum OrderMode {
        PURCHASES,
        SALES
    }
    private List<Order> orders;
    //private List<Listing> listings;
    private static OrderRecyclerViewEvent listener;
    public OrderMode listingMode;
    public boolean showReview = true;
    public boolean showEditReview = false;
    //public boolean showWishlist = true;
    private final AppDatabase db = AppActivity.getDatabase();

    public OrderItemAdapter(List<Order> list, OrderRecyclerViewEvent listener, OrderMode mode){
        OrderItemAdapter.listener = listener;
        this.listingMode = mode;
        if (list != null && !list.isEmpty()){
            this.orders = list;
        }
    }
    public static class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final LinearLayout itemHolder;
        private final View view;
        private Order order;
        private Listing listing;
        private final TextView title;
        private final TextView price;
        private final ImageView image;
        private final TextView orderState;
        private final Button editButton;
        private final Button reviewButton;

        private final CustomDropdown orderStateDropdown;

        private final Animator fadeOutListing;
        //private final Animation slideInListing;
        //private final AnimationSet set;
        public OrderViewHolder(View view) {
            super(view);
            this.view = view;
            title = (TextView) view.findViewById(R.id.listing_name);
            price = (TextView) view.findViewById(R.id.listing_price);
            image = (ImageView) view.findViewById(R.id.listing_image);
            orderState = (TextView) view.findViewById(R.id.order_state);
            editButton = view.findViewById(R.id.buttonEditReview);
            reviewButton = view.findViewById(R.id.buttonLeaveReview);
            orderStateDropdown = view.findViewById(R.id.order_state_dropdown);
            itemHolder = view.findViewById(R.id.item_holder);
            view.setOnClickListener(this);
            fadeOutListing = AnimatorInflater.loadAnimator(view.getContext(),R.animator.listing_fade_out);
            fadeOutListing.setTarget(itemHolder);
            //slideInListing = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_in_listing);
            //set = new AnimationSet(true);
            //set.addAnimation(slideInListing);
            //LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);

            //set.addAnimation(slideInListing);

            //slideInListing.setTarget(itemHolder);
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
        public TextView getOrderState(){
            return orderState;
        }
        public LinearLayout getItemHolder() {
            return itemHolder;
        }
        public Button getReviewButton(){
            return reviewButton;
        }
        public Button getEditButton(){
            return editButton;
        }
        public CustomDropdown getOrderStateDropdown(){
            return orderStateDropdown;
        }
        public void SetOrder(Order order){
            this.order = order;
        }
        public void SetListing(Listing listing){
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
    public OrderItemAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (listingMode) {
            case PURCHASES -> {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listing_item_purchases, parent, false);
                return new OrderViewHolder(view);
            }
            default -> { // SALES
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listing_item_sales, parent, false);
                return new OrderViewHolder(view);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemAdapter.OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        Listing listing = db.listingDAO().getListingByID(order.getFk_listingid());
        holder.SetOrder(order);
        holder.SetListing(listing);
        holder.getTitle().setText(db.listingDAO().getGameNameByListingId(listing.getId()));
        holder.getPrice().setText(listing.getPrice() + " €");
        long userId = AppActivity.getCurrentUserID();
        setImage(holder.getImage(), db.gameDAO().getGameByID(listing.getFk_gameid()).getImage());
        if(listingMode == OrderMode.PURCHASES)
        {
            if(order.getOrderstate() != OrderState.Delivered) showReview = false;
            holder.editButton.setVisibility(showEditReview ? ViewGroup.VISIBLE : View.GONE);
            holder.reviewButton.setVisibility(showReview ? ViewGroup.VISIBLE : View.GONE);
            holder.orderState.setText(order.getOrderstate().getResourceId());
            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "Editing review!", Toast.LENGTH_SHORT).show();
                    listener.onEditClick(listing);
                    // TODO: Add listing edit
                }
            });
            holder.reviewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "Reviewing!", Toast.LENGTH_SHORT).show();
                    listener.onReviewClick(listing);
                }
            });
        }
        else
        {
            // TODO: Dropdown stuff
        }
    }

    @Override
    public int getItemCount() {
        if(orders != null)
            return orders.size();
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
