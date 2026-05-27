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
    private String name = "test_name";
    @NonNull
    @ColumnInfo(name = "email")
    private String email = "test_email";
    @NonNull
    @ColumnInfo(name = "password")
    private String password = "test_password";
    @ColumnInfo(name = "profile_image")
    private String profileImage;

    public void setId(long id) { this.id = id; }
    public long getId() { return this.id; }
    public void setName(String name) { this.name = name; }
    public String getName() { return this.name; }
    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return this.email; }
    public void setPassword(String password) { this.password = password; }
    public String getPassword() { return this.password; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public String getProfileImage() { return this.profileImage; }

    public void generateTestData(long id)
    {
        this.id = id;
        this.name = "test_name_" + id;
        this.email = "test_" + id + "@email.com";
        this.password = "test";
    }

}
