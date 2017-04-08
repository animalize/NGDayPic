package man.animalize.ngdaypic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

/**
 * Created by 416 on 10-19 019
 */
public class BootCompleteReciver extends BroadcastReceiver {
    private static final String TAG = "BootCompleteReciver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null ||
                !intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED))
            return;

        // 启停服务
        boolean should_on = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean("should_on", false);
        boolean is_on = BackService.isServiceAlarmOn();

        if (should_on != is_on && should_on) {
            BackService.setServiceAlarm(true);
        }
    }
}
