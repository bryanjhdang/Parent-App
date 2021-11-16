package cmpt276.as3.cmpt276hydrogenproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.util.Objects;

import cmpt276.as3.cmpt276hydrogenproject.model.Child;
import cmpt276.as3.cmpt276hydrogenproject.model.ChildManager;
import cmpt276.as3.cmpt276hydrogenproject.model.Task;
import cmpt276.as3.cmpt276hydrogenproject.model.TaskManager;

public class TaskManagerActivity extends AppCompatActivity {
    private TaskManager taskManager = TaskManager.getInstance();
    private ChildManager childManager = ChildManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_manager_activity);
        setActionBar();


        showTaskList();
        addTaskButton();
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
        //save here
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
}