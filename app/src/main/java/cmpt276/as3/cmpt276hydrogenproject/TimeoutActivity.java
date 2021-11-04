package cmpt276.as3.cmpt276hydrogenproject;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class TimeoutActivity extends AppCompatActivity {

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

    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeout_activity);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        displayTimerField = findViewById(R.id.textDisplayTimer);
        editTextInput = findViewById(R.id.minuteTextInput);
        setTimeBtn = findViewById(R.id.btnSetTimer);
        startTimerBtn = findViewById(R.id.btnStartTimer);
        resetTimerBtn = findViewById(R.id.btnResetTimer);

        startTimerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(TimeoutActivity.this, NotificationBroadcast.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(TimeoutActivity.this, 0, intent, 0);
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
                Toast.makeText(TimeoutActivity.this, "Field is empty !", Toast.LENGTH_SHORT).show();
                return;
            }
            // Parse string input into long
            long inputInMilli = Long.parseLong(input) * 60000;
            if (inputInMilli == 0) {
                Toast.makeText(TimeoutActivity.this, "Invalid: Enter 1 minute or greater", Toast.LENGTH_SHORT).show();
                //TODO: remove this!! THIS IS FOR DEBUGGING AND PUT THE RETURN BACK
                inputInMilli = 5000;
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
                //countdownFinished();

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

    private void makeNotification() {
        String CHANNEL_ID = "channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            //NotificationManager notificationManager = getSystemService(NotificationManager.class);
            //notificationManager.createNotificationChannel(channel);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
/*
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("TITLE")
                .setContentText("TEXT BODY")
                .setPriority(NotificationCompat.PRIORITY_MAX);

        int notificationId = 1;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, builder.build());

 */

    }
    /*
    private void countdownFinished() {
        countdownSound();
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
        makeNotification();
    }
     */

    private void countdownFinished() {
        countdownSound();
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
    }
}
