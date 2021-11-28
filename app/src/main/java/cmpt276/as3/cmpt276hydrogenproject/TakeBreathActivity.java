package cmpt276.as3.cmpt276hydrogenproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_breath);
        setActionBar();
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

    private void setChooseBreathAmount() {
        // have a textview display that the using can press on
        // have a settings thing (...) next to the textview that the user and press on
        // being up several options between 1-10
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