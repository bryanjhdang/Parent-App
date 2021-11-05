package cmpt276.as3.cmpt276hydrogenproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class TimeoutActivity extends AppCompatActivity {

    final int CONVERT_MILLIS_TO_SECONDS = 60000;

    private Button startTimerBtn;
    private Button setTimeBtn;
    private Button resetTimerBtn;
    private TextView displayTimerField;
    private EditText editTextInput;
    private CountDownTimer backgroundTimerCountDown;
    private MediaPlayer soundEffectPlayer;

    private long startTimeInMilli;
    private long endOfTime;
    private boolean timerWorkingState;
    private long leftTimeInMilli;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeout_activity);
        setActionBar();

        displayTimerField = findViewById(R.id.textDisplayTimer);
        editTextInput = findViewById(R.id.minuteTextInput);
        setTimeBtn = findViewById(R.id.btnSetTimer);
        startTimerBtn = findViewById(R.id.btnStartTimer);
        resetTimerBtn = findViewById(R.id.btnResetTimer);

        setAllPresetTimers();

        startTimerBtn.setOnClickListener(v -> {
            if (timerWorkingState) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        setTimeBtn.setOnClickListener(v -> {
            String input = editTextInput.getText().toString();
            // Check for no value in the field
            if (input.length() == 0) {
                Toast.makeText(TimeoutActivity.this, "Field is empty !", Toast.LENGTH_SHORT).show();
                return;
            }
            // Parse string input into long
            long inputInMilli = Long.parseLong(input) * CONVERT_MILLIS_TO_SECONDS;
            if (inputInMilli == 0) {
                Toast.makeText(TimeoutActivity.this, "Invalid: Enter 1 minute or greater", Toast.LENGTH_SHORT).show();
                //TODO: remove this!! THIS IS FOR DEBUGGING AND PUT THE RETURN BACK
                inputInMilli = 1000;
                //return;
            }
            editTextInput.setText("");
            setTime(inputInMilli);
        });

        resetTimerBtn.setOnClickListener(v -> resetTimer());
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, TimeoutActivity.class);
    }

    private void setActionBar() {
        getSupportActionBar().setTitle("Timeout Timer");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.darker_navy_blue)));
    }

    private void setTime(long milliseconds) {
        startTimeInMilli = milliseconds;
        closeKeyboard();
        resetTimer();
        startTimerBtn.setText(R.string.btnTextStart);
    }

    private void startTimer() {
        endOfTime = System.currentTimeMillis() + leftTimeInMilli;

        backgroundTimerCountDown = new CountDownTimer(leftTimeInMilli, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                leftTimeInMilli = millisUntilFinished;
                updateDisplayTimer();
            }

            @Override
            public void onFinish() {
                timerWorkingState = false;

                countdownFinished();

                updateLayoutVisibility();
            }}.start();

        timerWorkingState = true;
        updateLayoutVisibility();
    }

    private void resetTimer() {
        leftTimeInMilli = startTimeInMilli;
        updateDisplayTimer();
        updateLayoutVisibility();
        startTimerBtn.setText(R.string.btnTextStart);
    }

    private void pauseTimer() {
        backgroundTimerCountDown.cancel();
        timerWorkingState = false;
        updateLayoutVisibility();
    }

    private void updateDisplayTimer() {
        int hours = (int) (leftTimeInMilli / 1000) / 3600;
        int minutes = (int) ((leftTimeInMilli / 1000) % 3600) / 60;
        int seconds = (int) (leftTimeInMilli / 1000) % 60;

        String timeLeftFormat;
        if (!(hours < 1)) {
            timeLeftFormat = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeLeftFormat = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }

        displayTimerField.setText(timeLeftFormat);
    }

    private void updateLayoutVisibility() {
        if (timerWorkingState) {
            editTextInput.setVisibility(View.INVISIBLE);
            setTimeBtn.setVisibility(View.INVISIBLE);
            resetTimerBtn.setVisibility(View.INVISIBLE);
            startTimerBtn.setText(R.string.btnTextPause);
        } else {
            editTextInput.setVisibility(View.VISIBLE);
            setTimeBtn.setVisibility(View.VISIBLE);
            startTimerBtn.setText(R.string.timerTextResume);

                if (leftTimeInMilli < startTimeInMilli) {
                    resetTimerBtn.setVisibility(View.VISIBLE);
                } else {
                    resetTimerBtn.setVisibility(View.INVISIBLE);
                }

                if (leftTimeInMilli < 1000) {
                    startTimerBtn.setVisibility(View.INVISIBLE);
                } else {
                    startTimerBtn.setVisibility(View.VISIBLE);
                }
        }
    }

    /**
     * Set pre-set timer buttons for the user to easily access in increments of:
     * 1, 2, 3, 5 and 10 minutes.
     */
    private void setAllPresetTimers() {
        Button oneMinBtn = findViewById(R.id.oneMinBtn);
        Button twoMinBtn = findViewById(R.id.twoMinBtn);
        Button threeMinBtn = findViewById(R.id.threeMinBtn);
        Button fiveMinBtn = findViewById(R.id.fiveMinBtn);
        Button tenMinBtn = findViewById(R.id.tenMinBtn);

        setPresetTimer(oneMinBtn, 1);
        setPresetTimer(twoMinBtn, 2);
        setPresetTimer(threeMinBtn, 3);
        setPresetTimer(fiveMinBtn, 5);
        setPresetTimer(tenMinBtn, 10);
    }

    /**
     * Assigns a time to be set when the button parameter is pressed.
     */
    private void setPresetTimer(Button presetTimeBtn, int inputInMilli) {
        inputInMilli *= CONVERT_MILLIS_TO_SECONDS;
        int finalInputInMilli = inputInMilli;
        presetTimeBtn.setOnClickListener(v -> {
            pauseTimer();
            setTime(finalInputInMilli);
        });
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("startTimeInMillis", startTimeInMilli);
        editor.putLong("millisLeft", leftTimeInMilli);
        editor.putBoolean("timerRunning", timerWorkingState);
        editor.putLong("endTime", endOfTime);
        editor.apply();
        if (backgroundTimerCountDown != null)
        {
            backgroundTimerCountDown.cancel();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        startTimeInMilli = prefs.getLong("startTimeInMillis", 600000);
        timerWorkingState = prefs.getBoolean("timerRunning", false);
        leftTimeInMilli = prefs.getLong("millisLeft", startTimeInMilli);
        updateLayoutVisibility();
        updateDisplayTimer();

        if (timerWorkingState) {
            endOfTime = prefs.getLong("endTime", 0);
            leftTimeInMilli = endOfTime - System.currentTimeMillis();

            if (leftTimeInMilli < 0) {
                timerWorkingState = false;
                leftTimeInMilli = 0;
                updateDisplayTimer();
                updateLayoutVisibility();
            } else {
                startTimer();
            }
        }
    }

    private void countdownSound() {
        if(soundEffectPlayer == null) {
            soundEffectPlayer = MediaPlayer.create(this, R.raw.mgs_alert_sound);
        }
        soundEffectPlayer.start();
    }

    private void countdownFinished() {
        countdownSound();
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
    }
}
