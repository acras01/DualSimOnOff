package ua.od.acros.dualsimonoff;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;

import org.joda.time.DateTime;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        final String ALARM_ACTION = "ua.od.acros.dualsimonoff.ALARM";

        SharedPreferences prefs = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        DateTime alarmTime;

        if (prefs.getInt("sim1_sel", 0) == 1) {
            Intent i1Off = new Intent(context, OnOffReceiver.class);
            i1Off.putExtra("sim", "sim1");
            i1Off.putExtra("action", false);
            i1Off.setAction(ALARM_ACTION);
            final int SIM1_OFF = 100;
            PendingIntent pi1Off = PendingIntent.getBroadcast(context, SIM1_OFF, i1Off, 0);
            alarmTime = new DateTime().withHourOfDay(Integer.valueOf(prefs.getString("sim1on_time", "00:05").split(":")[0]))
                    .withMinuteOfHour(Integer.valueOf(prefs.getString("sim1on_time", "00:05").split(":")[1]))
                    .withSecondOfMinute(0);
            if (alarmTime.getMillis() < System.currentTimeMillis())
                alarmTime.plusDays(1);
            am.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getMillis(), AlarmManager.INTERVAL_DAY, pi1Off);

            Intent i1On = new Intent(context, OnOffReceiver.class);
            i1On.putExtra("sim", "sim1");
            i1On.putExtra("action", true);
            i1On.setAction(ALARM_ACTION);
            final int SIM1_ON = 101;
            PendingIntent pi1On = PendingIntent.getBroadcast(context, SIM1_ON, i1On, 0);
            alarmTime = new DateTime().withHourOfDay(Integer.valueOf(prefs.getString("sim1off_time", "00:05").split(":")[0]))
                    .withMinuteOfHour(Integer.valueOf(prefs.getString("sim1off_time", "00:05").split(":")[1]))
                    .withSecondOfMinute(0);
            if (alarmTime.getMillis() < System.currentTimeMillis())
                alarmTime.plusDays(1);
            am.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getMillis(), AlarmManager.INTERVAL_DAY, pi1On);
        }

        if (prefs.getInt("sim2_sel", 0) == 1) {
            Intent i2Off = new Intent(context, OnOffReceiver.class);
            i2Off.putExtra("sim", "sim1");
            i2Off.putExtra("action", false);
            i2Off.setAction(ALARM_ACTION);
            final int SIM2_OFF = 100;
            PendingIntent pi2Off = PendingIntent.getBroadcast(context, SIM2_OFF, i2Off, 0);
            alarmTime = new DateTime().withHourOfDay(Integer.valueOf(prefs.getString("sim2on_time", "00:05").split(":")[0]))
                    .withMinuteOfHour(Integer.valueOf(prefs.getString("sim2on_time", "00:05").split(":")[1]))
                    .withSecondOfMinute(0);
            if (alarmTime.getMillis() < System.currentTimeMillis())
                alarmTime.plusDays(1);
            am.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getMillis(), AlarmManager.INTERVAL_DAY, pi2Off);

            Intent i2On = new Intent(context, OnOffReceiver.class);
            i2On.putExtra("sim", "sim1");
            i2On.putExtra("action", true);
            i2On.setAction(ALARM_ACTION);
            final int SIM2_ON = 101;
            PendingIntent pi2On = PendingIntent.getBroadcast(context, SIM2_ON, i2On, 0);
            alarmTime = new DateTime().withHourOfDay(Integer.valueOf(prefs.getString("sim2off_time", "00:05").split(":")[0]))
                    .withMinuteOfHour(Integer.valueOf(prefs.getString("sim2off_time", "00:05").split(":")[1]))
                    .withSecondOfMinute(0);
            if (alarmTime.getMillis() < System.currentTimeMillis())
                alarmTime.plusDays(1);
            am.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getMillis(), AlarmManager.INTERVAL_DAY, pi2On);
        }

        boolean sim1 = prefs.getBoolean("sim1_state", true);
        boolean sim2 = prefs.getBoolean("sim2_state", true);
        int mode;
        if (sim1) {
            if (sim2)
                mode = prefs.getInt("on", 0);
            else
                mode = prefs.getInt("sim1", 0);
        } else {
            if (sim2)
                mode = prefs.getInt("sim2", 0);
            else
                mode = prefs.getInt("off", 0);
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
}
