package cmpt276.as3.cmpt276hydrogenproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        void handleHold() {}
        void handleRelease() {}
        void handleClickOn() {}
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

        initializeBreathCount();
        setBreathCountArrows();

        testButtonHoldTimer();
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

    private void hideActionBar() {
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    private void initializeBreathCount() {
        TextView breathText = findViewById(R.id.breathTxt);
        TextView breathCount = findViewById(R.id.breathCount);

        breathText.setText("Let's take 1 breath(s)!");
        breathCount.setText("1");
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

        final int MAX_BREATHS = 10;
        final int MIN_BREATHS = 1;

        if (option == INCREASE_BREATHS && breathCountInt < MAX_BREATHS) {
            breathCountInt++;
        } else if (option == DECREASE_BREATHS && breathCountInt > MIN_BREATHS) {
            breathCountInt--;
        }

        String newBreathCountStr = Integer.toString(breathCountInt);
        breathCount.setText(newBreathCountStr);
    }

    private void updateBreathAmountText() {
        TextView breathText = findViewById(R.id.breathTxt);
        TextView breathCount = findViewById(R.id.breathCount);

        String breathCountStr = breathCount.getText().toString();
        String msg = "Let's take " + breathCountStr + " breath(s)!";
        breathText.setText(msg);
    }

    private void testButtonClick() {
        Button button = findViewById(R.id.breathButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "clicked the button!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private long timeElapsed = 0L;

    @SuppressLint("ClickableViewAccessibility")
    private void testButtonHoldTimer() {
        Button button = findViewById(R.id.breathButton);
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    // when holding down the button
                    case MotionEvent.ACTION_DOWN:
//                        timeElapsed = motionEvent.getDownTime();

                        Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_SHORT).show();
//                        currentState.handleHold();
                        break;

                    // when letting go of the button
                    case MotionEvent.ACTION_UP:
//                        timeElapsed = motionEvent.getEventTime() - timeElapsed;
//                        timeElapsed /= 1000;
//                        String msg = "Button held for " + timeElapsed + " seconds!";
//                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
//                                .show();
//                        timeElapsed = 0L;

//                        currentState.handleClickOff();
                        Toast.makeText(getApplicationContext(), "let go", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    // ***********************************************************
    // State Pattern states
    // ***********************************************************

    private class MenuState extends State {
        @Override
        void handleEnter() {
            ConstraintLayout breathCountLayout = findViewById(R.id.breathCountLayout);
            breathCountLayout.setVisibility(View.VISIBLE);

            TextView tv = findViewById(R.id.breathHelpTxt);
            tv.setText("Currently in default");
        }

        @Override
        void handleExit() {
            ConstraintLayout breathCountLayout = findViewById(R.id.breathCountLayout);
            breathCountLayout.setVisibility(View.INVISIBLE);

            Toast.makeText(getApplicationContext(), "left menu", Toast.LENGTH_SHORT).show();
        }

        @Override
        void handleClickOn() {

            setState(inhaleState);
        }
    }

    private class InhaleState extends State {
        @Override
        void handleEnter() {
            // what happens when entering inhale state?
            // voice clip plays
            // music plays
            // animation starts
            super.handleEnter();
        }

        @Override
        void handleExit() {
            // what happens when leaving inhale state?
            // stop animation etc
            super.handleExit();
        }

        @Override
        void handleHold() {
            // increase time held
            // animation dynamically updating
            // should only be able to initially click again after restarting inhale state
            super.handleHold();
        }

        @Override
        void handleRelease() {
            // if less than 3 seconds, reset back to start
            // if after 3 seconds, move to exhale state
            super.handleRelease();
        }
    }

    private class ExhaleState extends State {
        @Override
        void handleEnter() {
            // play voice clip
            // start animation
            super.handleEnter();
        }

        @Override
        void handleExit() {
            // get rid of animation
            // change music?
            super.handleExit();
        }

        @Override
        void handleClickOn() {
            // should only register after three seconds
            // send to menu if 0 breaths left (play voice clip), send to inhale if > 0 states
            super.handleClickOn();
        }

        @Override
        void handleRelease() {
            // if less than 3 seconds, reset back to start
            // if after 3 seconds, move to exhale state
            super.handleRelease();
        }
    }

    private class IdleState extends State {
    }
}