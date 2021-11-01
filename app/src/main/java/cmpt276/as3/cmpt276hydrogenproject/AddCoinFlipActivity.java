package cmpt276.as3.cmpt276hydrogenproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import cmpt276.as3.cmpt276hydrogenproject.model.Child;
import cmpt276.as3.cmpt276hydrogenproject.model.ChildManager;
import cmpt276.as3.cmpt276hydrogenproject.model.CoinFlip;
import cmpt276.as3.cmpt276hydrogenproject.model.CoinFlipManager;

public class AddCoinFlipActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ChildManager childManager = ChildManager.getInstance();
    private CoinFlipManager coinFlipManager = CoinFlipManager.getInstance();
    private Child flipCoinChild = childManager.getChildSuggestion(coinFlipManager.getPreviousPick());
    private String rawChoiceInput;
    private boolean isHeads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_coinflip_activity);
        choosingChildSpinner();
        createRadioButtons();
        flipCoinButton();
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, AddCoinFlipActivity.class);
    }

    public void flipCoinButton() {
        Button btn = findViewById(R.id.flipCoinButton);
        //TODO: get the CHOICE (heads/tails) from the radio button and put it in the bool.
        btn.setOnClickListener(v -> {
            try {
                setChildChoice(rawChoiceInput);
                coinFlipManager.addCoinFlip(new CoinFlip(flipCoinChild, isHeads));
                String nameOfChoosingChild = flipCoinChild.getName();
                Toast.makeText(this,
                        nameOfChoosingChild + " chose " + rawChoiceInput,
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this,
                        "Please select Heads or Tails.",
                        Toast.LENGTH_SHORT).show();
            }
        });
        //TODO: do an animation
    }

    private void createRadioButtons() {
        RadioGroup coinSideChoices = findViewById(R.id.headsOrTails);
        String[] coinFlipOptions = getResources().getStringArray(R.array.choices);

        //populate the empty radio group in the activity
        for(final String choice : coinFlipOptions) {
            RadioButton button = new RadioButton(this);
            button.setText(choice);
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
        if(optionChosen.equals("Heads")) {
            isHeads = true;
        } else {
            isHeads = false;
        }
    }

    public void choosingChildSpinner() {
        Spinner choosingChildSpinner = findViewById(R.id.choosingChildSpinner);

        ArrayAdapter adapter = new ArrayAdapter<Child>(this,
                android.R.layout.simple_spinner_dropdown_item,
                childManager.getChildrenList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        choosingChildSpinner.setAdapter(adapter);

        choosingChildSpinner.setSelection(childManager.indexOfChild(flipCoinChild));
        choosingChildSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int choice = parent.getSelectedItemPosition();
        Child choosingChild = childManager.getChildAt(choice);
        if (coinFlipManager.getPreviousPick() == null) {
            flipCoinChild = choosingChild;
        } else if (choosingChild.getName().equals(coinFlipManager.getPreviousPick().getName())) {
            Toast.makeText(getApplicationContext(),
                    "Cannot pick the same child!", Toast.LENGTH_SHORT).show();
        } else {
            //set the child
            flipCoinChild = choosingChild;
        }
    }

    //TODO: make the radio buttons do something

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //do nothing
    }
}


