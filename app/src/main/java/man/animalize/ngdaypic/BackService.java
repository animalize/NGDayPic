package man.animalize.ngdaypic;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import man.animalize.ngdaypic.Utility.Fetcher;


public class BackService extends IntentService {
    private static final String TAG = "BackService";
    private static int POLL_INTERVAL = 1000 * 3600 * 2;

    public BackService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntent");

        Fetcher f = new Fetcher();
        byte[] data = f.getByte("http://news.sina.com.cn/");

        if (data != null) {
            Log.i(TAG, "html byte lenth: " + data.length);
        }
    }

    // 启停服务
    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = new Intent(context, BackService.class);
        PendingIntent pi = PendingIntent.getService(
                context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            alarmManager.setRepeating(AlarmManager.RTC,
                    System.currentTimeMillis(), POLL_INTERVAL, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    // 检测服务是否已启动
    public static boolean isServiceAlarmOn(Context context) {
        Intent i = new Intent(context, BackService.class);
        PendingIntent pi = PendingIntent.getService(
                context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }
}
