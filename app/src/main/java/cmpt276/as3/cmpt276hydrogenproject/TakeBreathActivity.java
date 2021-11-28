package cmpt276.as3.cmpt276hydrogenproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
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

        initializeBreathCount();
        setBreathCountArrows();

        testButtonHold();
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
        breathText.setText("Let's take " + breathCountStr + " breath(s)!");
    }

    private long timeElapsed = 0L;

    private void testButtonHold() {
        Button button = findViewById(R.id.breathButton);
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        timeElapsed = motionEvent.getDownTime();
                        break;
                    case MotionEvent.ACTION_UP:
                        timeElapsed = motionEvent.getEventTime() - timeElapsed;
                        timeElapsed /= 1000;
                        String msg = "Button held for " + timeElapsed + " seconds!";
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
                                .show();
                        timeElapsed = 0L;
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
            TextView tv = findViewById(R.id.breathHelpTxt);
            tv.setText("Currently in default");
        }

        @Override
        void handleExit() {
            Toast.makeText(getApplicationContext(), "left menu", Toast.LENGTH_SHORT).show();
        }

        @Override
        void handleClickOn() {
            super.handleClickOn();
        }
    }

    private class InhaleState extends State {
        Handler timerHandler = new Handler();
        Runnable timerRunnable = () -> setState(inhaleState);

        @Override
        void handleEnter() {
            super.handleEnter();
        }

        @Override
        void handleExit() {
            super.handleExit();
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
        Handler timerHandler = new Handler();
        Runnable timerRunnable = () -> setState(inhaleState);

        @Override
        void handleEnter() {
            super.handleEnter();
        }

        @Override
        void handleExit() {
            super.handleExit();
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

    private class IdleState extends State {}
}