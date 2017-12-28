package nov.me.kanmodel.notes.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import nov.me.kanmodel.notes.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MyRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context mContext;
    public static List<String> mList = new ArrayList<>();

    /*
     * 构造函数
     */
    public MyRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
    }

    /*
     * MyRemoteViewsFactory调用时执行，这个方法执行时间超过20秒回报错。
     * 如果耗时长的任务应该在onDataSetChanged或者getViewAt中处理
     */
    @Override
    public void onCreate() {
        // 需要显示的数据
        mList.add("12-27 16:43:46.133 1832-8215/? W/NetworkIdentity: Active mobile network without subscriber!\n" +
                "12-27 16:43:46.133 1832-8215/? W/NetworkIdentity: Active mobile network without subscriber!\n" +
                "12-27 16:43:46.133 1832-8215/? W/NetworkIdentity: Active mobile network without subscriber!\n" +
                "12-27 16:43:46.133 1832-8215/? W/NetworkIdentity: Active mobile network without subscriber!\n" +
                "12-27 16:43:46.136 1832-3267/? W/NetworkIdentity: Active mobile network without subscriber!\n" +
                "12-27 16:43:46.136 1832-3267/? W/NetworkIdentity: Active mobile network without subscriber!\n" +
                "12-27 16:43:46.137 1832-3267/? W/NetworkIdentity: Active mobile network without subscriber!\n" +
                "12-27 16:43:46.137 1832-3267/? W/NetworkIdentity: Active mobile network without subscriber!\n" +
                "12-27 16:43:46.137 1832-3267/? W/NetworkIdentity: Active mobile network without subscriber!\n" +
                "12-27 16:43:46.137 1832-3267/? W/NetworkIdentity: Active mobile network without subscriber!\n" +
                "12-27 16:43:46.137 1832-3267/? W/NetworkIdentity: Active mobile network without subscriber!\n" +
                "12-27 16:43:46.137 1832-3267/? W/NetworkIdentity: Active mobile network without subscriber!\n" +
                "12-27 16:43:46.137 1832-3267/? W/NetworkIdentity: Active mobile network without subscriber!\n" +
                "12-27 16:43:46.137 1832-3267/? W/NetworkIdentity: Active mobile network without subscriber!\n" +
                "12-27 16:43:46.138 1832-3267/? W/NetworkIdentity: Active mobile network without subscriber!\n" +
                "12-27 16:43:46.479 1832-2069/? D/sensors_hal_Time: time_service_sensor1_cb: msg_type 2\n" +
                "12-27 16:43:46.479 1832-2069/? D/sensors_hal_Time: time_service_sensor1_cb: Sn 24, msg Id 3, txn Id 0\n" +
                "12-27 16:43:46.480 1832-2069/? D/sensors_hal_Time: tsOffsetIs: Apps: 228526057139014; DSPS: 3193440045; Offset : 131070001093301 (diff 34010 @0)\n" +
                "12-27 16:43:46.563 1832-2340/? D/WifiStateMachine:  ConnectedState !CMD_RSSI_POLL  rt=149493448/228526135 94 0 \"KO5\" c4:04:15:8f:a7:e1 rssi=-44 f=5180 sc=60 link=150 tx=1.2, 0.0, 0.0  rx=2.3 bcn=0 [on:0 tx:0 rx:0 period:3007] from screen [on:0 period:-1759228927] gl hn rssi=-39 ag=0 hr ls-=0 [56,56,56,56,61] brc=0 lrc=0 offload-stopped\n" +
                "12-27 16:43:46.571 1832-2340/? D/WifiStateMachine:  L2ConnectedState !CMD_RSSI_POLL  rt=149493456/228526143 94 0 \"KO5\" c4:04:15:8f:a7:e1 rssi=-44 f=5180 sc=60 link=150 tx=1.2, 0.0, 0.0  rx=2.3 bcn=0 [on:0 tx:0 rx:0 period:9] from screen [on:0 period:-1759228918] gl hn rssi=-39 ag=0 hr ls-=0 [56,56,56,56,61] brc=0 lrc=0 offload-stopped\n" +
                "12-27 16:43:46.571 1832-2340/? D/WifiStateMachine:  get link layer stats 0\n" +
                "12-27 16:43:46.573 1832-2340/? D/WifiNative-wlan0: doString: [SIGNAL_POLL]\n" +
                "12-27 16:43:46.575 2598-2598/? D/wpa_supplicant: wlan0: Control interface command 'SIGNAL_POLL'\n" +
                "12-27 16:43:46.635 1832-2340/? D/WifiStateMachine: fetchRssiLinkSpeedAndFrequencyNative rssi=-44 linkspeed=150 freq=5180\n" +
                "12-27 16:43:46.635 1832-2340/? D/WifiConfigManager: updateConfiguration freq=5180 BSSID=c4:04:15:8f:a7:e1 RSSI=-44 \"KO5\"WPA_PSK\n" +
                "12-27 16:43:46.640 1832-2340/? D/WifiStateMachine: calculateWifiScore freq=5180 speed=150 score=60 highRSSI  -> txbadrate=0.00 txgoodrate=0.58 txretriesrate=0.00 rxrate=1.17 userTriggerdPenalty0\n" +
                "12-27 16:43:46.641 1832-2340/? D/WifiStateMachine:  good link -> stuck count =0\n" +
                "12-27 16:43:46.643 1832-2340/? D/WifiStateMachine:  badRSSI count0 lowRSSI count0 --> score 56\n" +
                "12-27 16:43:46.643 1832-2340/? D/WifiStateMachine:  isHighRSSI       ---> score=61\n" +
                "12-27 16:43:47.356 2518-2518/? I/NetworkController.WifiSignalController: Change in state to: connected=true,enabled=true,level=3,inetCondition=1,iconGroup=IconGroup(Default Wi-Fi Icons),activityIn=true,activityOut=true,rssi=-54,,ssid=\"KO5\",isGigaWiFi=false,isAttWifiCall=false\n" +
                "12-27 16:43:47.907 1144-2026/? V/Netd: unexpected event from subsystem power_supply\n" +
                "12-27 16:43:47.911 1144-2026/? V/Netd: unexpected event from subsystem power_supply\n" +
                "12-27 16:43:47.924 2518-2518/? D/KeyguardUpdateMonitor: Intent.ACTION_BATTERY_CHANGED status : 5 ,plugged : 2 ,level : 100 ,temperature : 230, temperatureState : 0, EXTRA_CHARGING_CURRENT : 0 / EXTRA_HVDCP_TYPE : false\n" +
                "12-27 16:43:47.926 2518-2518/? I/LGPowerUI: onReceive = android.intent.action.BATTERY_CHANGED\n" +
                "12-27 16:43:47.930 1036-2376/? V/APM::Devices: DeviceVector::refreshTypes() mDeviceTypes 00004000\n" +
                "12-27 16:43:47.930 1036-2376/? V/APM::Devices: DeviceVector::getDevice() for type 00004000 address  found 0x0\n" +
                "12-27 16:43:47.930 2819-2819/? D/UsbHostManagerService: intent receive : android.intent.action.BATTERY_CHANGED\n" +
                "12-27 16:43:47.931 2518-2518/? I/LGPowerUI: level = 100, plugType = 2, plugged = true, charging = false, temperature = 230, chargingCurrent = 0, factoryCableItem = 0, isFastCharging = false, batteryID = 1\n" +
                "12-27 16:43:47.933 2518-2886/? I/AbsQuickSettingsHandlerBase: Got action android.intent.action.BATTERY_CHANGED for BATTERY_SAVER\n" +
                "12-27 16:43:47.934 3392-3610/? W/QCNEJ: |CORE| CNE received unexpected action: android.intent.action.BATTERY_CHANGED\n" +
                "12-27 16:43:47.937 7070-7070/? D/TeleService: PhoneGlobalsEx: onReceive: android.intent.action.BATTERY_CHANGED\n" +
                "12-27 16:43:47.943 24146-24146/? W/MainApplication: onReceive intent : Intent { act=android.in");
//        for (int i = 0; i < 5; i++) {
//            mList.add("item"+ i);
//        }
    }

    /**
     * 当调用notifyAppWidgetViewDataChanged方法时，触发这个方法
     * 例如：MyRemoteViewsFactory.notifyAppWidgetViewDataChanged();
     */
    @Override
    public void onDataSetChanged() {

    }

    /**
     * 这个方法不用多说了把，这里写清理资源，释放内存的操作
     */
    @Override
    public void onDestroy() {
        mList.clear();
    }

    /**
     * 返回集合数量
     */
    @Override
    public int getCount() {
        return mList.size();
    }

    /**
     * 创建并且填充，在指定索引位置显示的View，这个和BaseAdapter的getView类似
     */
    @Override
    public RemoteViews getViewAt(int position) {
        if (position < 0 || position >= mList.size())
            return null;
        String content = mList.get(position);
        final RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.my_widget_layout_item);// 创建在当前索引位置要显示的View
        views.setTextViewText(R.id.widget_list_item_tv, content);// 设置要显示的内容
//        Intent intent = new Intent();// 填充Intent，填充在AppWdigetProvider中创建的PendingIntent
//        intent.putExtra("content", content);// 传入点击行的数据
//        views.setOnClickFillInIntent(R.id.widget_list_item_tv, intent);
        return views;
    }

    /**
     * 显示一个"加载"View。返回null的时候将使用默认的View
     */
    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    /**
     * 不同View定义的数量。默认为1（本人一直在使用默认值）
     */
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    /**
     * 返回当前索引的。
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 如果每个项提供的ID是稳定的，即她们不会在运行时改变，就返回true（没用过。。。）
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }
}
