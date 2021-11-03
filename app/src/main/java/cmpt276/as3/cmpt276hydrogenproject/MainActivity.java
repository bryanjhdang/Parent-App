package cmpt276.as3.cmpt276hydrogenproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

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
}