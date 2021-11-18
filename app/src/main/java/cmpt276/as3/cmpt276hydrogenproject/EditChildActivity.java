package cmpt276.as3.cmpt276hydrogenproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import cmpt276.as3.cmpt276hydrogenproject.model.Child;
import cmpt276.as3.cmpt276hydrogenproject.model.ChildManager;

/**
 * Showcases the information of an individual child, allowing the following changes:
 * - Name
 * - Image
 */
public class EditChildActivity extends AppCompatActivity {
    private String actionBarTitle;
    private Child child;
    private ChildManager childManager = ChildManager.getInstance();

    private final String TITLE_MSG = "actionBarTitle";
    private final String INDEX_MSG = "childIndex";
    private final String EDIT_CHILD = "Edit Child";

    private final int IMAGE_GALLERY_REQUEST = 20;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_child);
        initializeIntentInfo();

        // TODO: this is a test image, remove later
        imageView = findViewById(R.id.childPortrait);

        setActionBar();
        setChangeChildInformation();
        setDeleteChildButton();
        setSaveChildButton();
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, EditChildActivity.class);
    }

    private void initializeIntentInfo() {
        Intent intent = getIntent();
        actionBarTitle = intent.getStringExtra(TITLE_MSG);
        if (isEditingChild()) {
            int childIndex = intent.getIntExtra(INDEX_MSG, 0);
            child = childManager.getChildAt(childIndex);
        }
    }

    private void setActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(actionBarTitle);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.darker_navy_blue)));
    }

    private void setChangeChildInformation() {
        setChangeNameInput();
        setChangeImageInput();
    }

    private void setChangeNameInput() {
        if (isEditingChild()) {
            EditText nameInput = findViewById(R.id.childNameEditText);
            nameInput.setText(child.getName());
        }
    }

    private void setChangeImageInput() {
        ImageView childPortrait = findViewById(R.id.childPortrait);
        childPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setImageSelectionAlert();
            }
        });
    }

    private void setImageSelectionAlert() {
        String[] imageOptions = getResources().getStringArray(R.array.photoOptions);
        final int CAMERA = 0;
        final int GALLERY = 1;

        AlertDialog.Builder builder = new AlertDialog.Builder(EditChildActivity.this);
        builder.setTitle("Choose image from:");
        builder.setItems(imageOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == CAMERA) {
                    String tempMsg = "Clicked on Camera!";
                    Toast.makeText(getApplicationContext(), tempMsg, Toast.LENGTH_SHORT)
                            .show();
                } else if (i == GALLERY) {
                    editImageFromGallery();
                }
            }
        });

        builder.show();
    }

    private void editImageFromGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK);
        File imageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String imageDirectoryPath = imageDirectory.getPath();

        Uri data = Uri.parse(imageDirectoryPath);
        pickPhotoIntent.setDataAndType(data, "image/*");

        startActivityForResult(pickPhotoIntent, IMAGE_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            //successful processing
            if (requestCode == IMAGE_GALLERY_REQUEST) {
                Uri imageFromGallery = data.getData();

                InputStream inputStream;

                try {
                    inputStream = getContentResolver().openInputStream(imageFromGallery);

                    Bitmap image = BitmapFactory.decodeStream(inputStream);

                    imageView.setImageBitmap(image);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this,
                            "Unable to open image",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setDeleteChildButton() {
        FloatingActionButton deleteButton = findViewById(R.id.deleteChildButton);

        if (!isEditingChild()) {
            deleteButton.hide();
            return;
        }

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
            deleteChild();
            finish();
        }));

        builder.setNegativeButton("Cancel", null);

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void deleteChild() {
        childManager.removeChildByObject(child);
    }

    private void setSaveChildButton() {
        FloatingActionButton saveButton = findViewById(R.id.saveChildButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText childNameInput = findViewById(R.id.childNameEditText);
                String newChildName = childNameInput.getText().toString();

                if (ChildManager.isValidName(newChildName)) {
                    if (isEditingChild() && newChildName.equals(child.getName())) {
                        String msg = "No changes were made!";
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        setNewChildInfo(newChildName);
                        String msg = "Child added.";
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
                                .show();
                        finish();
                    }
                } else {
                    String msg = "Name is invalid!";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void setNewChildInfo(String newChildName) {
        if (isEditingChild()) {
            child.setName(newChildName);
        } else {
            childManager.addChild(newChildName);
        }
    }

    private boolean isEditingChild() {
        if (actionBarTitle.equals(EDIT_CHILD)) {
            return true;
        }
        return false;
    }
}