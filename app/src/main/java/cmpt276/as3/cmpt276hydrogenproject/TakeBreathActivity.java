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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class TakeBreathActivity extends AppCompatActivity {

    final int THREE_SECONDS = 3000;
    final int TEN_SECONDS = 10000;
    final int COUNTDOWN_INTERVAL = 1000;
    final int ANIMATION_INTERVAL = 100;
    final double ANIM_RATE_OF_CHANGE = 1.01;

    private TextView exhaleCircle;
    private TextView inhaleCircle;
    int INHALE_INITIAL_WIDTH = 267;
    int EXHALE_INITIAL_HEIGHT = 267;

    public final State menuState = new MenuState();
    public final State inhaleState = new InhaleState();
    public final State exhaleState = new ExhaleState();
    private State currentState = new IdleState();
    SharedPreferences sp;

    private final String actionBarTitle = "Take A Breath";

    private final int INCREASE_BREATHS = 1;
    private final int DECREASE_BREATHS = 2;
    private int breathCountInt = 1;
    private int breathsRemaining;

    // ***********************************************************
    // State Pattern's base states
    // ***********************************************************

    private abstract class State {
        void handleEnter() {}
        void handleExit() {}
        void handleHold() {}
        void handleRelease() {}
        void initializeCircle() {}
    }

    public void setState(State newState) {
        currentState.handleExit();
        currentState = newState;
        currentState.handleEnter();
    }

    // ***********************************************************
    // Android Code implementation
    // ***********************************************************

    // TODO: Remove this later because it's for debugging
    TextView timerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sp = getSharedPreferences("Hydrogen", Context.MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_breath);
        setActionBar();
        initializeBreathCount();
        setBreathCountArrows();
        setButtonControl();
        exhaleCircle = findViewById(R.id.exhaleCircleBG);
        inhaleCircle = findViewById(R.id.inhaleCircleBG);


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

    @SuppressLint("ClickableViewAccessibility")
    private void setButtonControl() {
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

    private class MenuState extends State {
        @Override
        void handleEnter() {
            canSeeBreathCount(true);
            TextView breathHelp = findViewById(R.id.breathHelpTxt);
            breathHelp.setText("Hold button and start inhaling!");

            initializeCircle();
        }

        @Override
        void handleExit() {
            canSeeBreathCount(false);
        }

        @Override
        void handleHold() {
            setState(inhaleState);
        }

        @Override
        void initializeCircle() {
            inhaleCircle.setVisibility(View.VISIBLE);
            exhaleCircle.setVisibility(View.INVISIBLE);

            Button breathButton = findViewById(R.id.breathButton);
            breathButton.setText("In");
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

        CountDownTimer animationTimer = new CountDownTimer(TEN_SECONDS, ANIMATION_INTERVAL) {
            @Override
            public void onTick(long l) {
                ViewGroup.LayoutParams params = inhaleCircle.getLayoutParams();
                params.width *= ANIM_RATE_OF_CHANGE;
                params.height *= ANIM_RATE_OF_CHANGE;
                inhaleCircle.setLayoutParams(params);
            }

            @Override
            public void onFinish() {

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

            initializeCircle();
            animationTimer.start();
        }

        @Override
        void handleExit() {
            countDownTimer.cancel();
            animationTimer.cancel();
        }

        @Override
        void handleHold() {
            if (!isRunning) {
                isRunning = true;
                countDownTimer.start();
                animationTimer.start();
            }
        }

        @Override
        void handleRelease() {
            if (threeSecondsPassed) {
                setState(exhaleState);
            } else {
                isRunning = false;
                initializeCircle();
                countDownTimer.cancel();
                animationTimer.cancel();

                // TODO: Remove, for debugging purposes
                String msg = "Let go too early!";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
                        .show();
            }
        }

        @Override
        void initializeCircle() {
            inhaleCircle.setVisibility(View.VISIBLE);
            exhaleCircle.setVisibility(View.INVISIBLE);

            int INHALE_INITIAL_WIDTH = 267;
            int EXHALE_INITIAL_HEIGHT = 267;

            ViewGroup.LayoutParams params = inhaleCircle.getLayoutParams();
            params.width = INHALE_INITIAL_WIDTH;
            params.height = EXHALE_INITIAL_HEIGHT;
            inhaleCircle.setLayoutParams(params);
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

        CountDownTimer animationTimer = new CountDownTimer(TEN_SECONDS, ANIMATION_INTERVAL) {
            @Override
            public void onTick(long l) {
                ViewGroup.LayoutParams params = exhaleCircle.getLayoutParams();
                params.width /= ANIM_RATE_OF_CHANGE;
                params.height /= ANIM_RATE_OF_CHANGE;
                exhaleCircle.setLayoutParams(params);
            }

            @Override
            public void onFinish() {
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

            initializeCircle();
            animationTimer.start();
        }

        @Override
        void handleExit() {
            countDownTimer.cancel();
            animationTimer.cancel();
        }

        @Override
        void handleHold() {
            if (threeSecondsPassed && breathsRemaining > 0) {
                setState(inhaleState);
            } else if (threeSecondsPassed && breathsRemaining <= 0) {
                setState(menuState);
            }
        }

        @Override
        void initializeCircle() {
            inhaleCircle.setVisibility(View.INVISIBLE);
            exhaleCircle.setVisibility(View.VISIBLE);

            int INITIAL_WIDTH = 800;
            int INITIAL_HEIGHT = 800;

            ViewGroup.LayoutParams params = exhaleCircle.getLayoutParams();
            params.width = INITIAL_WIDTH;
            params.height = INITIAL_HEIGHT;
            exhaleCircle.setLayoutParams(params);
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