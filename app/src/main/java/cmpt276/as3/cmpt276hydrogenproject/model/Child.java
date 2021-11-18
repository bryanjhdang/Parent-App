package cmpt276.as3.cmpt276hydrogenproject.model;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

public class Child {
    String name;

    Bitmap profilePicture;

    public Child(String name, Bitmap profilePicture) {
        this.name = name;
        this.profilePicture = profilePicture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public Bitmap getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Bitmap profilePicture) {
        this.profilePicture = profilePicture;
    }
}
