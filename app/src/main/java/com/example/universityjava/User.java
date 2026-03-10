package com.example.universityjava;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo()
    private long id;
    @NonNull
    @ColumnInfo(name = "user_name")
    private String name;
    @NonNull
    @ColumnInfo(name = "email")
    private String email;
    @NonNull
    @ColumnInfo(name = "password")
    private String password;

    public void setId(long id) { this.id = id; }
    public long getId() { return this.id; }
    public void setName(String name) { this.name = name; }
    public String getName() { return this.name; }
    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return this.email; }
    public void setPassword(String password) { this.password = password; }
    public String getPassword() { return this.password; }

}
