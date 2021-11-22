package cmpt276.as3.cmpt276hydrogenproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import cmpt276.as3.cmpt276hydrogenproject.model.Child;
import cmpt276.as3.cmpt276hydrogenproject.model.ChildManager;
import cmpt276.as3.cmpt276hydrogenproject.model.CoinFlipManager;
import cmpt276.as3.cmpt276hydrogenproject.model.TaskManager;

/**
 * Showcases the information of an individual child, allowing the following changes:
 * - Name
 * - Image
 */
public class EditChildActivity extends AppCompatActivity {
    private String actionBarTitle;
    private Child child;
    private ChildManager childManager = ChildManager.getInstance();
    private CoinFlipManager coinFlipManager = CoinFlipManager.getInstance();
    private TaskManager taskManager = TaskManager.getInstance();

    private final String TITLE_MSG = "actionBarTitle";
    private final String INDEX_MSG = "childIndex";
    private final String EDIT_CHILD = "Edit Child";

    private final int IMAGE_GALLERY_REQUEST = 20;
    private ImageView imageView;
    private Bitmap image;
    private boolean imageChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_child);
        initializeIntentInfo();

        imageView = findViewById(R.id.childPortraitPreview);

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
            Bitmap currentChildProfilePic = childManager.decodeToBase64(child.getStringProfilePicture());
            imageView.setImageBitmap(currentChildProfilePic);
        }
    }

    private void setChangeImageInput() {
        ImageView childPortrait = findViewById(R.id.childPortraitPreview);
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
                    editImageFromCamera();
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
        imageChanged = true;

        startActivityForResult(pickPhotoIntent, IMAGE_GALLERY_REQUEST);
    }

    private void editImageFromCamera() {
        Intent newPictureFromCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageChanged = true;
        startActivityForResult(newPictureFromCameraIntent, 69);
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
                    image = BitmapFactory.decodeStream(inputStream);
                    imageView.setImageBitmap(image);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this,
                            "Unable to open image",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                try {
                    assert data != null;
                    Bitmap cameraImage = (Bitmap) data.getExtras().get("data");
                    image = cameraImage;
                    imageView.setImageBitmap(cameraImage);
                } catch (Exception e) {
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
            taskManager.updateTaskChildren();
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

                        if(imageChanged == true){
                            imageChanged = false;
                            if(image == null) {
                                image = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
                            }
                            child.setStringProfilePicture(childManager.encodeToBase64(image));
                            String msgImage = "Profile Picture Updated";
                            Toast.makeText(getApplicationContext(), msgImage, Toast.LENGTH_SHORT)
                                    .show();
                            finish();
                        }

                    } else {
                        setNewChildInfo(newChildName);
                        imageChanged = false;
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
        //if no image is chosen by the user, the default image is set.
        if(image == null) {
            image = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
        }
        if (isEditingChild()) {
            coinFlipManager.updateCoinFlipChild(child.getName(), newChildName);
            childManager.editChildName(child, newChildName);
            coinFlipManager.updateChildNames(child, newChildName);
            child.setProfilePicture(childManager.encodeToBase64(image));
        } else {
            childManager.addChild(newChildName, childManager.encodeToBase64(image));
            imageChanged = false;
        }
        taskManager.updateTaskChildren();
    }

    private boolean isEditingChild() {
        if (actionBarTitle.equals(EDIT_CHILD)) {
            return true;
        }
        return false;
    }
}