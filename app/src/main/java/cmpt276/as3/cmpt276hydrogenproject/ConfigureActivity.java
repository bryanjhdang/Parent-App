package cmpt276.as3.cmpt276hydrogenproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cmpt276.as3.cmpt276hydrogenproject.model.Child;
import cmpt276.as3.cmpt276hydrogenproject.model.ChildManager;

public class ConfigureActivity extends AppCompatActivity {
    private final ChildManager childManager = ChildManager.getInstance();

    SharedPreferences sp;

    private final String TITLE_MSG = "actionBarTitle";
    private final String INDEX_MSG = "childIndex";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure_activity);
        setActionBar();

        sp = getSharedPreferences("Hydrogen", Context.MODE_PRIVATE);

        updateConfigText();
        updateListView();
        setAddChildButton();
        registerClickCallback();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateConfigText();
        updateListView();
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, ConfigureActivity.class);
    }

    private void updateConfigText() {
        int childListSize = childManager.getSizeOfChildList();
        TextView configText = findViewById(R.id.configText);

        if (childListSize == 0) {
            configText.setText(R.string.no_children);
        } else {
            configText.setText(R.string.has_children);
        }
    }

    private void setActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle("Configure My Children");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.darker_navy_blue)));
    }

    /**
     * Add button that, when clicked, will prompt the user to enter a name.
     * Valid name will be added to list of children.
     */
    private void setAddChildButton() {
        FloatingActionButton addChildButton = findViewById(R.id.addChildBtn);

        addChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchEditChildActivity = EditChildActivity.makeIntent(ConfigureActivity.this);
                launchEditChildActivity.putExtra(TITLE_MSG, "Add Child");
                startActivity(launchEditChildActivity);
            }
        });
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
        ArrayAdapter<Child> adapter = new ChildrenListAdapter();

        // Configure the list view.
        ListView list = findViewById(R.id.childListView);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        saveChildren();
    }

    private class ChildrenListAdapter extends ArrayAdapter<Child> {
        public ChildrenListAdapter() {
            super(ConfigureActivity.this, R.layout.child_item, childManager.getChildrenList());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.child_item, parent, false);
            }
            // Get name from child list and set TextView to that name
            Child child = childManager.getChildAt(position);
            String name = child.getName();
            TextView childName = view.findViewById(R.id.childNameTxt);
            childName.setText(name);
            ImageView profilePic = view.findViewById(R.id.childIconImg);
            //the below three lines of code can be commented and uncommented if app crashes upon launch.
            profilePic.setImageBitmap(childManager.decodeToBase64(child.getProfilePicture()));
            return view;
        }
    }

    void saveChildren() {
        SharedPreferences.Editor editor = sp.edit();
        Gson myGson = new GsonBuilder().create();
        String jsonString = myGson.toJson(childManager.getChildrenList());
        Log.i("SAVE", jsonString);
        editor.putString("childList", jsonString);
        editor.apply();
    }

    /**
     * Check if any name on the list was clicked and prompt the configure child pop-up.
     */
    private void registerClickCallback() {
        ListView list = findViewById(R.id.childListView);
        list.setOnItemClickListener((adapterView, view, index, id) -> {
            promptEditChild(index);
        });
    }

    /**
     * Gives the user the option to change the selected child name or delete the child.
     */
    private void promptEditChild(int childIndex) {
        Intent launchEditChildActivity = EditChildActivity.makeIntent(ConfigureActivity.this);
        launchEditChildActivity.putExtra(TITLE_MSG, "Edit Child");
        launchEditChildActivity.putExtra(INDEX_MSG, childIndex);
        startActivity(launchEditChildActivity);
    }
}
