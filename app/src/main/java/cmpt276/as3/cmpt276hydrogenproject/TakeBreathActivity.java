package cmpt276.as3.cmpt276hydrogenproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
    }

    public final State menuState = new MenuState();
    public final State inhaleState = new InhaleState();
    public final State exhaleState = new ExhaleState();
    private State currentState = new IdleState();
    SharedPreferences sp;

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

    private int breathCountInt = 1;
    private int breathsRemaining;


    // TODO: Remove this later because it's for debugging
    TextView timerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_breath);
        setActionBar();

        sp = getSharedPreferences("Hydrogen", Context.MODE_PRIVATE);

        initializeBreathCount();
        setBreathCountArrows();

        testButtonHoldTimer();

        // TODO: Remove this later because it's for debugging
        timerText = (TextView) findViewById(R.id.secondsTesting);

        setState(menuState);
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

    private void initializeBreathCount() {
        TextView breathText = findViewById(R.id.breathTxt);
        TextView breathCount = findViewById(R.id.breathCount);
        loadBreaths();

        breathText.setText("Let's take " + breathCountInt + " breath(s)!");
        breathCount.setText("" + breathCountInt);

        breathsRemaining = breathCountInt;
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
                updateBreathChoice();
            }
        });
    }

    private void setRightArrow() {
        ImageView rightArrow = findViewById(R.id.rightArrow);
        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateNewBreathAmount(INCREASE_BREATHS);
                updateBreathChoice();
            }
        });
    }

    private void calculateNewBreathAmount(int option) {
        TextView breathCount = findViewById(R.id.breathCount);
        String breathCountStr = breathCount.getText().toString();
        breathCountInt = Integer.parseInt(breathCountStr);

        final int MAX_BREATHS = 10;
        final int MIN_BREATHS = 1;

        if (option == INCREASE_BREATHS && breathCountInt < MAX_BREATHS) {
            breathCountInt++;
        } else if (option == DECREASE_BREATHS && breathCountInt > MIN_BREATHS) {
            breathCountInt--;
        }

        breathsRemaining = breathCountInt;

        saveBreaths();

        String newBreathCountStr = Integer.toString(breathCountInt);
        breathCount.setText(newBreathCountStr);
    }

    private void updateBreathChoice() {
        TextView breathText = findViewById(R.id.breathTxt);
        TextView breathCount = findViewById(R.id.breathCount);

        String breathCountStr = breathCount.getText().toString();
        String msg = "Let's take " + breathCountStr + " breath(s)!";
        breathText.setText(msg);
    }

    // TODO: Delete this function later; it's just for testing
    private void updateTimerText(long time) {
        TextView timerText = findViewById(R.id.secondsTesting);
        String timeAsStr = String.valueOf((int)time);
        timerText.setText(timeAsStr);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void testButtonHoldTimer() {
        Button button = findViewById(R.id.breathButton);
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        currentState.handleHold();
                        break;
                    case MotionEvent.ACTION_UP:
                        currentState.handleRelease();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void canSeeBreathCount(boolean canSee) {
        ConstraintLayout breathCountLayout = findViewById(R.id.breathCountLayout);
        TextView breathCountText = findViewById(R.id.breathTxt);

        if (canSee == true) {
            breathCountText.setVisibility(View.VISIBLE);
            breathCountLayout.setVisibility(View.VISIBLE);
        } else {
            breathCountText.setVisibility(View.INVISIBLE);
            breathCountLayout.setVisibility(View.INVISIBLE);
        }
    }

    // ***********************************************************
    // State Pattern states
    // ***********************************************************

    final int THREE_SECONDS = 3000;
    final int TEN_SECONDS = 10000;
    final int COUNTDOWN_INTERVAL = 1000;

    private class MenuState extends State {
        @Override
        void handleEnter() {
            canSeeBreathCount(true);

            TextView tv = findViewById(R.id.breathHelpTxt);
            tv.setText("menu state");
        }

        @Override
        void handleExit() {
            canSeeBreathCount(false);
        }

        @Override
        void handleHold() {
            setState(inhaleState);
        }
    }

    private class InhaleState extends State {
        boolean isRunning;
        boolean threeSecondsPassed;
        boolean tenSecondsPassed;

        CountDownTimer countDownTimer = new CountDownTimer(TEN_SECONDS, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO: Remove, for debugging purposes
                timerText.setText("seconds: " + ((millisUntilFinished / COUNTDOWN_INTERVAL) + 1));

                if (millisUntilFinished <= TEN_SECONDS - THREE_SECONDS) {
                    threeSecondsPassed = true;

                    Button breathButton = findViewById(R.id.breathButton);
                    breathButton.setText("Out");
                }
            }

            @Override
            public void onFinish() {
                // TODO: Remove, for debugging purposes
                timerText.setText("ten seconds are up!");

                tenSecondsPassed = true;
            }
        };

        @Override
        void handleEnter() {
            Button breathButton = findViewById(R.id.breathButton);
            breathButton.setText("In");

            TextView breathHelp = findViewById(R.id.breathHelpTxt);
            breathHelp.setText("Breath in");

            threeSecondsPassed = false;
            tenSecondsPassed = false;

            isRunning = true;
            countDownTimer.start();
        }

        @Override
        void handleExit() {
            countDownTimer.cancel();
        }

        @Override
        void handleHold() {
            // increase time held
            // animation dynamically updating
            // should only be able to initially click again after restarting inhale state

            if (!isRunning) {
                isRunning = true;
                countDownTimer.start();
            }
        }

        @Override
        void handleRelease() {
            if (threeSecondsPassed) {
                setState(exhaleState);
            } else {
                isRunning = false;
                countDownTimer.cancel();

                // TODO: Remove, for debugging purposes
                String msg = "Let go too early!";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private class ExhaleState extends State {
        boolean threeSecondsPassed;
        boolean tenSecondsPassed;

        CountDownTimer countDownTimer = new CountDownTimer(TEN_SECONDS, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO: Remove, for debugging purposes
                timerText.setText("seconds: " + ((millisUntilFinished / COUNTDOWN_INTERVAL) + 1));

                if (millisUntilFinished <= TEN_SECONDS - THREE_SECONDS) {
                    threeSecondsPassed = true;

                    Button breathButton = findViewById(R.id.breathButton);
                    breathsRemaining--;
                    if (breathsRemaining <= 0) {
                        breathButton.setText("Good job");
                    } else {
                        breathButton.setText("In");
                    }
                }
            }

            @Override
            public void onFinish() {
                // TODO: Remove, for debugging purposes
                timerText.setText("ten seconds are up!");

                tenSecondsPassed = true;
            }
        };

        @Override
        void handleEnter() {
            // TODO: play voice clip
            // TODO: start animation

            Button breathButton = findViewById(R.id.breathButton);
            breathButton.setText("Out");

            TextView breathHelp = findViewById(R.id.breathHelpTxt);
            breathHelp.setText("Breath out");

            threeSecondsPassed = false;
            tenSecondsPassed = false;

            countDownTimer.start();
        }

        @Override
        void handleExit() {
            countDownTimer.cancel();
        }

        @Override
        void handleHold() {
            if (threeSecondsPassed && breathsRemaining > 0) {
                setState(inhaleState);
            } else if (threeSecondsPassed && breathsRemaining <= 0) {
                setState(menuState);
            }
        }
    }

    private class IdleState extends State {
    }

    private void saveBreaths() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("breathCount", breathCountInt);
        editor.apply();
    }

    private void loadBreaths() {
        breathCountInt = sp.getInt("breathCount", 1);
    }
}