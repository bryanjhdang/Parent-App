package cmpt276.as3.cmpt276hydrogenproject.model;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * stores a single task object, storing important features like
 * the task name and the child assigned to it
 */
public class Task {
    private String taskName;
    private Child currentChild;
    private ArrayList<TaskFinished> tasksFinished = new ArrayList<>();

    ChildManager childManager = ChildManager.getInstance();

    public Task(String taskName, Child currentChild) {
        this.taskName = taskName;
        this.currentChild = currentChild;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Child getCurrentChild() {
        return currentChild;
    }

    public void setCurrentChild(Child currentChild) {
        this.currentChild = currentChild;
    }

    public void taskCompleted() {
        childManager = ChildManager.getInstance();
        addTaskToCompletionHistory();
        currentChild = childManager.getNextChildInCoinFlipQueue(currentChild);
    }

    public void addTaskToCompletionHistory() {
        TaskFinished finishedTask;
        String childProfilePicture = currentChild.getStringProfilePicture();
        finishedTask = new TaskFinished(childProfilePicture,
                currentChild.getName());
        tasksFinished.add(finishedTask);
    }

    public String getTaskName() {
        return taskName;
    }

    public String getChildName() {
        return currentChild.getName();
    }

    public ArrayList<TaskFinished> getTasksFinished() {
        return tasksFinished;
    }

    public void setTasksFinished(ArrayList<TaskFinished> tasksFinished) {
        this.tasksFinished = tasksFinished;
    }

    public TaskFinished getFinishedTaskAt(int index) {
        return tasksFinished.get(index);
    }
    @NonNull
    @Override
    public String toString() {
        if (currentChild == null) {
            return taskName + "\nThere are no children to do this task!";
        } else {
            return taskName + "\nIt is " + currentChild.getName() + "'s turn!";
        }
    }
}
