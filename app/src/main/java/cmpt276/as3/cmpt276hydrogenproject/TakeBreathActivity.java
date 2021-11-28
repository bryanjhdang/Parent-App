package cmpt276.as3.cmpt276hydrogenproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class TakeBreathActivity extends AppCompatActivity {

    // ***********************************************************
    // State Pattern's base states
    // ***********************************************************

    private abstract class State {
//        private TakeBreathActivity context;
//        public State(TakeBreathActivity context) {
//            this.context = context;
//        }

        void handleEnter() {}
        void handleExit() {}
        void handleClickOn() {}
        void handleHold() {}
        void handleClickOff() {}
    }

    public final State menuState = new MenuState();
    public final State inhaleState = new InhaleState();
    public final State exhaleState = new ExhaleState();
    private State currentState = new IdleState();

    public void setState(State newState) {
        currentState.handleExit();
        currentState = newState;
        currentState.handleEnter();
    }

    // ***********************************************************
    // Android Code implementation
    // ***********************************************************

    private final String actionBarTitle = "Take A Breath";

    private final int INCREASE_BREATHS = 1;
    private final int DECREASE_BREATHS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_breath);
        setActionBar();

        setBreathCountArrows();
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, TakeBreathActivity.class);
    }

    private void setActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(actionBarTitle);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.darker_navy_blue)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setBreathCountArrows() {
        setLeftArrow();
        setRightArrow();
    }

    private void setLeftArrow() {
        ImageView leftArrow = findViewById(R.id.leftArrow);
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateNewBreathAmount(DECREASE_BREATHS);
                updateBreathAmountText();
            }
        });
    }

    private void setRightArrow() {
        ImageView rightArrow = findViewById(R.id.rightArrow);
        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateNewBreathAmount(INCREASE_BREATHS);
                updateBreathAmountText();
            }
        });
    }

    private void calculateNewBreathAmount(int option) {
        TextView breathCount = findViewById(R.id.breathCount);
        String breathCountStr = breathCount.getText().toString();
        int breathCountInt = Integer.parseInt(breathCountStr);

        if (option == INCREASE_BREATHS && breathCountInt < 10) {
            breathCountInt++;
        } else if (option == DECREASE_BREATHS && breathCountInt > 1) {
            breathCountInt--;
        }

        String newBreathCountStr = Integer.toString(breathCountInt);
        breathCount.setText(newBreathCountStr);
    }

    private void updateBreathAmountText() {
        TextView breathText = findViewById(R.id.breathTxt);
        TextView breathCount = findViewById(R.id.breathCount);

        String breathCountStr = breathCount.getText().toString();
        breathText.setText("Let's take " + breathCountStr + " breaths!");
    }

    // ***********************************************************
    // State Pattern states
    // ***********************************************************

    private class MenuState extends State {

    }

    private class InhaleState extends State {
        @Override
        void handleEnter() {
            super.handleEnter();
        }

        @Override
        void handleExit() {
            super.handleExit();
        }

        @Override
        void handleClickOn() {
            super.handleClickOn();
        }

        @Override
        void handleHold() {
            super.handleHold();
        }

        @Override
        void handleClickOff() {
            super.handleClickOff();
        }
    }

    private class ExhaleState extends State {

    }

    private class IdleState extends State {

    }
}