package cmpt276.as3.cmpt276hydrogenproject.model;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

public class Child {
    String name;

    String uriProfilePicture;

    Bitmap bitmapProfilePicture = null;

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

    public String getStringProfilePicture() {
        return uriProfilePicture;
    }

    public void setStringProfilePicture(String profilePicture) {
        this.uriProfilePicture = profilePicture;
    }

    public Bitmap getBitmapProfilePicture() {
        return bitmapProfilePicture;
    }

    public void setBitmapProfilePicture(Bitmap profilePicture) {
        this.bitmapProfilePicture = profilePicture;
    }
}
