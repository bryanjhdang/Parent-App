package cmpt276.as3.cmpt276hydrogenproject;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[] {1000, 1000, 1000, 1000, 1000})
                .setLights(Color.GREEN, 3000, 3000)
                .setAutoCancel(true);

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "name", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("description");

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(1, builder.build());

        //countdownSound(context);
    }
    private void countdownSound(Context context) {
        if(soundEffectPlayer == null) {
            soundEffectPlayer = MediaPlayer.create(context, R.raw.mgs_alert_sound);
        }
        soundEffectPlayer.start();
//        Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
//        vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
    }
}