package cmpt276.as3.cmpt276hydrogenproject.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Queue;

public class ChildManager {
    private ArrayList<Child> CHILDREN_LIST = new ArrayList<>();
    private ArrayList<Child> COIN_FLIP_QUEUE = new ArrayList<>();
    private static ChildManager instance;

    /**
     * Method to retrieve the class without accessing it by the constructor
     * @return the only instance of the class
     */
    public static ChildManager getInstance() {
        if (instance == null) {
            instance = new ChildManager();
        }
        return instance;
    }

    private ChildManager() {}

    public Child getChildAt(int index) {
        return CHILDREN_LIST.get(index);
    }

    public Child getChildFromCoinFlipQueueAt(int index) {
        return COIN_FLIP_QUEUE.get(index);
    }

    public Child getFirstChild() {
        return CHILDREN_LIST.get(0);
    }

    public Child getNextChildInCoinFlipQueue(Child previousPick) {
        int index = COIN_FLIP_QUEUE.indexOf(previousPick);
        index++;
        if (index == COIN_FLIP_QUEUE.size()) {
            index = 0;
        }
        return COIN_FLIP_QUEUE.get(index);
    }

    public int indexOfChildInCoinFlipQueue(Child child) {
        return COIN_FLIP_QUEUE.indexOf(child);
    }

    public ArrayList<Child> getChildrenList() {
        return CHILDREN_LIST;
    }

    public ArrayList<Child> getQueue() {
        return COIN_FLIP_QUEUE;
    }

    public void setAllChildren(ArrayList<Child> childList) {
        this.CHILDREN_LIST = childList;
    }

    public int getSizeOfChildList() { return CHILDREN_LIST.size(); }

    public void addChild(String name, String profilePic) {
        Child child = new Child(name, profilePic);
        CHILDREN_LIST.add(child);
//        COIN_FLIP_QUEUE.add(child);
    }

    public boolean containsChild (Child child) {
        return CHILDREN_LIST.contains(child);
    }

    public boolean isEmpty() {
        return CHILDREN_LIST.isEmpty();
    }

    public void removeChildByIdx(int idx) {
        CHILDREN_LIST.remove(idx);
    }

    public void removeChildByObject(Child child) {
        CHILDREN_LIST.remove(child);
//        COIN_FLIP_QUEUE.remove(child);
    }

    public void editChildName(int idx, String name) {
        Child child = CHILDREN_LIST.get(idx);
        child.setName(name);
    }

    //code inspiration from https://stackoverflow.com/questions/18072448/how-to-save-image-in-shared-preference-in-android-shared-preference-issue-in-a
    //this code is to help with the decoding and encoding of a bitmap into an image when read into the program via shared preferences
    public String encodeToBase64(Bitmap image) {
        Bitmap newImage = image;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        newImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] b = byteArrayOutputStream.toByteArray();
        String stringifiedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return stringifiedImage;
    }

    public Bitmap decodeToBase64(String userInput) {
        byte[] decodedString = Base64.decode(userInput, 0);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public static boolean isValidName(String name) {
        if (name.length() == 0) {
            return false;
        }

        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) != ' ') {
                return true;
            }
        }
        return false;
    }
}