package cmpt276.as3.cmpt276hydrogenproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cmpt276.as3.cmpt276hydrogenproject.model.Child;
import cmpt276.as3.cmpt276hydrogenproject.model.ChildManager;

public class ConfigureActivity extends AppCompatActivity {
    private ChildManager childManager = ChildManager.getInstance();
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure_activity);
        sp = getSharedPreferences("Hydrogen", Context.MODE_PRIVATE);

        loadChildren();
        updateListView();
        setAddChildButton();
        registerClickCallback();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateListView();
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

        saveChildren();
    }

    void saveChildren() {
        SharedPreferences.Editor editor = sp.edit();
        Gson myGson = new GsonBuilder().create();
        String jsonString = myGson.toJson(childManager.getChildrenList());
        editor.putString("childList", jsonString);
        editor.commit();
    }

    void loadChildren() {
        Gson myGson = new GsonBuilder().create();
        String jsonString = sp.getString("childList", "");
        if (!jsonString.equals("")) {
            Type listType = new TypeToken<ArrayList<Child>>(){}.getType();
            childManager.setAllChildren(myGson.fromJson(jsonString, listType));
        }
    }

    /**
     * Check if any name on the list was clicked and prompt the configure child pop-up.
     */
    private void registerClickCallback() {
        ListView list = findViewById(R.id.childListView);
        list.setOnItemClickListener((adapterView, view, index, id) -> {
            promptEditDeleteChild(index);
        });
    }

    /**
     * Gives the user the option to change the selected child name or delete the child.
     */
    private void promptEditDeleteChild(int childIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfigureActivity.this);
        builder.setTitle("Edit child name:");

        // Prompt the user for input
        EditText input = new EditText(ConfigureActivity.this);
        builder.setView(input);

        // Edit child name on the list
        builder.setPositiveButton("Confirm", (dialogInterface, i) -> {
            String name = input.getText().toString();

            if (isValidName(name)) {
                childManager.editChildName(childIndex, name);
                String msg = "Name changed.";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
                        .show();
                updateListView();
            } else {
                String msg = "Name is invalid!";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
                        .show();
            }
        });

        // Remove child from list when clicking on "Delete Child" prompt
        builder.setNegativeButton("Delete Child", (dialogInterface, i) -> {
            String msg = childManager.getChildAt(childIndex).getName() + " has been removed.";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
                    .show();
            childManager.removeChild(childIndex);
            updateListView();
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
