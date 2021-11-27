package cmpt276.as3.cmpt276hydrogenproject;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * activity that allows user to set timer or choose from a selection
 * of preset times with buttons on the screen.
 */
public class TimeoutActivity extends AppCompatActivity {

    private final int CONVERT_MILLIS_TO_SECONDS = 60000;
    private final int COUNTDOWN_INTERVAL = 1000;
    private final int SECONDS_PER_HOUR = 3600;
    private final int SECONDS_PER_MINUTE = 60;

    private final int DEFAULT_SETTING_1 = 1;
    private final int DEFAULT_SETTING_2 = 2;
    private final int DEFAULT_SETTING_3 = 3;
    private final int DEFAULT_SETTING_4 = 5;
    private final int DEFAULT_SETTING_5 = 10;

    private final int INITIAL_DEFAULT = 600000;

    private Button startTimerBtn;
    private Button setTimeBtn;
    private Button resetTimerBtn;
    private TextView displayTimerField;
    private EditText editTextInput;
    private CountDownTimer backgroundTimerCountDown;
    private MaterialProgressBar materialProgressBar;

    private long startTimeInMilli;
    private long endOfTime;
    private boolean timerWorkingState;
    private boolean isFirstTime;
    private long leftTimeInMilli;

    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeout_activity);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        setActionBar();
        materialProgressBar = findViewById(R.id.timerCountdownBar);

        displayTimerField = findViewById(R.id.textDisplayTimer);
        editTextInput = findViewById(R.id.minuteTextInput);
        setTimeBtn = findViewById(R.id.btnSetTimer);
        startTimerBtn = findViewById(R.id.btnStartTimer);
        resetTimerBtn = findViewById(R.id.btnResetTimer);

        startTimerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(TimeoutActivity.this, NotificationBroadcast.class);
            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(TimeoutActivity.this, 0, intent, 0);
            if (timerWorkingState) {
                pauseTimer();
                alarmManager.cancel(pendingIntent);
            } else {
                startTimer();
                //code was followed from demo from https://www.youtube.com/watch?v=nl-dheVpt8o
                long timeWhenButtonClicked = System.currentTimeMillis();
                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        timeWhenButtonClicked + leftTimeInMilli,
                        pendingIntent);
            }
        });

        setTimeBtn.setOnClickListener(v -> {
            String input = editTextInput.getText().toString();
            // Check for no value in the field
            if (input.length() == 0) {
                Toast.makeText(TimeoutActivity.this, "Field is empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Parse string input into long
            long inputInMilli = Long.parseLong(input) * CONVERT_MILLIS_TO_SECONDS;
            if (inputInMilli == 0) {
                Toast.makeText(TimeoutActivity.this, "Invalid: Enter 1 minute or greater", Toast.LENGTH_SHORT).show();
                return;
            }
            editTextInput.setText("");
            setTime(inputInMilli);
        });

        resetTimerBtn.setOnClickListener(v -> resetTimer());

        setAllPresetTimers();
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, TimeoutActivity.class);
    }

    private void setActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle("Timeout Timer");
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
        materialProgressBar.setVisibility(MaterialProgressBar.VISIBLE);

        backgroundTimerCountDown = new CountDownTimer(leftTimeInMilli, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                leftTimeInMilli = millisUntilFinished;
                tickVisualTimer();
                updateDisplayTimer();
            }

            private void tickVisualTimer() {
                double timeRemainingPercent = (double)leftTimeInMilli/(double)startTimeInMilli;
                timeRemainingPercent *= 100;
                if (leftTimeInMilli == 0) {
                    materialProgressBar.setVisibility(MaterialProgressBar.INVISIBLE);
                } else {
                    materialProgressBar.setProgress((int) timeRemainingPercent, true);
                }
            }

            @Override
            public void onFinish() {
                timerWorkingState = false;
                updateLayoutVisibility();
            }
        }.start();

        timerWorkingState = true;
        updateLayoutVisibility();
    }

    private void resetTimer() {
        Intent intent = new Intent(TimeoutActivity.this, NotificationBroadcast.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(TimeoutActivity.this, 0, intent, 0);
        if (backgroundTimerCountDown != null) {
            pauseTimer();
        }
        materialProgressBar.setVisibility(MaterialProgressBar.INVISIBLE);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
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
        int hours = (int) (leftTimeInMilli / COUNTDOWN_INTERVAL) / SECONDS_PER_HOUR;
        int minutes = (int) ((leftTimeInMilli / COUNTDOWN_INTERVAL) % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE;
        int seconds = (int) (leftTimeInMilli / COUNTDOWN_INTERVAL) % SECONDS_PER_MINUTE;

        String timeLeftFormat;
        if (!(hours < 1)) {
            timeLeftFormat = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeLeftFormat = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }

        displayTimerField.setText(timeLeftFormat);
    }

    /**
     * Method that edits the visibility / invisibility of the activity buttons
     * depending on the current state
     */
    private void updateLayoutVisibility() {
        if (timerWorkingState) {                            // Timer is currently running
            editTextInput.setVisibility(View.INVISIBLE);
            setTimeBtn.setVisibility(View.INVISIBLE);
            resetTimerBtn.setVisibility(View.VISIBLE);
            startTimerBtn.setText(R.string.btnTextPause);
            resetTimerBtn.setText("Stop");
        } else {                                            // Timer is not running
            editTextInput.setVisibility(View.VISIBLE);
            setTimeBtn.setVisibility(View.VISIBLE);
            startTimerBtn.setText(R.string.timerTextResume);
            resetTimerBtn.setText("Reset");

            if (leftTimeInMilli < startTimeInMilli) {
                resetTimerBtn.setVisibility(View.VISIBLE);
            } else {
                resetTimerBtn.setVisibility(View.INVISIBLE);
            }

            if (leftTimeInMilli < COUNTDOWN_INTERVAL) {
                startTimerBtn.setVisibility(View.INVISIBLE);
            } else {
                startTimerBtn.setVisibility(View.VISIBLE);
            }
        }

        if (!isFirstTime) {
            isFirstTime = true;
            startTimerBtn.setText(R.string.btnTextStart);
            resetTimerBtn.setVisibility(View.INVISIBLE);
        } else {
            if (!timerWorkingState) {
                startTimerBtn.setText(R.string.timerTextResume);
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

        setPresetTimer(oneMinBtn, DEFAULT_SETTING_1);
        setPresetTimer(twoMinBtn, DEFAULT_SETTING_2);
        setPresetTimer(threeMinBtn, DEFAULT_SETTING_3);
        setPresetTimer(fiveMinBtn, DEFAULT_SETTING_4);
        setPresetTimer(tenMinBtn, DEFAULT_SETTING_5);
    }

    /**
     * Assigns a time to be set when the button parameter is pressed.
     */
    private void setPresetTimer(Button presetTimeBtn, int inputInMilli) {
        inputInMilli *= CONVERT_MILLIS_TO_SECONDS;
        int finalInputInMilli = inputInMilli;
        presetTimeBtn.setOnClickListener(v -> {
            if (backgroundTimerCountDown != null) {
                pauseTimer();
            }
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
        if (backgroundTimerCountDown != null) {
            backgroundTimerCountDown.cancel();
            isFirstTime = true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        startTimeInMilli = prefs.getLong("startTimeInMillis", INITIAL_DEFAULT);
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
}
