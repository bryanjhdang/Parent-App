package cmpt276.as3.cmpt276hydrogenproject.model;

import java.util.ArrayList;

public class TaskManager {
    private ArrayList<Task> TASK_LIST = new ArrayList<>();
    private static TaskManager instance;
    private ChildManager childManager = ChildManager.getInstance();

    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    public ArrayList<Task> getTaskList() {
        return TASK_LIST;
    }

    public void setTaskList(ArrayList<Task> taskList) {
        this.TASK_LIST = taskList;
    }

    public void addTask(Task newTask) {
        TASK_LIST.add(newTask);
    }

    public void addTask(String taskName) {
        Task newTask;
        if (childManager.isEmpty()) {
            newTask = new Task(taskName, null);
        } else {
            newTask = new Task(taskName, childManager.getFirstChild());
        }
        TASK_LIST.add(newTask);
    }

    public void refreshTaskChildren() {
        for (Task task : TASK_LIST) {
            if (task.getCurrentChild() == null) {
                task.setCurrentChild(childManager.getFirstChild());
            }
        }
    }

    //TODO: MAKE METHOD TO ASSIGN NEXT CHILD

    public Task getTaskAt(int index) {
        return TASK_LIST.get(index);
    }
}
