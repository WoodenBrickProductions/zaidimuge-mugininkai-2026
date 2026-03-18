package com.example.universityjava.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"fk_userid","fk_listingid"})
public class CartListing {
    @ColumnInfo(name = "fk_userid")
    private long fk_userid;
    @ColumnInfo(name = "fk_listingid")
    private long fk_listingid;

    public void setFk_userid(long fk_userid) { this.fk_userid = fk_userid; }
    public long getFk_userid() { return this.fk_userid; }

    public void setFk_listingid(long fk_listingid) { this.fk_listingid = fk_listingid; }
    public long getFk_listingid() { return this.fk_listingid; }
}
