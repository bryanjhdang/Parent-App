package cmpt276.as3.cmpt276hydrogenproject.model;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.util.Random;

import cmpt276.as3.cmpt276hydrogenproject.R;

public class Child {
    String name;
    int childID;
    String uriProfilePicture;

    Bitmap bitmapProfilePicture = null;

    public Child(String name, String profilePicture) {
        this.name = name;
        generateID();
        this.uriProfilePicture = profilePicture;
    }

    private void generateID() {
        Random random = new Random();
        this.childID = random.nextInt(1000000);
    }

    public int getChildID() {
        return childID;
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
