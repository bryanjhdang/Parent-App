package cmpt276.as3.cmpt276hydrogenproject.model;

import androidx.annotation.NonNull;

public class Task {
    private String taskName;
    private Child currentChild;
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
        currentChild = childManager.getNextChildInCoinFlipQueue(currentChild);
    }

    public String getTaskName() {
        return taskName;
    }

    public String getChildName() {
        return currentChild.getName();
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
