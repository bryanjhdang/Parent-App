package cmpt276.as3.cmpt276hydrogenproject.model;

import android.graphics.Bitmap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskFinished {
    private Bitmap childProfilePicture;
    private String childName;
    private LocalDateTime timeTaskCompleted;

    public TaskFinished(Bitmap bm, String childName) {
        this.childProfilePicture = bm;
        this.childName = childName;
        this.timeTaskCompleted = LocalDateTime.now();
    }

    public Bitmap getChildProfilePicture() {
        return childProfilePicture;
    }

    public void setChildProfilePicture(Bitmap childProfilePicture) {
        this.childProfilePicture = childProfilePicture;
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
        string += childName + " did this task on ";
        string += formatter.format(timeTaskCompleted);
        return string;
    }
}
