package cmpt276.as3.cmpt276hydrogenproject;

import static android.content.Context.VIBRATOR_SERVICE;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationBroadcast extends BroadcastReceiver {
    private MediaPlayer soundEffectPlayer;
    @Override
    //code was inspired by https://www.youtube.com/watch?v=nl-dheVpt8o
    //up until line 31
    public void onReceive(Context context, Intent intent) {
        Intent thisIntent = TimeoutActivity.makeIntent(context);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, thisIntent, 0);

        String CHANNEL_ID = "channel";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("TITLE")
                .setContentText("TEXT BODY")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        int notificationId = 1;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());

        //countdownSound(context);
    }
    private void countdownSound(Context context) {
        if(soundEffectPlayer == null) {
            soundEffectPlayer = MediaPlayer.create(context, R.raw.mgs_alert_sound);
        }
        soundEffectPlayer.start();
        Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
    }
}