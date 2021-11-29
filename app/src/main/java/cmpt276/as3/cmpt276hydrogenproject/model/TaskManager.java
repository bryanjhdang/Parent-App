package cmpt276.as3.cmpt276hydrogenproject.model;

import java.util.ArrayList;
import java.util.List;

/**
 * class that stores the list of tasks that the parent wants to be completed.
 */
public class TaskManager {
    private ArrayList<Task> TASK_LIST = new ArrayList<>();
    private static TaskManager instance;
    private final ChildManager childManager = ChildManager.getInstance();

    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    public ArrayList<Task> getTaskList() {
        return TASK_LIST;
    }

    public ArrayList<TaskFinished> getFinishedTaskList(int index) {
        Task currentTask = getTaskAt(index);
        return currentTask.getTasksFinished();
    }

    public void setTaskList(ArrayList<Task> taskList) {
        this.TASK_LIST = taskList;
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

    public void updateTaskChildren() {
        if (childManager.isEmpty()) {
            for (Task task : TASK_LIST) {
                task.setCurrentChild(null);
            }
        } else {
            for (Task task : TASK_LIST) {
                boolean assignedChildDeleted = !childManager.containsChild(task.getCurrentChild());
                if (task.getCurrentChild() == null) {
                    task.setCurrentChild(childManager.getFirstChild());
                } else if (assignedChildDeleted) {
                    task.setCurrentChild(childManager.getFirstChild());
                }
            }
        }
    }

    public void deleteTaskAt(int index) {
        TASK_LIST.remove(index);
    }

    public Task getTaskAt(int index) {
        return TASK_LIST.get(index);
    }
}
