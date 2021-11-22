package cmpt276.as3.cmpt276hydrogenproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Objects;

public class HelpActivity extends AppCompatActivity {
    private final String actionBarTitle = "Help";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);
        setActionBar();
        showAppCredentialsList();
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, HelpActivity.class);
    }

    private void setActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(actionBarTitle);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.darker_navy_blue)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void showAppCredentialsList() {
        ListView creditsListView = findViewById(R.id.creditsList);
        String[] creditsList = getResources().getStringArray(R.array.appDevInformation);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, R.layout.help_list, R.id.textView, creditsList
        );
        creditsListView.setAdapter(arrayAdapter);
    }
}