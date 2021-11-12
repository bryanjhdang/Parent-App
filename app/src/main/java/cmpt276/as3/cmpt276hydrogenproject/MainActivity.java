package cmpt276.as3.cmpt276hydrogenproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
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

public class MainActivity extends AppCompatActivity {
    ChildManager childManager = ChildManager.getInstance();
    CoinFlipManager coinFlipManager = CoinFlipManager.getInstance();
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        //Objects.requireNonNull(getSupportActionBar()).hide();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Main Menu");
        sp = getSharedPreferences("Hydrogen", MODE_PRIVATE);

        loadChildren();
        loadCoinFlips();
        toConfigureBtn();
        toCoinflipBtn();
        toTimeoutBtn();
    }

    void toConfigureBtn() {
        Button btn = findViewById(R.id.configureBtn);
        btn.setOnClickListener(v -> {
            Intent launchGame = ConfigureActivity.makeIntent(MainActivity.this);
            startActivity(launchGame);
        });
    }

    void toCoinflipBtn() {
        Button btn = findViewById(R.id.coinFlipBtn);
        btn.setOnClickListener(v -> {
            Intent launchGame = CoinFlipActivity.makeIntent(MainActivity.this);
            startActivity(launchGame);
        });
    }

    void toTimeoutBtn() {
        Button btn = findViewById(R.id.timeoutBtn);
        btn.setOnClickListener(v -> {
            Intent launchGame = TimeoutActivity.makeIntent(MainActivity.this);
            startActivity(launchGame);
        });
    }

    void loadChildren() {
        Gson myGson = new GsonBuilder().create();
        String jsonString = sp.getString("childList", "");
        if (!jsonString.equals("")) {
            Type listType = new TypeToken<ArrayList<Child>>(){}.getType();
            childManager.setAllChildren(myGson.fromJson(jsonString, listType));
        }
    }

    void loadCoinFlips() {
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
        String jsonString = sp.getString("coinFlipList", "");
        if (!jsonString.equals("")) {
            Type listType = new TypeToken<ArrayList<CoinFlip>>(){}.getType();
            coinFlipManager.setCoinFlipList(myGson.fromJson(jsonString, listType));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.helpMenu) {
            Intent helpMenuIntent = HelpActivity.makeIntent(this);
            startActivity(helpMenuIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}