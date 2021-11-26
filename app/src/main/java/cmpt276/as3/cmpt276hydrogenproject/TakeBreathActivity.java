package cmpt276.as3.cmpt276hydrogenproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import java.util.Objects;

public class TakeBreathActivity extends AppCompatActivity {
    private final String actionBarTitle = "Take A Breath";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_breath);
        setActionBar();
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, TakeBreathActivity.class);
    }

    private void setActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(actionBarTitle);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.darker_navy_blue)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}