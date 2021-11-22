package cmpt276.as3.cmpt276hydrogenproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Objects;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Help");
        showAppCredentialsList();
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, HelpActivity.class);
    }

    public void showAppCredentialsList() {
        ListView creditsListView = findViewById(R.id.creditsList);
        String[] creditsList = getResources().getStringArray(R.array.appDevInformation);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this, R.layout.help_list, R.id.textView, creditsList
        );
        creditsListView.setAdapter(arrayAdapter);
    }
}