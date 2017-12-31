package nov.me.kanmodel.notes.widget;

import android.annotation.SuppressLint;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import nov.me.kanmodel.notes.MainActivity;
import nov.me.kanmodel.notes.NoteAdapter;
import nov.me.kanmodel.notes.utils.TimeAid;

public class UpdateWidgetService extends Service {
    private Context context;
    private static AppWidgetManager appWidgetManager;
    private static final String TAG = "UpdateWidgetService";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        appWidgetManager = AppWidgetManager.getInstance(context);// 定义计时器
        Timer timer = new Timer();
        // 启动周期性调度
        Log.d(TAG, "onCreate: ");
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
        Log.d(TAG, "onStartCommand: ");
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                Log.d(TAG, "handleMessage: Time :" + TimeAid.getNowTime());
                NoteAppWidget.updateAllWidget();
                try {
                    if (MainActivity.getNoteAdapter() != null) {
                        NoteAdapter noteAdapter = MainActivity.getNoteAdapter();
                        noteAdapter.refreshAllDataForce();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                startService(new Intent(getApplicationContext(), UpdateWidgetService.class));
            }
            super.handleMessage(msg);
        }
    };
}
