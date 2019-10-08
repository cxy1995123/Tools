package chen.com.library.tools;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;


import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationUtils {


    public static boolean checkNotificationIsOpen(Context context) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        return manager.areNotificationsEnabled();
    }



    public static void showNotification(Context context, String title, String content, boolean isFull) {
        String channelId = "1";
        Intent intent = new Intent();

        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
           NotificationCompat.Builder builder = new NotificationCompat.Builder(context,channelId);

            builder.setChannelId(channelId);
            builder.setAutoCancel(true);
            builder.setSmallIcon(0);
            builder.setContentTitle(title);
            builder.setContentText(content);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(pendingIntent);
            builder.setShowWhen(true);
            builder.setWhen(System.currentTimeMillis());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isFull) {
                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                builder.setFullScreenIntent(pendingIntent, false);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, "id", NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(channel);
            }
            manager.notify(314401, builder.build());
        }


    }

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel channel = getChannels("1");

            NotificationChannel channel2 = getChannels("2");

            NotificationChannel channel3 = getChannels("3");

            List<NotificationChannel> channels = new ArrayList<>();

            channels.add(channel);
            channels.add(channel2);
            channels.add(channel3);

            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannels(channels);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static NotificationChannel getChannels(String id) {
        // 通知渠道的id

        // 用户可以看到的通知渠道的名字.
        CharSequence name = "notification channel" + id;
        // 用户可以看到的通知渠道的描述
        String description = "notification description" + id;
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
        // 配置通知渠道的属性
        mChannel.setDescription(description);
        // 设置通知出现时的闪灯（如果 android 设备支持的话）
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.RED);
        // 设置通知出现时的震动（如果 android 设备支持的话）
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        //最后在notificationmanager中创建该通知渠道
        return mChannel;
    }


}
