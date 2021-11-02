package cmpt276.as3.cmpt276hydrogenproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import cmpt276.as3.cmpt276hydrogenproject.model.Child;
import cmpt276.as3.cmpt276hydrogenproject.model.ChildManager;

public class ConfigureActivity extends AppCompatActivity {
    private ChildManager childManager = ChildManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure_activity);

        updateListView();
        setAddChildButton();
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, ConfigureActivity.class);
    }

    /**
     * Add button that, when clicked, will prompt the user to enter a name.
     * Valid name will be added to list of children.
     */
    private void setAddChildButton() {
        FloatingActionButton addChildBtn = findViewById(R.id.addChildBtn);
        addChildBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ConfigureActivity.this);
            builder.setTitle("Enter the name of the child:");

            // Prompt the user for input
            EditText input = new EditText(ConfigureActivity.this);
            builder.setView(input);

            builder.setPositiveButton("OK", (dialogInterface, i) -> {
                String name = input.getText().toString();

                if (isValidName(name)) {
                    childManager.addChild(name);
                    String msg = "Added " + name + ".";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
                            .show();
                    updateListView();
                } else {
                    String msg = "Name is invalid!";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
                            .show();
                }
            });

            builder.setNegativeButton("Cancel", null);

            AlertDialog alert = builder.create();
            alert.show();
        });
    }

    /**
     * Determines if an inputted name is valid.
     * Names with no characters and names with all spaces are invalid.
     */
    private boolean isValidName(String name) {
        if (name.length() == 0) {
            return false;
        }

        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) != ' ') {
                return true;
            }
        }
        return false;
    }

    /**
     * Update the ListView item on the activity to display
     * the list of children
     */
    private void updateListView() {
        // Create list of items
        List<String> childStrList = new ArrayList<>();
        for (int i = 0; i < childManager.getSizeOfChildList(); i++) {
            String childName = childManager.getChildAt(i).getName();
            childStrList.add(childName);
        }

        // Build Adapter
        ArrayAdapter<Child> adapter = new ArrayAdapter(this, R.layout.child_item, childStrList);

        // Configure the list view.
        ListView list = findViewById(R.id.childListView);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
