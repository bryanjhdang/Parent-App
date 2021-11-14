package cmpt276.as3.cmpt276hydrogenproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import cmpt276.as3.cmpt276hydrogenproject.model.ChildManager;

/**
 * Showcases the information of an individual child, allowing the following changes:
 * - Name
 * - Image
 */

/**
 * TODO: Delete this section later
 * High level concepts
 * - Mark differences between editing / adding
 *      - Change title bar
 *      - Show delete button
 * - Need to pass over the child index to display the proper name to edit
 * - Need to pass over if it came from adding / editing
 */
public class EditChildActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_child);
        setActionBar();

        setChangeChildInformation();
        setDeleteChildButton();
        setSaveChildButton();
    }

    // call the intent when the user presses on a child in the listview
    public static Intent makeIntent(Context context, int childIndex) {
        return new Intent(context, EditChildActivity.class);
    }

    private void setActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle("Configure Child");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.darker_navy_blue)));
    }

    private void setChangeChildInformation() {
        setChangeName();
        setChangeImage();
    }

    private void setChangeName() {
        // allow the user to change the name
    }

    private void setChangeImage() {
        // at the moment display
        ImageView childPortrait = findViewById(R.id.childPortrait);
        childPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempStr = "Clicked on image";
                Toast.makeText(getApplicationContext(), tempStr, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDeleteChildButton() {
        // fab
        // give a warning
        // if click ok, then delete the child and return to configure child screen

        FloatingActionButton deleteButton = findViewById(R.id.deleteChildButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptDeletion();
            }
        });
    }

    private void promptDeletion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditChildActivity.this);
        builder.setTitle("Delete child? This action cannot be reversed!");

        builder.setPositiveButton("OK", ((dialogInterface, i) -> {
            String tempStr = "[NAME HERE] deleted!";
            Toast.makeText(getApplicationContext(), tempStr, Toast.LENGTH_SHORT).show();
            finish();
            deleteChild();
        }));

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void deleteChild() {
        // stub for now
    }

    private void setSaveChildButton() {
        FloatingActionButton saveButton = findViewById(R.id.saveChildButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText childNameInput = findViewById(R.id.childNameEditText);
                String childName = childNameInput.getText().toString();

                if (ChildManager.isValidName(childName)) {
                    setNewChildInfo(childName);
                    String msg = "Child added.";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
                            .show();
                    finish();
                } else {
                    String msg = "Name is invalid!";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void setNewChildInfo(String newChildName) {
        // need to store the name the user chose
        // need to add helper method to save it in the singleton
    }
}

//    AlertDialog.Builder builder = new AlertDialog.Builder(ConfigureActivity.this);
//        builder.setTitle("Edit child name:");
//
//                // Prompt the user for input
//
//                EditText input = new EditText(ConfigureActivity.this);
//                builder.setView(input);
//
//                // Edit child name on the list
//                builder.setPositiveButton("Confirm", (dialogInterface, i) -> {
//                String name = input.getText().toString();
//
//                if (isValidName(name)) {
//                childManager.editChildName(childIndex, name);
//                String msg = "Name changed.";
//                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
//                .show();
//                updateListView();
//                } else {
//                String msg = "Name is invalid!";
//                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
//                .show();
//                }
//                });
//
//                // Remove child from list when clicking on "Delete Child" prompt
//                builder.setNegativeButton("Delete Child", (dialogInterface, i) -> {
//                String msg = childManager.getChildAt(childIndex).getName() + " has been removed.";
//                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
//                .show();
//                childManager.removeChild(childIndex);
//                updateListView();
//                updateConfigText();
//                });
//
//                AlertDialog alert = builder.create();
//                alert.show();