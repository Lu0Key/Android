package cn.edu.sdu.online.isdu.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;


public class NotificationUtil {
    static NotificationManager manager;
    static NotificationCompat.Builder builder; // API26以下
    static Notification.Builder nBuilder; // API26及以上
    static NotificationChannel notificationChannel;
    static Context mContext;

    public static class Builder {
        private int smallIconRes = 0;
        private int largeIconRes = 0;
        private String title;
        private String message;
        private PendingIntent pendingIntent;
        private String channelId = "channel_id";
        private int notifyId = 0;
        private long when = 0;
        private boolean autoCancel = false;
        private boolean vibrate = false;
        private boolean lights = false;
        private int lightColor = Color.GREEN;
        private boolean useDefault = false;
        private boolean longText = false;
        private boolean bigPicture = false;
        private Bitmap bigBitmap;
        private boolean onGoing = false; // 常驻通知栏
        private String ticker;
        private int priority = NotificationCompat.PRIORITY_MAX;
        List<NotificationCompat.Action> actionList = new ArrayList<>();
        List<Notification.Action> nActionList = new ArrayList<>();

        public Builder(Context context) {
            mContext = context;
            manager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        }

        public Builder addAction(NotificationCompat.Action action) {
            this.actionList.add(action);
            return this;
        }

        public Builder addAction(Notification.Action action) {
            this.nActionList.add(action);
            return this;
        }

        public Builder setOnGoing(boolean onGoing) {
            this.onGoing = onGoing;
            return this;
        }

        public Builder setTicker(String ticker) {
            this.ticker = ticker;
            return this;
        }

        public Builder setSmallIcon(int res) {
            this.smallIconRes = res;
            return this;
        }

        public Builder setLargeIcon(int res) {
            this.largeIconRes = res;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setIntent(PendingIntent pi) {
            this.pendingIntent = pi;
            return this;
        }

        public Builder setChannelId(String id) {
            this.channelId = id;
            return this;
        }

        public Builder setNotifyId(int notifyId) {
            this.notifyId = notifyId;
            return this;
        }

        public Builder setWhen(long when) {
            this.when = when;
            return this;
        }

        public Builder setAutoCancel(boolean autoCancel) {
            this.autoCancel = autoCancel;
            return this;
        }

        public Builder setVibrate(boolean vibrate) {
            this.vibrate = vibrate;
            return this;
        }

        public Builder setLights(boolean lights) {
            this.lights = lights;
            return this;
        }

        public Builder setLightColor(int lightColor) {
            this.lightColor = lightColor;
            return this;
        }

        public Builder setUseDefault(boolean useDefault) {
            this.useDefault = useDefault;
            return this;
        }

        public Builder setLongText(boolean longText) {
            this.longText = longText;
            return this;
        }

        public Builder setBigPicture(boolean bigPicture) {
            this.bigPicture = bigPicture;
            return this;
        }

        public Builder setPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder setBigBitmap(Bitmap bigBitmap) {
            this.bigBitmap = bigBitmap;
            return this;
        }

        public Notification build() {
            Intent intentClick = new Intent(mContext, NotificationBroadcastReceiver.class);
            intentClick.setAction("notification_clicked");
            intentClick.putExtra("notify_id", notifyId);
            intentClick.putExtra("channel_id", channelId);
            PendingIntent pendingIntentClick = PendingIntent.getBroadcast(mContext, 0,
                    intentClick, PendingIntent.FLAG_ONE_SHOT);

            Intent intentCancel = new Intent(mContext, NotificationBroadcastReceiver.class);
            intentCancel.setAction("notification_cancelled");
            intentCancel.putExtra("notify_id", notifyId);
            intentCancel.putExtra("channel_id", channelId);
            PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(mContext, 0,
                    intentCancel, PendingIntent.FLAG_ONE_SHOT);
            if (Build.VERSION.SDK_INT < 26) {
                builder = new NotificationCompat.Builder(mContext, channelId);
                if (smallIconRes != 0) builder.setSmallIcon(smallIconRes);
                if (largeIconRes != 0) builder.setLargeIcon(
                        BitmapFactory.decodeResource(mContext.getResources(),
                                largeIconRes));
                if (title != null) builder.setContentTitle(title);
                if (message != null) builder.setContentText(message);
                if (pendingIntent != null) builder.setContentIntent(pendingIntent);
                if (when != 0) builder.setWhen(when);
                builder.setAutoCancel(autoCancel);
                if (vibrate) builder.setVibrate(new long[] {100, 100, 100, 100});
                if (lights) builder.setLights(lightColor, 1000, 1000);
                if (longText) builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
                if (bigPicture) builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bigBitmap));
                if (useDefault) builder.setDefaults(NotificationCompat.DEFAULT_ALL);
                if (onGoing) builder.setOngoing(true);
                if (ticker != null) builder.setTicker(ticker);
                builder.setPriority(priority);

                builder.setContentIntent(pendingIntentClick);
                builder.setDeleteIntent(pendingIntentCancel);

                if (!actionList.isEmpty()) {
                    for (NotificationCompat.Action action : actionList)
                        builder.addAction(action);
                }

                return builder.build();
            } else {
                nBuilder = new Notification.Builder(mContext, channelId);
                notificationChannel =
                        new NotificationChannel(channelId, channelId, priority + 2);
                if (smallIconRes != 0) nBuilder.setSmallIcon(smallIconRes);
                if (largeIconRes != 0) nBuilder.setLargeIcon(
                        BitmapFactory.decodeResource(mContext.getResources(),
                                largeIconRes));
                if (title != null) nBuilder.setContentTitle(title);
                if (message != null) nBuilder.setContentText(message);
                if (pendingIntent != null) nBuilder.setContentIntent(pendingIntent);
                if (when != 0) nBuilder.setWhen(when);
                nBuilder.setAutoCancel(autoCancel);
                if (vibrate) {
                    notificationChannel.enableVibration(true);
                    notificationChannel.setVibrationPattern(new long[] {100, 100, 100, 100});
                }
                if (lights) {
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(lightColor);
                }
                if (longText) nBuilder.setStyle(new Notification.BigTextStyle().bigText(message));
                if (bigPicture) nBuilder.setStyle(new Notification.BigPictureStyle().bigPicture(bigBitmap));
                if (onGoing) nBuilder.setOngoing(true);
                if (ticker != null) nBuilder.setTicker(ticker);

                if (!nActionList.isEmpty()) {
                    for (Notification.Action action : nActionList)
                        nBuilder.addAction(action);
                }

                nBuilder.setContentIntent(pendingIntentClick);
                nBuilder.setDeleteIntent(pendingIntentCancel);

                manager.createNotificationChannel(notificationChannel);
                return nBuilder.build();
            }

        }

        public void show() {
            Notification notification = build();
            manager.notify(notifyId, notification);
        }
    }


    public static void cancel(int id) {
        if (manager != null) manager.cancel(id);
    }

    public static class NotificationBroadcastReceiver extends BroadcastReceiver {

        private static List<OnClickListener> onClickListenerList = new ArrayList<>();
        private static List<OnCancelListener> onCancelListenerList = new ArrayList<>();

        public NotificationBroadcastReceiver() {
            super();
        }

        public static void addOnClickListener(OnClickListener onClickListener) {
            onClickListenerList.add(onClickListener);
        }

        public static void addOnCancelListener(OnCancelListener onCancelListener) {
            onCancelListenerList.add(onCancelListener);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int notifyId = intent.getIntExtra("notify_id", -1);
            String channelId = intent.getStringExtra("channel_id");
            String action = intent.getAction();
            NotificationManager manager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (action == null) return;
            if (action.equals("notification_clicked")) {
                for (int i = 0; i < onClickListenerList.size(); i++)
                    onClickListenerList.get(i).onClick(notifyId, channelId, manager);
            } else if (action.equals("notification_cancelled")) {
                for (int i = 0; i < onCancelListenerList.size(); i++)
                    onCancelListenerList.get(i).onCancel(notifyId, channelId, manager);
            }
        }

    }

    public interface OnClickListener {
        void onClick(int notifyId, String channelId, NotificationManager manager);
    }

    public interface OnCancelListener {
        void onCancel(int notifyId, String channelId, NotificationManager manager);
    }
}
