package nov.me.kanmodel.notes.widget;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import nov.me.kanmodel.notes.activity.MainActivity;
import nov.me.kanmodel.notes.activity.adapter.NoteAdapter;
import nov.me.kanmodel.notes.utils.TimeAid;

/**
 * 用于挂件刷新
 * 每 60 * 1000 ms 后发送消息
 */
public class UpdateWidgetService extends Service {
    private Context context;
    private static final String TAG = "UpdateWidgetService";

    @Override
    public IBinder onBind(Intent intent) {
        //Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        context = getApplicationContext();
        Timer timer = new Timer();// 定义计时器
        Log.d(TAG, "onCreate: Build.VERSION.SDK_INT=" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "onCreate: Notification");
            NotificationChannel channel = new NotificationChannel("xxx", "xxx", NotificationManager.IMPORTANCE_LOW);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager == null)
                return;
            manager.createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, "xxx")
                    .setAutoCancel(true)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setOngoing(true)
                    .setPriority(NotificationManager.IMPORTANCE_LOW)
                    .build();

            startForeground(101, notification);
        }
        // 启动周期性调度
        timer.schedule(new TimerTask() {
            public void run() {
                // 发送空消息，通知界面更新
                handler.sendEmptyMessage(0x123);
            }
        }, 0, 1000 * 60);
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, START_STICKY, startId);
    }

    /**
     * 接受消息处理
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                NoteAppWidget.updateAllWidget();
                try {
                    if (MainActivity.getNoteAdapter() != null) {
                        NoteAdapter noteAdapter = MainActivity.getNoteAdapter();
                        noteAdapter.refreshAllDataForce();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getApplicationContext(), UpdateWidgetService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Log.d(TAG, "handleMessage: startForegroundService " + TimeAid.INSTANCE.getNowTime());
                    context.startForegroundService(intent);
                } else {
                    Log.d(TAG, "handleMessage: startService " + TimeAid.INSTANCE.getNowTime());
                    context.startService(intent);
                }
            }
            super.handleMessage(msg);
        }
    };
}
