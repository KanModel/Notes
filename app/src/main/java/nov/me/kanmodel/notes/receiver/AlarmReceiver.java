package nov.me.kanmodel.notes.receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.util.Date;
import java.util.Objects;

import nov.me.kanmodel.notes.MainActivity;
import nov.me.kanmodel.notes.R;
import nov.me.kanmodel.notes.utils.TimeAid;
import nov.me.kanmodel.notes.widget.NoteAppWidget;

/**
 * 接受通知
 * Created by KanModel on 2017/12/30.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    private static final int NOTIFICATION_ID = 1000;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "NOTE.NOTIFICATION")) {
//            long minute = intent.getLongExtra("min", 0);
//            if (minute > 0) {
//                Log.d(TAG, "onReceive: 剩余分钟 :" + --minute + " " + intent.getStringExtra("title"));
//                setAlarm(context, minute, intent.getStringExtra("title"));
//            } else {
            Log.d(TAG, "onReceive: title :" + intent.getStringExtra("title"));
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent intent2 = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent2, 0);
            Notification notify = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.logo_appwidget_preview)
                    .setContentTitle("时间便笺提醒")
//                    .setSound(Uri.fromFile(new File("/system/media/audio/alarms/wr.ogg")))
                    .setSound(Uri.parse("android.resource://" + NoteAppWidget.getmContext().getPackageName() + "/" + R.raw.wr))
                    .setVibrate(new long[]{0, 1000, 1000, 1000})
                    .setLights(Color.GREEN, 1000, 1000)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(intent.getStringExtra("title")))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setNumber(1).build();
            manager.notify(NOTIFICATION_ID, notify);
        }
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void setAlarm(Context context, long minute, String title) {
        Log.d(TAG, "setAlarm: minute: " + minute);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction("NOTE.NOTIFICATION");
        intent.putExtra("title", title);
        intent.putExtra("min", minute);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int type = AlarmManager.RTC_WAKEUP;
        //new Date()：表示当前日期，可以根据项目需求替换成所求日期
        //getTime()：日期的该方法同样可以表示从1970年1月1日0点至今所经历的毫秒数
//        long triggerAtMillis = new Date().getTime() + time;
//        long triggerAtMillis = TimeAid.getNowTime() + time;
//        long intervalMillis = 1000 * 60;
//        manager.setInexactRepeating(type, triggerAtMillis, intervalMillis, pi);
//        long triggerAtMillis = TimeAid.getNowTime() + 1000 * 60;
        long triggerAtMillis = TimeAid.getNowTime() + 1000 * 60 * minute;
        if (manager != null) {
            if (Build.VERSION.SDK_INT >= 19) {
                manager.setExact(type, triggerAtMillis, pi);
            } else {
                manager.set(type, triggerAtMillis, pi);
            }
        }
    }
}
