package cmpt276.as3.cmpt276hydrogenproject;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

public class NotificationBroadcast extends BroadcastReceiver {
    private final long[] VIBRATE_PATTERN = new long[] {0, 1000, 1000, 1000, 1000, 1000, 1000, 1000,
            1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000,
            1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000 ,1000, 1000, 1000, 1000, 1000,
            1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000};
    @Override
    //code was inspired by https://www.youtube.com/watch?v=nl-dheVpt8o
    //up until line 31
    public void onReceive(Context context, Intent intent) {

        Intent thisIntent = TimeoutActivity.makeIntent(context);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, thisIntent, 0);

        String CHANNEL_ID = "CHANNEL";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("My Parent App")
                .setContentText("The timeout has ended!")
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setContentIntent(pendingIntent)
                .setVibrate(VIBRATE_PATTERN)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true);

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "name", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("description");
        channel.enableVibration(true);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();
        channel.setSound(Settings.System.DEFAULT_RINGTONE_URI, audioAttributes);
        channel.setVibrationPattern(VIBRATE_PATTERN);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(0, builder.build());
    }
}
