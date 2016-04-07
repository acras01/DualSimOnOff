package ua.od.acros.dualsimonoff;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TimePicker;

public class TimePickerDialog extends DialogFragment {

    public static TimePickerDialog newInstance(String sim, boolean action) {
        TimePickerDialog fragment = new TimePickerDialog();
        Bundle bundle = new Bundle(2);
        bundle.putString("sim", sim);
        bundle.putBoolean("action", action);
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String sim = getArguments().getString("sim");
        boolean action = getArguments().getBoolean("action");
        final String[] pref = new String[1];
        final SharedPreferences prefs = getActivity().getSharedPreferences("ua.od.acros.dualsimonoff_preferences", Context.MODE_PRIVATE);
        if (sim != null)
            switch (sim) {
                case "sim1":
                    if (action)
                        pref[0] = "sim1on_time";
                    else
                        pref[0] = "sim1off_time";
                    break;
                case "sim2":
                    if (action)
                        pref[0] = "sim2on_time";
                    else
                        pref[0] = "sim2off_time";
                    break;
            }
        View view = View.inflate(getActivity(), R.layout.timepicker, null);
        final TimePicker tp = (TimePicker) view.findViewById(R.id.timePicker);
        if (!DateFormat.is24HourFormat(getContext()))
            tp.setIs24HourView(false);
        else
            tp.setIs24HourView(true);
        String time = prefs.getString(pref[0], "00:00");
        int[] timeArr = new int[] {Integer.valueOf(time.split(":")[0]), Integer.valueOf(time.split(":")[1])};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tp.setHour(timeArr[0]);
            tp.setMinute(timeArr[1]);
        } else {
            tp.setCurrentHour(timeArr[0]);
            tp.setCurrentMinute(timeArr[1]);
        }

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Set time")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String time;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            time = String.format("%02d", tp.getHour()) + ":" + String.format("%02d", tp.getMinute());
                        else
                            time = String.format("%02d", tp.getCurrentHour()) + ":" + String.format("%02d", tp.getCurrentMinute());
                        prefs.edit()
                                .putString(pref[0], time)
                                .apply();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .create();
    }
}
