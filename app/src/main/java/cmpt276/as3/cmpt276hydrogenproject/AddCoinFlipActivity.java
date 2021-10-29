package cmpt276.as3.cmpt276hydrogenproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import cmpt276.as3.cmpt276hydrogenproject.model.Child;
import cmpt276.as3.cmpt276hydrogenproject.model.ChildManager;

public class AddCoinFlipActivity extends AppCompatActivity {
    private ChildManager childManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_coinflip_activity);
        choosingChildSpinner();
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, AddCoinFlipActivity.class);
    }

    public void choosingChildSpinner() {
        childManager = new ChildManager();
        Spinner choosingChildSpinner = findViewById(R.id.choosingChildSpinner);
        ArrayList<String> childNames = childManager.getListOfChildrenNames();
        for(String s : childNames) {
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                childNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        choosingChildSpinner.setAdapter(adapter);

        //choosingChildSpinner.setSelected(childManager.getCoi);
        //choosingChildSpinner.setOnItemSelectedListener(this);
    }
}


