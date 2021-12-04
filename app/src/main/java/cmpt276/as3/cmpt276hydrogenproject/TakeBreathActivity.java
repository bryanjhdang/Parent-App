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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
//        void handleClickOn() {}
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_breath);
        setActionBar();

        sp = getSharedPreferences("Hydrogen", Context.MODE_PRIVATE);

        initializeBreathCount();
        setBreathCountArrows();

        testButtonHoldTimer();

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
        breathCountInt = Integer.parseInt(breathCountStr);

        final int MAX_BREATHS = 10;
        final int MIN_BREATHS = 1;

        if (option == INCREASE_BREATHS && breathCountInt < MAX_BREATHS) {
            breathCountInt++;
        } else if (option == DECREASE_BREATHS && breathCountInt > MIN_BREATHS) {
            breathCountInt--;
        }

        saveBreaths();

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

    private int getRemainingBreaths() {
        TextView breaths = findViewById(R.id.breathCount);
        return (Integer.parseInt(breaths.getText().toString()));
    }

    // ***********************************************************
    // State Pattern states
    // ***********************************************************

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
        Handler timerHandler = new Handler();
        Runnable timerRunnable = () -> setThreeSecondsPassed();
        Runnable testRunnable = () -> setTenSecondsPassed();

        boolean runnableInUse;
        boolean threeSecondsPassed;
        boolean tenSecondsPassed;

        final int threeSeconds = 3000;
        final int tenSeconds = 10000;

        @Override
        void handleEnter() {
            TextView tv = findViewById(R.id.breathHelpTxt);
            tv.setText("inhale state");

            threeSecondsPassed = false;
            tenSecondsPassed = false;


            timerHandler.postDelayed(timerRunnable, threeSeconds);
            timerHandler.postDelayed(testRunnable, tenSeconds);
            runnableInUse = true;
        }

        @Override
        void handleExit() {
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.removeCallbacks(testRunnable);
        }

        @Override
        void handleHold() {
            // increase time held
            // animation dynamically updating
            // should only be able to initially click again after restarting inhale state

            if (runnableInUse == false) {
                timerHandler.postDelayed(timerRunnable, threeSeconds);
                timerHandler.postDelayed(testRunnable, tenSeconds);
            }
        }

        @Override
        void handleRelease() {
            if (threeSecondsPassed) {
                setState(exhaleState);
            } else {
                // Reset the timer
                timerHandler.removeCallbacks(timerRunnable);
                timerHandler.removeCallbacks(testRunnable);
                runnableInUse = false;
            }

            if (tenSecondsPassed) {
                // set text to warn user
            }
        }

        private void setThreeSecondsPassed() {
            TextView tv = findViewById(R.id.breathHelpTxt);
            tv.setText("three seconds inhaled!");

            threeSecondsPassed = true;
        }

        private void setTenSecondsPassed() {
            TextView tv = findViewById(R.id.breathHelpTxt);
            tv.setText("ten seconds inhaled!");

            tenSecondsPassed = true;
        }
    }


// countdown timer test
//        TextView timerText = findViewById(R.id.secondsTesting);
//        new CountDownTimer(30000, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//                timerText.setText("seconds remaining: " + millisUntilFinished / 1000);
//            }
//
//            public void onFinish() {
//                timerText.setText("done!");
//            }
//        }.start();

    private class ExhaleState extends State {
        boolean threeSecondsPassed;
        boolean tenSecondsPassed;

        final int threeSeconds = 3000;
        final int tenSeconds = 10000;
        final int countDownInterval = 1000;

        @Override
        void handleEnter() {
            // play voice clip
            // start animation

            TextView timerText = findViewById(R.id.secondsTesting);
            new CountDownTimer(tenSeconds, countDownInterval) {

                public void onTick(long millisUntilFinished) {
                    timerText.setText("seconds: " + ((millisUntilFinished / countDownInterval) + 1));

                    if (millisUntilFinished <= tenSeconds - threeSeconds) {
                        threeSecondsPassed = true;
                        Toast.makeText(getApplicationContext(), "three seconds passed", Toast.LENGTH_SHORT).show();
                    }
                }

                public void onFinish() {
                    timerText.setText("ten seconds are up!");
                    tenSecondsPassed = true;
                }
            }.start();

            TextView tv = findViewById(R.id.breathHelpTxt);
            tv.setText("exhale state");

            threeSecondsPassed = false;
            tenSecondsPassed = false;

        }

        @Override
        void handleExit() {
            // get rid of animation
            // change music?

        }

//            // should only register after three seconds
//            // send to menu if 0 breaths left (play voice clip), send to inhale if > 0 states


        @Override
        void handleHold() {
            int breathsLeft = getRemainingBreaths();

            if (threeSecondsPassed && breathsLeft > 0) {
                setState(inhaleState);
            } else if (threeSecondsPassed && breathsLeft == 0) {
                setState(menuState);
            }
        }

        private boolean threeSecondsPassed() {
            return true;
        }

        private boolean tenSecondsPassed() {
            return true;
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