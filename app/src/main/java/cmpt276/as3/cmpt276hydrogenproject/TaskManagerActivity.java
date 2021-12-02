package cmpt276.as3.cmpt276hydrogenproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

import cmpt276.as3.cmpt276hydrogenproject.model.Child;
import cmpt276.as3.cmpt276hydrogenproject.model.ChildManager;
import cmpt276.as3.cmpt276hydrogenproject.model.Task;
import cmpt276.as3.cmpt276hydrogenproject.model.TaskManager;

/**
 * activity that display's the app's list of tasks; when task is clicked
 * a dialog box pops up with according information.
 */
public class TaskManagerActivity extends AppCompatActivity {
    SharedPreferences sp;
    private final TaskManager taskManager = TaskManager.getInstance();
    private final ChildManager childManager = ChildManager.getInstance();

    private final String TITLE_MSG = "actionBarTitle";
    private final String TASK_INDEX_MSG = "taskIndex";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_manager_activity);
        setActionBar();
        sp = getSharedPreferences("Hydrogen", Context.MODE_PRIVATE);

        updateChildObjects();
        updateTasksFinishedInfo();
        showTaskList();
        addTaskButton();
        registerClickCallback();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showTaskList();
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

    private void updateChildObjects() {
        for (Child child : childManager.getChildrenList()) {
            for (Task task : taskManager.getTaskList()) {
                if (task.getCurrentChild().getChildID() == child.getChildID()) {
                    task.setCurrentChild(child);
                }
            }
        }
    }

    private void updateTasksFinishedInfo() {
        for (Child child : childManager.getChildrenList()) {
            taskManager.updateTasksFinished(child.getChildID(),
                    child.getName(),
                    child.getStringProfilePicture());
        }
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
            Child currentChild = task.getCurrentChild();

            ImageView imageView = view.findViewById(R.id.taskIconImg);
            if (currentChild != null) {
                Bitmap bitmap = ChildManager.decodeToBase64(currentChild.getStringProfilePicture());
                imageView.setImageBitmap(bitmap);
            }

            TextView taskText = view.findViewById(R.id.taskViewText);
            taskText.setText(task.toString());

            return view;
        }
    }

    private void setActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle("Task Manager");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.darker_navy_blue)));
    }

    private void registerClickCallback() {
        ListView list = findViewById(R.id.taskListView);
        list.setOnItemClickListener((adapterView, view, index, id) -> {
            expandTaskInfo(index);
        });
    }

    private void expandTaskInfo(int index) {
        Intent launchTaskInfoActivity = TaskInfoActivity.makeIntent(TaskManagerActivity.this);
        launchTaskInfoActivity.putExtra(TITLE_MSG, "Manage Task Settings");
        launchTaskInfoActivity.putExtra(TASK_INDEX_MSG, index);
        startActivity(launchTaskInfoActivity);
    }

    private void saveTasks() {
        SharedPreferences.Editor editor = sp.edit();
        Gson myGson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                new TypeAdapter<LocalDateTime>() {
                    @Override
                    public void write(JsonWriter jsonWriter,
                                      LocalDateTime localDateTime) throws IOException {
                        jsonWriter.value(localDateTime.toString());
                    }

                    @Override
                    public LocalDateTime read(JsonReader jsonReader) throws IOException {
                        return LocalDateTime.parse(jsonReader.nextString());
                    }
                }).create();
        String jsonString = myGson.toJson(taskManager.getTaskList());
        editor.putString("taskList", jsonString);
        editor.apply();
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, TaskManagerActivity.class);
    }
}