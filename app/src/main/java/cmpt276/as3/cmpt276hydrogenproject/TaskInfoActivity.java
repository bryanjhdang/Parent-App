package cmpt276.as3.cmpt276hydrogenproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import java.util.Objects;

import cmpt276.as3.cmpt276hydrogenproject.model.Task;
import cmpt276.as3.cmpt276hydrogenproject.model.TaskManager;

public class TaskInfoActivity extends AppCompatActivity {

    private final String TITLE_MSG = "actionBarTitle";
    private final String TASK_INDEX_MSG = "taskIndex";
    private final String EDIT_CHILD = "Manage Task Settings";

    private String actionBarTitle;
    private Task task;

    private TaskManager taskManager = TaskManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_info_activity);

        initializeIntentInfo();
        setActionBar();
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

    public static Intent makeIntent(Context context) {
        return new Intent(context, TaskInfoActivity.class);
    }
}