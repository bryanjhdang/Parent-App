package cmpt276.as3.cmpt276hydrogenproject.model;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * class to store info of a finished task event upon an interaction
 * with the complete task button.
 */
public class TaskFinished {
    private String childProfilePicture;
    private String childName;
    private final LocalDateTime timeTaskCompleted;
    private final int id;

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

    public void setChildName(String childName) {
        this.childName = childName;
    }

    @NonNull
    @Override
    public String toString() {
        String string = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        string += childName + " did this task on: \n";
        string += formatter.format(timeTaskCompleted);
        return string;
    }
}
