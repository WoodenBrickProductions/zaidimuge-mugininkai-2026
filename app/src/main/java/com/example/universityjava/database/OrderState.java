package com.example.universityjava.database;

import com.example.universityjava.R;

public enum OrderState {
    Pending(R.string.orderstate_pending),
    Confirmed(R.string.orderstate_confirmed),
    Shipped(R.string.orderstate_shipped),
    Delivered(R.string.orderstate_delivered),
    Cancelled(R.string.orderstate_cancelled);

    private int mResourceId;

    private OrderState(int id) {
        mResourceId = id;
    }

    public int getResourceId() {
        return mResourceId;
    }
}
