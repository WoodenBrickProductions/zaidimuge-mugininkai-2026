package com.example.universityjava.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"fk_userid","fk_gameid"})
public class WishlistGame {
    @ColumnInfo(name = "fk_userid")
    private long fk_userid;
    @ColumnInfo(name = "fk_gameid")
    private long fk_gameid;

    public void setFk_userid(long fk_userid) { this.fk_userid = fk_userid; }
    public long getFk_userid() { return this.fk_userid; }

    public void setFk_gameid(long fk_gameid) { this.fk_gameid = fk_gameid; }
    public long getFk_gameid() { return this.fk_gameid; }
}
