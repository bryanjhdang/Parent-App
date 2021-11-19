package cmpt276.as3.cmpt276hydrogenproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Objects;

import cmpt276.as3.cmpt276hydrogenproject.model.ChildManager;
import cmpt276.as3.cmpt276hydrogenproject.model.Task;
import cmpt276.as3.cmpt276hydrogenproject.model.TaskManager;

public class TaskManagerActivity extends AppCompatActivity {
    SharedPreferences sp;
    private TaskManager taskManager = TaskManager.getInstance();
    private ChildManager childManager = ChildManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_manager_activity);
        setActionBar();
        sp = getSharedPreferences("Hydrogen", Context.MODE_PRIVATE);

        showTaskList();
        taskManager.updateTaskChildren();
        addTaskButton();
        registerClickCallback();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showTaskList();
        taskManager.updateTaskChildren();
    }

    private void addTaskButton() {
        FloatingActionButton addTaskBtn = findViewById(R.id.addTaskBtn);
        addTaskBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(TaskManagerActivity.this);
            builder.setTitle("Please describe this task:");

            EditText input = new EditText(TaskManagerActivity.this);
            builder.setView(input);

            builder.setPositiveButton("OK", (dialogInterface, i) -> {
                String taskName = input.getText().toString();

                if (isValidTaskName(taskName)) {
                    taskManager.addTask(taskName);
                    Toast.makeText(getApplicationContext(), "Added task!", Toast.LENGTH_SHORT)
                            .show();
                    showTaskList();
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid task name!",
                            Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", null);

            AlertDialog alert = builder.create();
            alert.show();
        });
    }

    private boolean isValidTaskName(String name) {
        if (name.length() == 0) {
            return false;
        }

        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) != ' ') {
                return true;
            }
        }
        return false;
    }

    void showTaskList() {
        ListView taskListView = findViewById(R.id.taskListView);
        ArrayAdapter<Task> arrayAdapter = new TaskListAdapter();
        taskListView.setAdapter(arrayAdapter);

        TextView emptyMessage = findViewById(R.id.taskListEmptyText);
        taskListView.setEmptyView(emptyMessage);
        saveTasks();
        arrayAdapter.notifyDataSetChanged();

    }

    private class TaskListAdapter extends ArrayAdapter<Task> {
        public TaskListAdapter() {
            super(TaskManagerActivity.this, R.layout.task_item, taskManager.getTaskList());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.task_item, parent, false);
            }

            Task task = taskManager.getTaskAt(position);

            TextView taskText = view.findViewById(R.id.taskViewText);
            taskText.setText(task.toString());

            return view;
        }
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, TaskManagerActivity.class);
    }

    private void setActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle("Task Manager");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.darker_navy_blue)));
    }

    private void registerClickCallback() {
        ListView list = findViewById(R.id.taskListView);
        list.setOnItemClickListener((adapterView, view, index, id) -> {
                Toast.makeText(getApplicationContext(), "HI", Toast.LENGTH_SHORT).show();
                expandTaskInfo(index);
        });
    }

    private void expandTaskInfo(int index) {
        ImageView image = new ImageView(this);
        image.setImageResource(R.drawable.icon);
        Task currentTask = taskManager.getTaskAt(index);

        AlertDialog.Builder builder = new AlertDialog.Builder(TaskManagerActivity.this);
        builder.setTitle("Task: " + currentTask.getTaskName());
        if (currentTask.getCurrentChild() != null) {
            builder.setMessage("This task is assigned to: " + currentTask.getChildName());
        } else {
            builder.setMessage("There are no children to assign tasks to!");
        }
        builder.setView(image);

        builder.setPositiveButton("Finished!", ((dialogInterface, i) -> {
            if (currentTask.getCurrentChild() != null) {
                currentTask.taskCompleted();
            } else {
                Toast.makeText(getApplicationContext(),
                        "There are no children to finish this task!", Toast.LENGTH_SHORT)
                        .show();
            }
            showTaskList();
        }));

        builder.setNeutralButton("Edit", ((dialogInterface, i) -> {
            editTaskDialog(index);
        }));

        builder.setNegativeButton("Cancel", null);

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void editTaskDialog(int index) {
        EditText input = new EditText(TaskManagerActivity.this);
        Task currentTask = taskManager.getTaskAt(index);

        AlertDialog.Builder builder = new AlertDialog.Builder(TaskManagerActivity.this);
        builder.setTitle("Edit task:");
        builder.setView(input);
        builder.setPositiveButton("Save", ((dialogInterface, i) -> {
            String taskName = input.getText().toString();

            if (isValidTaskName(taskName)) {
                currentTask.setTaskName(taskName);
                Toast.makeText(getApplicationContext(), "Edited task!", Toast.LENGTH_SHORT)
                        .show();
                showTaskList();
            } else {
                Toast.makeText(getApplicationContext(), "Invalid task name!",
                        Toast.LENGTH_SHORT).show();
            }
        }));

        builder.setNeutralButton("Delete", ((dialogInterface, i) -> {
            taskManager.deleteTaskAt(index);
            showTaskList();
        }));

        builder.setNegativeButton("Cancel", null);

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void saveTasks() {
        SharedPreferences.Editor editor = sp.edit();
        Gson myGson = new GsonBuilder().create();
        String jsonString = myGson.toJson(taskManager.getTaskList());
        editor.putString("taskList", jsonString);
        editor.apply();
    }
}