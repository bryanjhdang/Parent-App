package cmpt276.as3.cmpt276hydrogenproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import cmpt276.as3.cmpt276hydrogenproject.model.ChildManager;
import cmpt276.as3.cmpt276hydrogenproject.model.Task;
import cmpt276.as3.cmpt276hydrogenproject.model.TaskManager;

public class TaskInfoActivity extends AppCompatActivity {

    private String actionBarTitle;
    private Task task;

    private TaskManager taskManager = TaskManager.getInstance();

    private final String TITLE_MSG = "actionBarTitle";
    private final String TASK_INDEX_MSG = "taskIndex";
    private final String EDIT_CHILD = "Manage Task Settings";

    private ImageView assignedChildPicture;
    private EditText taskNameInput;
    private FloatingActionButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_info_activity);
        initializeIntentInfo();

        assignedChildPicture = findViewById(R.id.taskChildPicture);
        saveButton = findViewById(R.id.saveTaskChanges);
        taskNameInput = findViewById(R.id.taskHeading);

        setActionBar();
        setTaskInformation();

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

    private void initializeIntentInfo() {
        Intent intent = getIntent();
        actionBarTitle = intent.getStringExtra(TITLE_MSG);
        int taskIndex = intent.getIntExtra(TASK_INDEX_MSG, 0);
        task = taskManager.getTaskAt(taskIndex);
    }

    private void setActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(actionBarTitle);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.darker_navy_blue)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setTaskInformation() {
        //TODO: create checks for null children/tasks in each of the functions below
        setChangeTaskNameInput();
        setNameOfAssignedChild();
    }
    private void setChangeTaskNameInput() {
        //taskNameInput = findViewById(R.id.taskHeading);
        taskNameInput.setText(task.getTaskName());
        Bitmap assignedChildProfilePic = ChildManager.decodeToBase64(task.getCurrentChild().getStringProfilePicture());
        assignedChildPicture.setImageBitmap(assignedChildProfilePic);
    }

    private void setNameOfAssignedChild() {
        TextView nameOfAssignedChild = findViewById(R.id.taskAssignedName);
        String nameMessage = "This task is assigned to: ";
        nameMessage += task.getChildName();
        nameOfAssignedChild.setText(nameMessage);
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, TaskInfoActivity.class);
    }
}