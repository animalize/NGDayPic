package man.animalize.ngdaypic;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.util.ArrayList;

import man.animalize.ngdaypic.Base.DayPicItem;
import man.animalize.ngdaypic.Base.DayPicParsers;
import man.animalize.ngdaypic.Base.MyDBHelper;
import man.animalize.ngdaypic.Base.PictureUtils;
import man.animalize.ngdaypic.Utility.Fetcher;
import man.animalize.ngdaypic.Utility.FileReadWrite;


public class BackService extends IntentService {
    public static final String FILTER = "man.animalize.ngdaypic.got";
    private static final String TAG = "BackService";
    private static int POLL_INTERVAL_HOUR = 6;

    private LocalBroadcastManager mLBM =
            LocalBroadcastManager.getInstance(this);

    // 用于在主线程显示toast
    private Handler mHandler;

    public BackService() {
        super(TAG);
    }


    // 得到刷新间隔，小时数
    public static int getIntervalHour() {
        return POLL_INTERVAL_HOUR;
    }

    // 启停服务
    public static void setServiceAlarm(boolean isOn) {
        Context context = App.getContext();

        // 创建或得到已有的PendingIntent
        Intent i = new Intent(context, BackService.class);
        PendingIntent pi = PendingIntent.getService(
                context, 0, i, 0);

        // 得到AlarmManager
        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }

        // 启动 或 停止
        String t;
        if (isOn) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis(),
                    POLL_INTERVAL_HOUR * 1000 * 3600,
                    pi);

            t = "每日图片:正在启动服务";
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
            t = "每日图片:正在停止服务";
        }
        Toast.makeText(context, t, Toast.LENGTH_SHORT).show();
    }

    // 检测服务是否已启动
    // FLAG_NO_CREATE不创建、只查询
    // 如果已启动，pi会是PendingIntent
    public static boolean isServiceAlarmOn() {
        Context context = App.getContext();

        Intent i = new Intent(context, BackService.class);
        PendingIntent pi = PendingIntent.getService(
                context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        boolean allow3g = intent.getBooleanExtra("allow3g", false);

        if (!allow3g) {
            // 检查wifi状态
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (!mWifi.isConnected()) {
                toast("每日图片：WIFI不可用");
                return;
            }
        }

        Fetcher f = new Fetcher();

        // 分区获取英文、中文网站
        boolean r1 = doWork(1, f);
        boolean r2 = doWork(2, f);
        if (!r1 && !r2)
            return;

        // 广播，让ListFragment刷新内容
        Intent i = new Intent(FILTER);
        mLBM.sendBroadcast(i);
    }

    // Toast只能在主UI线程使用
    private void toast(final String str) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BackService.this,
                        str,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 获取网站的内容
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private boolean doWork(int type, Fetcher f) {
        DayPicItem item;

        // 解析html
        try {
            if (type == 1)
                item = DayPicParsers.NGDayPicParser(f);
            else
                item = DayPicParsers.CNNGDayPicParser(f);
        } catch (final Exception e) {
            toast(e.toString());
            //Log.i(TAG, e.toString());
            return false;
        }

        MyDBHelper dbhelper = MyDBHelper.getInstance(getApplicationContext());
        byte[] imgBuffer = null;
        long id;
        ArrayList<Integer> list;

        synchronized (dbhelper) {
            // title已存在?
            if (dbhelper.isTitleDateExist(item)) {
                toast("已存在:" + item.getTitle());
                return false;
            }

            if (item.getPicurl() != null) {
                // 下载图片
                imgBuffer = f.getByte(item.getPicurl());
                if (imgBuffer == null)
                    return false;
                //Log.i(TAG, "原图大小" + imgBuffer.length);

                // 缩略图
                byte[] icon = PictureUtils.getIcon(imgBuffer);
                item.setIcon(icon);
                //Log.i(TAG, "缩略图大小" + icon.length);
            }

            // 写入数据库
            id = dbhelper.insertItem(item);
            item.set_id(id);

            // 删除旧有数据
            list = dbhelper.delOlds();
        }

        // 保存图片
        if (imgBuffer != null) {
            if (!FileReadWrite.writeFile(imgBuffer, id + ".jpg"))
                return false;
        }

        // 删除旧有图片
        if (list != null) {
            for (int i : list) {
                FileReadWrite.delFile(i + ".jpg");
            }
        }

        // 通知栏
        if (Build.VERSION.SDK_INT >= 16 && !MainListFragment.isRunning()) {
            Intent i = new Intent(this, MainListActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.putExtra("item", item);
            PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

            Notification n = new Notification.Builder(this)
                    .setTicker("每日地理图片更新了")
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(item.getTitle())
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();
            NotificationManager nm =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(1, n);
        }

        return true;
    }
}
