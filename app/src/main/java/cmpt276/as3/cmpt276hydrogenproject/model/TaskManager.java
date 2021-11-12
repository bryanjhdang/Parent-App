package cmpt276.as3.cmpt276hydrogenproject.model;

import java.util.ArrayList;

public class TaskManager {
    private ArrayList<Task> TASK_LIST = new ArrayList<>();
    private static TaskManager instance;

    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    public ArrayList<Task> getTaskList() {
        return TASK_LIST;
    }

    public void setTaskListT(ArrayList<Task> taskList) {
        this.TASK_LIST = taskList;
    }

    public void addTask(Task newTask) {
        TASK_LIST.add(newTask);
    }

    public Task getTaskAt(int index) {
        return TASK_LIST.get(index);
    }
}
