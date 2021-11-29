package cmpt276.as3.cmpt276hydrogenproject.model;

import android.graphics.Bitmap;

import java.time.LocalDateTime;

public class TaskFinished {
    private Bitmap childProfilePicture;
    private String childName;
    private LocalDateTime timeTaskCompleted;

    public TaskFinished(Bitmap bm, String childName, LocalDateTime timeTaskCompleted) {
        this.childProfilePicture = bm;
        this.childName = childName;
        this.timeTaskCompleted = timeTaskCompleted;
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
        return childName + "completed this task at:" +
                timeTaskCompleted;
    }
}
