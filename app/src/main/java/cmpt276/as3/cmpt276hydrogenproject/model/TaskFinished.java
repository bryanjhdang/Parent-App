package cmpt276.as3.cmpt276hydrogenproject.model;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskFinished {
    private String childProfilePicture;
    private String childName;
    private LocalDateTime timeTaskCompleted;
    private int id;

    public TaskFinished(String stringifiedPicture, String childName, int id) {
        this.childProfilePicture = stringifiedPicture;
        this.childName = childName;
        this.timeTaskCompleted = LocalDateTime.now();
        this.id = id;
    }

    public String getChildProfilePicture() {
        return childProfilePicture;
    }

    public void setChildProfilePicture(String childProfilePicture) {
        this.childProfilePicture = childProfilePicture;
    }

    public int getId() {
        return id;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public LocalDateTime getTimeTaskCompleted() {
        return timeTaskCompleted;
    }

    public void setTimeTaskCompleted(LocalDateTime timeTaskCompleted) {
        this.timeTaskCompleted = timeTaskCompleted;
    }

    @NonNull
    @Override
    public String toString() {
        String string = "";
//        if (timeTaskCompleted == null) {
//            string += "TIME NULL";
//        }
//        if (childName == null) {
//            string += "NAME NULL";
//        }
//        string += "  ";
//        return string;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        string += childName + " did this task on: \n";
        string += formatter.format(timeTaskCompleted);
        return string;
    }
}
