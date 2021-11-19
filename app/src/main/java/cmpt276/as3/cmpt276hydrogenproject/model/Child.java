package cmpt276.as3.cmpt276hydrogenproject.model;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

public class  Child {
    String name;

    String uriProfilePicture;

    public Child(String name, String profilePicture) {
        this.name = name;
        this.uriProfilePicture = profilePicture;
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

    public String getProfilePicture() {
        return uriProfilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.uriProfilePicture = profilePicture;
    }
}
