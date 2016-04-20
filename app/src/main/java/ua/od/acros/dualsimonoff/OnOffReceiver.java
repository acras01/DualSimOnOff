package ua.od.acros.dualsimonoff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import java.util.Calendar;

public class OnOffReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        SharedPreferences prefs = context.getSharedPreferences("ua.od.acros.dualsimonoff_preferences", Context.MODE_PRIVATE);
        String sim = intent.getStringExtra("sim");
        boolean action = intent.getBooleanExtra("action", true);
        String key = "";
        switch (sim) {
            case "sim1":
                if (action)
                    key = "days1on";
                else
                    key = "days1off";
                break;
            case "sim2":
                if (action)
                    key = "days2on";
                else
                    key = "days2off";
                break;
        }
        if (new DaysOfWeek(prefs.getInt(key, 0)).getSetDays().contains(dayOfWeek)) {
            boolean[] simState = MobileUtils.getSimState(context.getApplicationContext());
            prefs.edit().putBoolean("sim1_state", simState[0]).apply();
            prefs.edit().putBoolean("sim2_state", simState[1]).apply();
            int mode = 0;
            switch (sim) {
                case "sim1":
                    if (simState[1]) {
                        if (action)
                            mode = prefs.getInt("on", 0);
                        else
                            mode = prefs.getInt("sim2", 0);
                    } else {
                        if (action)
                            mode = prefs.getInt("sim1", 0);
                        else
                            mode = prefs.getInt("off", 0);
                    }
                    prefs.edit().putBoolean("sim1_state", action).apply();
                    break;
                case "sim2":
                    if (simState[0]) {
                        if (action)
                            mode = prefs.getInt("on", 0);
                        else
                            mode = prefs.getInt("sim1", 0);
                    } else {
                        if (action)
                            mode = prefs.getInt("sim2", 0);
                        else
                            mode = prefs.getInt("off", 0);
                    }
                    prefs.edit().putBoolean("sim2_state", action).apply();
                    break;
            }
            Intent localIntent;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Settings.System.putInt(context.getContentResolver(), "dual_sim_mode_setting", mode);
                localIntent = new Intent("android.intent.action.DUAL_SIM_MODE");
            } else {
                Settings.System.putInt(context.getContentResolver(), "msim_mode_setting", mode);
                localIntent = new Intent("android.intent.action.MSIM_MODE");
            }
            localIntent.putExtra("mode", mode);
            context.sendBroadcast(localIntent);
        }

        if (wl.isHeld())
            wl.release();
    }
}
