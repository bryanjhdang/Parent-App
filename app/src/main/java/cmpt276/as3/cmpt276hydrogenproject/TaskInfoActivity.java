package cmpt276.as3.cmpt276hydrogenproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import org.w3c.dom.Text;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

import cmpt276.as3.cmpt276hydrogenproject.model.Child;
import cmpt276.as3.cmpt276hydrogenproject.model.ChildManager;
import cmpt276.as3.cmpt276hydrogenproject.model.Task;
import cmpt276.as3.cmpt276hydrogenproject.model.TaskFinished;
import cmpt276.as3.cmpt276hydrogenproject.model.TaskManager;

public class TaskInfoActivity extends AppCompatActivity {

    private String actionBarTitle;
    private Task task;
    private int taskIndex;

    private TaskManager taskManager = TaskManager.getInstance();

    private final String TITLE_MSG = "actionBarTitle";
    private final String TASK_INDEX_MSG = "taskIndex";;

    private ImageView assignedChildPicture;
    private EditText taskNameInput;
    private FloatingActionButton saveButton;
    private FloatingActionButton completeTaskButton;
    private FloatingActionButton deleteTaskButton;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_info_activity);
        initializeIntentInfo();

        sp = getSharedPreferences("Hydrogen", Context.MODE_PRIVATE);
        assignedChildPicture = findViewById(R.id.taskChildPicture);
        saveButton = findViewById(R.id.saveTaskChanges);
        completeTaskButton = findViewById(R.id.completeTaskButton);
        deleteTaskButton = findViewById(R.id.deleteTaskButton);
        taskNameInput = findViewById(R.id.taskName);

        showTaskFinishedList();
        setActionBar();
        setTaskTextWatcher();
        setTaskInformation();
        setTaskButtons();
    }

    private void setTaskTextWatcher() {
        saveButton.setVisibility(View.INVISIBLE);
        TextWatcher taskInputChangeWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                saveButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = taskNameInput.getText().toString();
                if (input.equals(task.getTaskName())) {
                    saveButton.setVisibility(View.INVISIBLE);
                } else {
                    saveButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = taskNameInput.getText().toString();
                if (input.equals(task.getTaskName())) {
                    saveButton.setVisibility(View.INVISIBLE);
                } else {
                    saveButton.setVisibility(View.VISIBLE);
                }
            }
        };
        taskNameInput.addTextChangedListener(taskInputChangeWatcher);
    }

    private void setTaskButtons() {
        setSaveTaskInfoButton();
        setCompleteTaskButton();
        setDeleteTaskButton();
    }

    private void initializeIntentInfo() {
        Intent intent = getIntent();
        actionBarTitle = intent.getStringExtra(TITLE_MSG);
        taskIndex = intent.getIntExtra(TASK_INDEX_MSG, 0);
        task = taskManager.getTaskAt(taskIndex);
    }

    private void setActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(actionBarTitle);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.darker_navy_blue)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setTaskInformation() {
        setChangeTaskNameInput();
        setNameOfAssignedChild();
    }
    private void setChangeTaskNameInput() {
        taskNameInput.setText(task.getTaskName());
        if (task.getCurrentChild() != null) {
            Bitmap assignedChildProfilePic = ChildManager.decodeToBase64(task.getCurrentChild().getStringProfilePicture());
            assignedChildPicture.setImageBitmap(assignedChildProfilePic);
        }
    }

    private void setNameOfAssignedChild() {
        TextView nameOfAssignedChild = findViewById(R.id.taskAssignedName);
        if (task.getCurrentChild() != null) {
            String nameMessage = "This task is assigned to: ";
            nameMessage += task.getChildName();
            nameOfAssignedChild.setText(nameMessage);
        } else {
            String noChildMessage = "There are no kids to assign this task to!";
            nameOfAssignedChild.setText(noChildMessage);
        }
    }

    private void setSaveTaskInfoButton() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String taskName = taskNameInput.getText().toString();
                if (isValidTaskName(taskName)) {
                    task.setTaskName(taskName);
                    Toast.makeText(getApplicationContext(),
                            "Task Changes Saved!",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Invalid task name!",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void setCompleteTaskButton() {
        completeTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (task.getCurrentChild() != null) {
                    task.taskCompleted();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "There are no children to finish this task!", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void setDeleteTaskButton() {
        deleteTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDeleteDialog();
            }
        });
    }

    private void createDeleteDialog() {
        TextView tv = new TextView(TaskInfoActivity.this);
        String dialogLabel = "Are you sure you want to delete this task?";
        AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoActivity.this);
        builder.setTitle(dialogLabel);
        builder.setPositiveButton("Yes", ((dialogInterface, i) -> {
            taskManager.deleteTaskAt(taskIndex);
            finish();
        }));
        builder.setNegativeButton("No", null);

        AlertDialog deleteDialog = builder.create();
        deleteDialog.show();
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

    private void showTaskFinishedList() {
        ListView taskFinishedView = findViewById(R.id.taskCompletionHistory);
        ArrayAdapter<TaskFinished> arrayAdapter = new TaskFinishedAdapter();
        taskFinishedView.setAdapter(arrayAdapter);
        TextView emptyMessage = findViewById(R.id.taskHistoryHeader);
        taskFinishedView.setEmptyView(emptyMessage);
        arrayAdapter.notifyDataSetChanged();
    }

    private class TaskFinishedAdapter extends ArrayAdapter<TaskFinished> {
        public TaskFinishedAdapter() {
            super(TaskInfoActivity.this, R.layout.finished_task_item, task.getTasksFinished());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.finished_task_item, parent, false);
            }

            TaskFinished taskFinished = task.getFinishedTaskAt(position);
            TextView textView = view.findViewById(R.id.finishedTaskTxt);
            if (textView != null) {
                textView.setText(taskFinished.toString());
            }

//            ImageView childImg = view.findViewById(R.id.childPicImg);
//            Bitmap childBtmp = taskFinished.getChildProfilePicture();
//            childImg.setImageBitmap(childBtmp);

            return view;
        }
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, TaskInfoActivity.class);
    }
}