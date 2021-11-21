package cmpt276.as3.cmpt276hydrogenproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

import cmpt276.as3.cmpt276hydrogenproject.model.Child;
import cmpt276.as3.cmpt276hydrogenproject.model.ChildManager;
import cmpt276.as3.cmpt276hydrogenproject.model.CoinFlip;
import cmpt276.as3.cmpt276hydrogenproject.model.CoinFlipManager;

public class AddCoinFlipActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final int ANIMATION_DURATION = 1000;
    private final float ROTATION_VALUE = 1800;

    private final ChildManager childManager = ChildManager.getInstance();
    private final CoinFlipManager coinFlipManager = CoinFlipManager.getInstance();
    private Child flipCoinChild;
    private MediaPlayer soundEffectPlayer;
    private String rawChoiceInput;
    private boolean isHeads;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_coinflip_activity);
        setActionBar();
        setEmptyFlipBtn();
        emptyChildListCoinFlip();
        flipCoinButton();
        setNextChoiceSuggestion();
        choosingChildSpinner();
        createRadioButtons();

    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, AddCoinFlipActivity.class);
    }

    private void setEmptyFlipBtn() {
        FloatingActionButton emptyFlipBtn = findViewById(R.id.emptyFlipBtn);
        emptyFlipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddCoinFlipActivity.this);
                builder.setTitle("Do you want to perform a coin flip with no child?");
                builder.setPositiveButton("Yes", ((dialogInterface, i) -> {
                    CoinFlip childlessCoinFlip = new CoinFlip();
                    coinFlipManager.addCoinFlip(childlessCoinFlip);
                    playCoinFlipSound();
                    getResultOfCoinFlip(childlessCoinFlip);
                }));
                builder.setNegativeButton("No", null);

                builder.show();
            }
        });
    }

    private void setActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle("Coin Flip");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.darker_navy_blue)));
    }

    private void emptyChildListCoinFlip() {
        if(childManager.getSizeOfChildList() == 0) {
            CoinFlip childlessCoinFlip = new CoinFlip();
            coinFlipManager.addCoinFlip(childlessCoinFlip);
            playCoinFlipSound();
            getResultOfCoinFlip(childlessCoinFlip);
        } else {
            flipCoinChild = childManager.getNextChildInCoinFlipQueue(coinFlipManager.getPreviousPick());
        }
    }

    private void flipCoinButton() {
        Button btn = findViewById(R.id.flipCoinButton);
        btn.setOnClickListener(v -> {
            try {
                setChildChoice(rawChoiceInput);
                CoinFlip newCoinFlip = new CoinFlip(flipCoinChild, isHeads);
                coinFlipManager.addCoinFlip(newCoinFlip);
                String nameOfChoosingChild = flipCoinChild.getName();
                Toast.makeText(this,
                        nameOfChoosingChild + " chose " + rawChoiceInput,
                        Toast.LENGTH_SHORT).show();
                playCoinFlipSound();
                hideInterface();
                getResultOfCoinFlip(newCoinFlip);
            } catch (Exception e) {
                Toast.makeText(this,
                        "Please select Heads or Tails.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideInterface() {
        Button btn = findViewById(R.id.flipCoinButton);
        btn.setEnabled(false);
        RadioGroup coinSideChoices = findViewById(R.id.headsOrTails);
        coinSideChoices.setVisibility(View.INVISIBLE);
        Spinner choosingChildSpinner = findViewById(R.id.choosingChildSpinner);
        choosingChildSpinner.setEnabled(false);
    }

    /**
     * code inspiration (via using the ViewPropertyAnimator class) from
     * https://www.youtube.com/watch?v=umLDj-qxDKI (code in Kotlin)
     * implementation of the Runnable was inspired by methods used by ViewPropertyAnimator class
     * https://developer.android.com/reference/android/view/ViewPropertyAnimator#withEndAction(java.lang.Runnable)
     */
    private void getResultOfCoinFlip(CoinFlip coinFlip) {
        boolean resultIsHeads = coinFlip.getResult();
        if(resultIsHeads) {
            animateFlip(R.drawable.heads, coinFlip);
        } else {
            animateFlip(R.drawable.tails, coinFlip);
        }
    }

    private void animateFlip(int id, CoinFlip coinFlip) {
        ImageView coinFlipView = findViewById(R.id.coin);
        Runnable endAction = new Runnable() {
            @Override
            public void run() {
                coinFlipView.setImageResource(id);
                //dialog box pops up after animation is over to clearly indicate winner
                retrieveWinnerFromCoinFlip(coinFlip);
            }
        };
        coinFlipView.animate()
                .setDuration(ANIMATION_DURATION)
                .rotationXBy(ROTATION_VALUE)
                .withEndAction(endAction);
    }
    
    private void retrieveWinnerFromCoinFlip(CoinFlip coinFlip) {
        boolean resultIsHeads = coinFlip.getResult();

        AlertDialog.Builder builder = new AlertDialog.Builder(AddCoinFlipActivity.this);
        String stringifiedOutput = "The result of the flip is ";
        if(resultIsHeads) {
            stringifiedOutput += "heads!";
        } else {
            stringifiedOutput += "tails!";
        }
        builder.setTitle(stringifiedOutput);
        builder.setPositiveButton("OK", ((dialogInterface, i) -> {
            finish();
        }));
        dialog = builder.create();
        dialog.show();

        childManager.moveChildToBackOfQueue(flipCoinChild);
    }

    private void playCoinFlipSound() {
        if(soundEffectPlayer == null) {
            soundEffectPlayer = MediaPlayer.create(this, R.raw.flip_sound);
        }
        soundEffectPlayer.start();
    }

    private void setNextChoiceSuggestion() {
        TextView nextChildSuggestion = findViewById(R.id.nextChildSuggestion);
        if(childManager.getSizeOfChildList() == 0) {
            String noKids = "There are no children in the database!";
            nextChildSuggestion.setText(noKids);
        } else {
            Child previousChild = coinFlipManager.getPreviousPick();
            int indexOfNextChild = childManager.indexOfChildInCoinFlipQueue(previousChild) + 1;
            //if the next index is greater than [size-1] (which is the highest index for an arraylist
            //of size = n) then it returns to beginning of the list to suggest.
            if (indexOfNextChild > (childManager.getSizeOfChildList()-1)) {
                indexOfNextChild = 0;
            }
            Child nextChild = childManager.getChildFromCoinFlipQueueAt(indexOfNextChild);
            String suggestion = "The next suggested child to pick is: " + nextChild;
            nextChildSuggestion.setText(suggestion);
        }
    }

    public void choosingChildSpinner() {
        Spinner choosingChildSpinner = findViewById(R.id.choosingChildSpinner);

        ArrayList<Child> childrenList = childManager.getQueue();
        CoinFlipSpinnerAdapter adapter = new CoinFlipSpinnerAdapter(AddCoinFlipActivity.this, childrenList);

        choosingChildSpinner.setAdapter(adapter);
        choosingChildSpinner.setSelection(childManager.indexOfChildInCoinFlipQueue(flipCoinChild));
        choosingChildSpinner.setOnItemSelectedListener(this);
    }

    private class CoinFlipSpinnerAdapter extends ArrayAdapter<Child> {
        public CoinFlipSpinnerAdapter(Context context, ArrayList<Child> childArrayList) {
            super(context, 0, childArrayList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return createView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return createView(position, convertView, parent);
        }

        private View createView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.coinflip_spinner_item, parent, false);
            }

            Child child = childManager.getChildFromCoinFlipQueueAt(position);
            String name = child.getName();
            TextView childName = convertView.findViewById(R.id.childNameSpinner);
            childName.setText(name);
            ImageView childImage = convertView.findViewById(R.id.childImageSpinner);
            childImage.setImageBitmap(childManager.decodeToBase64(child.getProfilePicture()));

            return convertView;
        }
    }

    private void createRadioButtons() {
        RadioGroup coinSideChoices = findViewById(R.id.headsOrTails);
        String[] coinFlipOptions = getResources().getStringArray(R.array.choices);

        //populate the empty radio group in the activity
        for (final String choice : coinFlipOptions) {
            RadioButton button = new RadioButton(this);
            button.setText(choice);
            button.setTextColor(Color.WHITE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rawChoiceInput = choice;
                }
            });
            coinSideChoices.addView(button);
        }
    }

    private void setChildChoice(String optionChosen) {
        //extrapolates the raw data from the radio buttons and converts it to a more
        //manageable boolean value.
        isHeads = optionChosen.equals("Heads");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int choice = parent.getSelectedItemPosition();
        Child choosingChild = childManager.getChildFromCoinFlipQueueAt(choice);

        //if the user wants to have the same child as last time pick, display a helpful message
        //telling them that this child did pick the last time a coin was flipped.
        if(coinFlipManager.getPreviousPick() != null) {
            if (choosingChild.getName().equals(coinFlipManager.getPreviousPick().getName())
                && childManager.getSizeOfChildList() != 1) {
                Toast.makeText(getApplicationContext(),
                        "Warning: This child also chose last time!", Toast.LENGTH_SHORT).show();
            }
        }
        //set the child
        flipCoinChild = choosingChild;
    }

    @Override
    protected void onDestroy() {
        if (dialog != null) {
            dialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //do nothing
    }
}


