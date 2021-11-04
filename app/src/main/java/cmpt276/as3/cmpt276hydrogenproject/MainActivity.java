package cmpt276.as3.cmpt276hydrogenproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import cmpt276.as3.cmpt276hydrogenproject.model.Child;
import cmpt276.as3.cmpt276hydrogenproject.model.ChildManager;
import cmpt276.as3.cmpt276hydrogenproject.model.CoinFlipManager;

public class MainActivity extends AppCompatActivity {
    ChildManager childManager = ChildManager.getInstance();
    CoinFlipManager coinFlipManager = CoinFlipManager.getInstance();
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        sp = getSharedPreferences("Hydrogen", MODE_PRIVATE);

        loadChildren();
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
}