package cmpt276.as3.cmpt276hydrogenproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Objects;

/**
 * Activity that allows the user to do deep breathing, along with options indicating how many breaths to do.
 * Holding on the button will start periods of inhale / exhale states for as long as the user chose.
 */
public class TakeBreathActivity extends AppCompatActivity {

    final int THREE_SECONDS = 3000;
    final int TEN_SECONDS = 10000;
    final int COUNTDOWN_INTERVAL = 1000;
    final int ANIMATION_INTERVAL = 100;
    final double INHALE_RATE_OF_CHANGE = 1.0122;
    final double EXHALE_RATE_OF_CHANGE = 1.01;

    private TextView breathButton;
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

    private MediaPlayer inhaleMusic;
    private MediaPlayer exhaleMusic;

    // ***********************************************************
    // State Pattern's base states
    // ***********************************************************

    private abstract class State {
        void handleEnter() {}
        void handleExit() {}
        void handleHold() {}
        void handleRelease() {}
    }

    public void setState(State newState) {
        currentState.handleExit();
        currentState = newState;
        currentState.handleEnter();
    }

    // ***********************************************************
    // Android Code implementation
    // ***********************************************************

    @SuppressLint("ClickableViewAccessibility")
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
        breathButton = findViewById(R.id.breathButton);

        inhaleMusic = MediaPlayer.create(this, R.raw.inhale_wii_play);
        exhaleMusic = MediaPlayer.create(this, R.raw.exhale_wii_fit);

        setState(menuState);
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, TakeBreathActivity.class);
    }

    @Override
    protected void onResume() {
        setState(menuState);
        super.onResume();
    }

    @Override
    protected void onStop() {
        inhaleMusic.pause();
        exhaleMusic.pause();
        super.onStop();
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
        TextView button = findViewById(R.id.breathButton);
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

        if (canSee) {
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
            Objects.requireNonNull(getSupportActionBar()).setTitle(actionBarTitle);
            TextView breathHelp = findViewById(R.id.breathHelpTxt);
            breathHelp.setText("");

            canSeeBreathCount(true);
            breathsRemaining = breathCountInt;
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

        private void initializeCircle() {
            inhaleCircle.setVisibility(View.VISIBLE);
            exhaleCircle.setVisibility(View.INVISIBLE);

            TextView breathButton = findViewById(R.id.breathButton);
            breathButton.setText("Begin");

            ViewGroup.LayoutParams params = inhaleCircle.getLayoutParams();
            params.width = INHALE_INITIAL_WIDTH;
            params.height = EXHALE_INITIAL_HEIGHT;
            inhaleCircle.setLayoutParams(params);

            ViewGroup.LayoutParams breathBtnParams = breathButton.getLayoutParams();
            breathBtnParams.width = INHALE_INITIAL_WIDTH;
            breathBtnParams.height = EXHALE_INITIAL_HEIGHT;
            breathButton.setLayoutParams(breathBtnParams);
        }
    }

    private class InhaleState extends State {
        boolean isRunning;
        boolean threeSecondsPassed;

        CountDownTimer countDownTimer = new CountDownTimer(TEN_SECONDS, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished <= TEN_SECONDS - THREE_SECONDS) {
                    threeSecondsPassed = true;
                    TextView breathButton = findViewById(R.id.breathButton);
                    breathButton.setText("Out");
                }
            }

            @Override
            public void onFinish() {
                inhaleMusic.pause();
            }
        };

        CountDownTimer animationTimer = new CountDownTimer(TEN_SECONDS, ANIMATION_INTERVAL) {
            @Override
            public void onTick(long l) {
                ViewGroup.LayoutParams params = inhaleCircle.getLayoutParams();
                params.width *= INHALE_RATE_OF_CHANGE;
                params.height *= INHALE_RATE_OF_CHANGE;
                inhaleCircle.setLayoutParams(params);

                ViewGroup.LayoutParams breathBtnParams = breathButton.getLayoutParams();
                breathBtnParams.width *= INHALE_RATE_OF_CHANGE;
                breathBtnParams.height *= INHALE_RATE_OF_CHANGE;
                breathButton.setLayoutParams(breathBtnParams);
            }

            @Override
            public void onFinish() {
            }
        };

        @Override
        void handleEnter() {
            Objects.requireNonNull(getSupportActionBar()).setTitle(actionBarTitle + " (" + breathsRemaining + ")");
            TextView breathButton = findViewById(R.id.breathButton);
            breathButton.setText("In");
            TextView breathHelp = findViewById(R.id.breathHelpTxt);
            breathHelp.setText("Breath in and hold button");

            threeSecondsPassed = false;
            isRunning = true;
            countDownTimer.start();

            initializeCircle();
            animationTimer.start();
            inhaleMusic.start();
        }

        @Override
        void handleExit() {
            countDownTimer.cancel();
            animationTimer.cancel();
            resetMusic();
        }

        @Override
        void handleHold() {
            if (!isRunning) {
                isRunning = true;
                countDownTimer.start();
                animationTimer.start();
                inhaleMusic.start();
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
                resetMusic();
            }
        }

        private void initializeCircle() {
            inhaleCircle.setVisibility(View.VISIBLE);
            exhaleCircle.setVisibility(View.INVISIBLE);

            ViewGroup.LayoutParams params = inhaleCircle.getLayoutParams();
            params.width = INHALE_INITIAL_WIDTH;
            params.height = EXHALE_INITIAL_HEIGHT;
            inhaleCircle.setLayoutParams(params);

            ViewGroup.LayoutParams breathBtnParams = breathButton.getLayoutParams();
            breathBtnParams.width = INHALE_INITIAL_WIDTH;
            breathBtnParams.height = EXHALE_INITIAL_HEIGHT;
            breathButton.setLayoutParams(breathBtnParams);
        }

        private void resetMusic() {
            inhaleMusic.pause();
            inhaleMusic.seekTo(0);
        }
    }

    private class ExhaleState extends State {
        boolean threeSecondsPassed;
        boolean breathHasBeenDecreased;

        CountDownTimer countDownTimer = new CountDownTimer(TEN_SECONDS, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished <= TEN_SECONDS - THREE_SECONDS) {
                    threeSecondsPassed = true;

                    if (!breathHasBeenDecreased) {
                        breathsRemaining--;
                        breathHasBeenDecreased = true;
                        Objects.requireNonNull(getSupportActionBar()).setTitle(actionBarTitle + " (" + breathsRemaining + ")");
                    }

                    TextView breathButton = findViewById(R.id.breathButton);
                    if (breathsRemaining <= 0) {
                        breathButton.setText("Good\nJob");
                    } else {
                        breathButton.setText("In");
                    }
                }
            }

            @Override
            public void onFinish() {
                exhaleMusic.pause();
            }
        };

        CountDownTimer animationTimer = new CountDownTimer(TEN_SECONDS, ANIMATION_INTERVAL) {
            @Override
            public void onTick(long l) {
                ViewGroup.LayoutParams params = exhaleCircle.getLayoutParams();
                params.width /= EXHALE_RATE_OF_CHANGE;
                params.height /= EXHALE_RATE_OF_CHANGE;
                exhaleCircle.setLayoutParams(params);

                ViewGroup.LayoutParams breathBtnParams = breathButton.getLayoutParams();
                breathBtnParams.width /= EXHALE_RATE_OF_CHANGE;
                breathBtnParams.height /= EXHALE_RATE_OF_CHANGE;
                breathButton.setLayoutParams(breathBtnParams);
            }

            @Override
            public void onFinish() {
            }
        };

        @Override
        void handleEnter() {
            TextView breathButton = findViewById(R.id.breathButton);
            breathButton.setText("Out");
            TextView breathHelp = findViewById(R.id.breathHelpTxt);
            breathHelp.setText("Breath out");

            breathHasBeenDecreased = false;
            threeSecondsPassed = false;
            countDownTimer.start();

            initializeCircle();
            animationTimer.start();
            exhaleMusic.start();
        }

        @Override
        void handleExit() {
            countDownTimer.cancel();
            animationTimer.cancel();
            resetMusic();
        }

        @Override
        void handleHold() {
            if (threeSecondsPassed && breathsRemaining > 0) {
                setState(inhaleState);
            } else if (threeSecondsPassed && breathsRemaining <= 0) {
                setState(menuState);
            }
        }

        private void initializeCircle() {
            inhaleCircle.setVisibility(View.INVISIBLE);
            exhaleCircle.setVisibility(View.VISIBLE);

            int MAX_WIDTH = 800;
            int MAX_HEIGHT = 800;

            ViewGroup.LayoutParams params = exhaleCircle.getLayoutParams();
            params.width = MAX_WIDTH;
            params.height = MAX_HEIGHT;
            exhaleCircle.setLayoutParams(params);

            ViewGroup.LayoutParams breathBtnParams = breathButton.getLayoutParams();
            breathBtnParams.width = MAX_WIDTH;
            breathBtnParams.height = MAX_HEIGHT;
            breathButton.setLayoutParams(breathBtnParams);
        }

        private void resetMusic() {
            exhaleMusic.pause();
            exhaleMusic.seekTo(0);
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