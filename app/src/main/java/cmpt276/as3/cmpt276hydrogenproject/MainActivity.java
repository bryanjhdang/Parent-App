package cmpt276.as3.cmpt276hydrogenproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

import cmpt276.as3.cmpt276hydrogenproject.model.Child;
import cmpt276.as3.cmpt276hydrogenproject.model.ChildManager;
import cmpt276.as3.cmpt276hydrogenproject.model.CoinFlip;
import cmpt276.as3.cmpt276hydrogenproject.model.CoinFlipManager;
import cmpt276.as3.cmpt276hydrogenproject.model.Task;
import cmpt276.as3.cmpt276hydrogenproject.model.TaskManager;

/**
 * main menu display that allows the user to navigate to their desired feature
 */
public class MainActivity extends AppCompatActivity {
    ChildManager childManager = ChildManager.getInstance();
    CoinFlipManager coinFlipManager = CoinFlipManager.getInstance();
    TaskManager taskManager = TaskManager.getInstance();
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Objects.requireNonNull(getSupportActionBar()).hide();
        sp = getSharedPreferences("Hydrogen", MODE_PRIVATE);

        loadData();
        setNavigationButtons();
    }

    private void loadData() {
        loadChildren();
        loadChildQueue();
        loadCoinFlips();
        loadTasks();
    }

    private void setNavigationButtons() {
        toConfigureBtn();
        toCoinFlipBtn();
        toTimeoutBtn();
        toTaskManagerBtn();
        toHelpBtn();
        toTakeBreathBtn();
    }

    void toConfigureBtn() {
        Button btn = findViewById(R.id.configureBtn);
        btn.setOnClickListener(v -> {
            Intent launchActivity = ConfigureActivity.makeIntent(MainActivity.this);
            startActivity(launchActivity);
        });
    }

    void toCoinFlipBtn() {
        Button btn = findViewById(R.id.coinFlipBtn);
        btn.setOnClickListener(v -> {
            Intent launchActivity = CoinFlipActivity.makeIntent(MainActivity.this);
            startActivity(launchActivity);
        });
    }

    void toTimeoutBtn() {
        Button btn = findViewById(R.id.timeoutBtn);
        btn.setOnClickListener(v -> {
            Intent launchGame = TimeoutActivity.makeIntent(MainActivity.this);
            startActivity(launchGame);
        });
    }

    void toHelpBtn() {
        Button helpBtn = findViewById(R.id.helpBtn);
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent helpIntent = HelpActivity.makeIntent(MainActivity.this);
                startActivity(helpIntent);
            }
        });
    }

    void toTaskManagerBtn() {
        Button btn = findViewById(R.id.taskManagerBtn);
        btn.setOnClickListener(v -> {
            Intent launchActivity = TaskManagerActivity.makeIntent(MainActivity.this);
            startActivity(launchActivity);
        });
    }

    void toTakeBreathBtn() {
        Button takeBreathBtn = findViewById(R.id.takeBreathBtn);
        takeBreathBtn.setOnClickListener(v -> {
            Intent launchTakeBreathActivity = TakeBreathActivity.makeIntent(MainActivity.this);
            startActivity(launchTakeBreathActivity);
        });
    }

    private final Gson myGson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
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

    void loadChildren() {
        String jsonString = sp.getString("childList", "");
        if (!jsonString.equals("")) {
            Type listType = new TypeToken<ArrayList<Child>>() {
            }.getType();
            childManager.setAllChildren(myGson.fromJson(jsonString, listType));
        }
    }

    void loadChildQueue() {
        String jsonString = sp.getString("childQueue", "");
        if (!jsonString.equals("")) {
            Type listType = new TypeToken<ArrayList<Child>>() {
            }.getType();
            childManager.setChildQueue(myGson.fromJson(jsonString, listType));
        }
    }

    void loadCoinFlips() {
        String jsonString = sp.getString("coinFlipList", "");
        if (!jsonString.equals("")) {
            Type listType = new TypeToken<ArrayList<CoinFlip>>() {
            }.getType();
            coinFlipManager.setCoinFlipList(myGson.fromJson(jsonString, listType));
        }
    }

    void loadTasks() {
        String jsonString = sp.getString("taskList", "");
        if (!jsonString.equals("")) {
            Type listType = new TypeToken<ArrayList<Task>>() {}.getType();
            taskManager.setTaskList(myGson.fromJson(jsonString, listType));
        }
    }
}