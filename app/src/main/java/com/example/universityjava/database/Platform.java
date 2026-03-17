package com.example.universityjava.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity
public class Platform {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    private int id;
    @ColumnInfo(name="name")
    private String name;

    public void setId(int id) { this.id = id; }
    public int getId() { return this.id; }
    public void setName(String name) { this.name = name; }
    public String getName() { return this.name; }
}
