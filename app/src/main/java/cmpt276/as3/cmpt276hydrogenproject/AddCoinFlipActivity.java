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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
        setNextChoiceSuggestion();
        choosingChildSpinner();
        createRadioButtons();
        flipCoinButton();
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, AddCoinFlipActivity.class);
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
                retrieveWinnerFromCoinFlip(newCoinFlip);
            } catch (Exception e) {
                Toast.makeText(this,
                        "Please select Heads or Tails.",
                        Toast.LENGTH_SHORT).show();
            }
        });
        //TODO: do an animation
    }

    private void retrieveWinnerFromCoinFlip(CoinFlip coinFlip) {
        boolean resultIsHeads = coinFlip.getResult();
        AlertDialog dialog;
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
    }

    private void createRadioButtons() {
        RadioGroup coinSideChoices = findViewById(R.id.headsOrTails);
        String[] coinFlipOptions = getResources().getStringArray(R.array.choices);

        //populate the empty radio group in the activity
        for (final String choice : coinFlipOptions) {
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
        //extrapolates the raw data from the radio buttons and converts it to a more
        //manageable boolean value.
        if (optionChosen.equals("Heads")) {
            isHeads = true;
        } else {
            isHeads = false;
        }
    }

    private void setNextChoiceSuggestion() {
        TextView nextChildSuggestion = findViewById(R.id.nextChildSuggestion);
        if(childManager.getSizeOfChildList() == 0) {
            String noKids = "There are no children in the database!";
            nextChildSuggestion.setText(noKids);
        } else {
            int indexOfNextChild = childManager.indexOfChild(coinFlipManager.getPreviousPick()) + 1;
            //if the next index is greater than [size-1] (which is the highest index for an arraylist
            //of size = n) then it returns to beginning of the list to suggest.
            if (indexOfNextChild > (childManager.getSizeOfChildList()-1)) {
                indexOfNextChild = 0;
            }
            Child nextChild = childManager.getChildAt(indexOfNextChild);
            String suggestion = "The next suggested child to pick is " + nextChild;
            nextChildSuggestion.setText(suggestion);
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
        //if the user wants to have the same child as last time pick, display a helpful message
        //telling them that this child did pick the last time a coin was flipped.
        if (choosingChild.getName().equals(coinFlipManager.getPreviousPick().getName())) {
            Toast.makeText(getApplicationContext(),
                    "Warning: This child also chose last time!", Toast.LENGTH_SHORT).show();
        }
        //set the child
        flipCoinChild = choosingChild;
    }

    //TODO: make the radio buttons do something

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //do nothing
    }
}


