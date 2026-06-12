package com.example.universityjava.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Order {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    private long id;
    @ColumnInfo(name = "fk_userid")
    private long fk_userid;
    @ColumnInfo(name = "fk_listingid")
    private long fk_listingid;
    @ColumnInfo(name = "fk_pickuppoint")
    private String fk_pickuppoint;
    @ColumnInfo(name = "orderstate")
    private OrderState orderstate;

    public void setId(long id) { this.id = id; }
    public long getId() { return this.id; }

    public void setFk_userid(long fk_userid) { this.fk_userid = fk_userid; }
    public long getFk_userid() { return this.fk_userid; }

    public void setFk_listingid(long fk_listingid) { this.fk_listingid = fk_listingid; }
    public long getFk_listingid() { return this.fk_listingid; }

    public void setFk_pickuppoint(String fk_pickuppoint) { this.fk_pickuppoint = fk_pickuppoint; }
    public String getFk_pickuppoint() { return this.fk_pickuppoint; }

    public OrderState getOrderstate() {return orderstate;}
    public void setOrderstate(OrderState orderstate){ this.orderstate = orderstate; }
}
