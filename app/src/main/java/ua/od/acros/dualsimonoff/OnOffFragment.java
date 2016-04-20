package ua.od.acros.dualsimonoff;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.joda.time.DateTime;

public class OnOffFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener,
        SharedPreferences.OnSharedPreferenceChangeListener, ChooseDaysDialog.ChooseDaysDialogClosedListener {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private AppCompatSpinner sim1;
    private AppCompatButton b1, b2, b3, b4;
    private SharedPreferences prefs;

    private final String SIM1 = "sim1_sel";
    private final String SIM2 = "sim2_sel";
    private DaysOfWeek days1on, days1off, days2on, days2off;

    public OnOffFragment() {
    }

    public static OnOffFragment newInstance(int sectionNumber) {
        OnOffFragment fragment = new OnOffFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onoff, container, false);
        sim1 = (AppCompatSpinner) view.findViewById(R.id.sim1);
        AppCompatSpinner sim2 = (AppCompatSpinner) view.findViewById(R.id.sim2);
        prefs = getActivity().getSharedPreferences("ua.od.acros.dualsimonoff_preferences", Context.MODE_PRIVATE);
        sim1.setSelection(prefs.getInt(SIM1, 0));
        sim2.setSelection(prefs.getInt(SIM2, 0));
        b1 = (AppCompatButton) view.findViewById(R.id.sim1on);
        b1.setText(prefs.getString("sim1on_time", "00:00"));
        b2 = (AppCompatButton) view.findViewById(R.id.sim1off);
        b2.setText(prefs.getString("sim1off_time", "00:00"));
        b3 = (AppCompatButton) view.findViewById(R.id.sim2on);
        b3.setText(prefs.getString("sim2on_time", "00:00"));
        b4 = (AppCompatButton) view.findViewById(R.id.sim2off);
        b4.setText(prefs.getString("sim2off_time", "00:00"));
        b1.setEnabled(false);
        b2.setEnabled(false);
        b3.setEnabled(false);
        b4.setEnabled(false);
        if (prefs.getInt(SIM1, 0) == 1) {
            b1.setEnabled(true);
            b2.setEnabled(true);
        }
        if (prefs.getInt(SIM2, 0) == 1) {
            b3.setEnabled(true);
            b4.setEnabled(true);
        }
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
        sim1.setOnItemSelectedListener(this);
        sim2.setOnItemSelectedListener(this);

        days1on = new DaysOfWeek(prefs.getInt("days1on", 0));
        days1off = new DaysOfWeek(prefs.getInt("days1off", 0));
        days2on = new DaysOfWeek(prefs.getInt("days2on", 0));
        days2off = new DaysOfWeek(prefs.getInt("days1off", 0));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        String sim = "";
        boolean action = true;
        int bitSet = 0;
        String dialog = "";
        switch (v.getId()) {
            case R.id.sim1on:
                sim = "sim1";
                action = true;
                dialog = "time";
                break;
            case R.id.sim1off:
                sim = "sim1";
                action = false;
                dialog = "time";
                break;
            case R.id.sim2on:
                sim = "sim2";
                action = true;
                dialog = "time";
                break;
            case R.id.sim2off:
                sim = "sim2";
                action = false;
                dialog = "time";
                break;
            case R.id.days1on:
                sim = "sim1";
                action = true;
                dialog = "days";
                bitSet = days1on.getBitSet();
                break;
            case R.id.days10ff:
                sim = "sim1";
                action = false;
                dialog = "days";
                bitSet = days1off.getBitSet();
                break;
            case R.id.days2on:
                sim = "sim2";
                action = true;
                dialog = "days";
                bitSet = days2on.getBitSet();
                break;
            case R.id.days2off:
                sim = "sim2";
                action = false;
                dialog = "days";
                bitSet = days2off.getBitSet();
                break;
        }
        if (dialog.equals("days"))
            ChooseDaysDialog.newInstance(sim, action, bitSet).show(getActivity().getSupportFragmentManager(), dialog);
        else
            TimePickerDialog.newInstance(sim, action).show(getActivity().getSupportFragmentManager(), dialog);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        AppCompatButton butOn;
        AppCompatButton butOff;
        String setting;
        if (parent == sim1) {
            setting = SIM1;
            butOn = b1;
            butOff = b2;
        } else {
            setting = SIM2;
            butOn = b3;
            butOff = b4;
        }
        prefs.edit().putInt(setting, position).apply();
        switch (position) {
            case 1:
                butOn.setEnabled(true);
                butOff.setEnabled(true);
                break;
            case 0:
                butOn.setEnabled(false);
                butOff.setEnabled(false);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        b1.setText(prefs.getString("sim1on_time", "00:00"));
        b2.setText(prefs.getString("sim1off_time", "00:00"));
        b3.setText(prefs.getString("sim2on_time", "00:00"));
        b4.setText(prefs.getString("sim2off_time", "00:00"));

        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        DateTime alarmTime;
        final String ALARM_ACTION = "ua.od.acros.dualsimonoff.ALARM";
        if ((key.equals(SIM1) && sharedPreferences.contains("sim1off_time")) || key.equals("sim1off_time")) {
            Intent i1Off = new Intent(getActivity(), OnOffReceiver.class);
            i1Off.putExtra("sim", "sim1");
            i1Off.putExtra("action", false);
            i1Off.setAction(ALARM_ACTION);
            final int SIM1_OFF = 100;
            PendingIntent pi1Off = PendingIntent.getBroadcast(getActivity(), SIM1_OFF, i1Off, 0);
            if (sharedPreferences.getInt(SIM1, 0) == 1) {
                am.cancel(pi1Off);
                alarmTime = new DateTime().withHourOfDay(Integer.valueOf(sharedPreferences.getString("sim1off_time", "23:55").split(":")[0]))
                        .withMinuteOfHour(Integer.valueOf(sharedPreferences.getString("sim1off_time", "23:55").split(":")[1]))
                        .withSecondOfMinute(0);
                if (alarmTime.getMillis() < System.currentTimeMillis())
                    alarmTime = alarmTime.plusDays(1);
                am.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getMillis(), AlarmManager.INTERVAL_DAY, pi1Off);
            } else
                am.cancel(pi1Off);
        }
        if ((key.equals(SIM1) && sharedPreferences.contains("sim1on_time")) || key.equals("sim1on_time")) {
            Intent i1On = new Intent(getActivity(), OnOffReceiver.class);
            i1On.putExtra("sim", "sim1");
            i1On.putExtra("action", true);
            i1On.setAction(ALARM_ACTION);
            final int SIM1_ON = 101;
            PendingIntent pi1On = PendingIntent.getBroadcast(getActivity(), SIM1_ON, i1On, 0);
            if (sharedPreferences.getInt(SIM1, 0) == 1) {
                am.cancel(pi1On);
                alarmTime = new DateTime().withHourOfDay(Integer.valueOf(sharedPreferences.getString("sim1on_time", "00:05").split(":")[0]))
                        .withMinuteOfHour(Integer.valueOf(sharedPreferences.getString("sim1on_time", "00:05").split(":")[1]))
                        .withSecondOfMinute(0);
                if (alarmTime.getMillis() < System.currentTimeMillis())
                    alarmTime = alarmTime.plusDays(1);
                am.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getMillis(), AlarmManager.INTERVAL_DAY, pi1On);
            } else
                am.cancel(pi1On);
        }
        if ((key.equals(SIM2) && sharedPreferences.contains("sim2off_time")) || key.equals("sim2off_time")) {
            Intent i2Off = new Intent(getActivity(), OnOffReceiver.class);
            i2Off.putExtra("sim", "sim2");
            i2Off.putExtra("action", false);
            i2Off.setAction(ALARM_ACTION);
            final int SIM2_OFF = 200;
            PendingIntent pi2Off = PendingIntent.getBroadcast(getActivity(), SIM2_OFF, i2Off, 0);
            if (sharedPreferences.getInt(SIM2, 0) == 1) {
                am.cancel(pi2Off);
                alarmTime = new DateTime().withHourOfDay(Integer.valueOf(sharedPreferences.getString("sim2off_time", "23:55").split(":")[0]))
                        .withMinuteOfHour(Integer.valueOf(sharedPreferences.getString("sim2off_time", "23:55").split(":")[1]))
                        .withSecondOfMinute(0);
                if (alarmTime.getMillis() < System.currentTimeMillis())
                    alarmTime = alarmTime.plusDays(1);
                am.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getMillis(), AlarmManager.INTERVAL_DAY, pi2Off);
            } else
                am.cancel(pi2Off);
        }
        if ((key.equals(SIM2) && sharedPreferences.contains("sim2on_time")) || key.equals("sim2on_time")) {
            Intent i2On = new Intent(getActivity(), OnOffReceiver.class);
            i2On.putExtra("sim", "sim2");
            i2On.putExtra("action", true);
            i2On.setAction(ALARM_ACTION);
            final int SIM2_ON = 201;
            PendingIntent pi2On = PendingIntent.getBroadcast(getActivity(), SIM2_ON, i2On, 0);
            if (sharedPreferences.getInt(SIM2, 0) == 1) {
                am.cancel(pi2On);
                alarmTime = new DateTime().withHourOfDay(Integer.valueOf(sharedPreferences.getString("sim2on_time", "00:05").split(":")[0]))
                        .withMinuteOfHour(Integer.valueOf(sharedPreferences.getString("sim2on_time", "00:05").split(":")[1]))
                        .withSecondOfMinute(0);
                if (alarmTime.getMillis() < System.currentTimeMillis())
                    alarmTime = alarmTime.plusDays(1);
                am.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getMillis(), AlarmManager.INTERVAL_DAY, pi2On);
            } else
                am.cancel(pi2On);
        }
    }

    @Override
    public void OnDialogClosed(String sim, boolean state, int bitSet) {
        String key = "";
        switch (sim) {
            case "sim1":
                if (state)
                    key = "days1on";
                else
                    key = "days1off";
                break;
            case "sim2":
                if (state)
                    key = "days2on";
                else
                    key = "days2off";
                break;
        }
        prefs.edit().putInt(key, bitSet).apply();
    }
}
