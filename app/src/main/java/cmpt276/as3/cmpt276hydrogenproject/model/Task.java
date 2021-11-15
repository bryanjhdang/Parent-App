package cmpt276.as3.cmpt276hydrogenproject.model;

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

    public void taskCompleted() {
        currentChild = childManager.getNextChild(currentChild);
    }

    @Override
    public String toString() {
        return taskName + "\nIt is " + currentChild.getName() + "'s turn!";
    }
}
