package ua.od.acros.dualsimonoff;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SimFragment extends Fragment implements View.OnClickListener {

    private AppCompatEditText mEditText;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public SimFragment() {
    }

    public static SimFragment newInstance(int sectionNumber) {
        SimFragment fragment = new SimFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sim, container, false);
        mEditText = (AppCompatEditText) view.findViewById(R.id.editText);
        view.findViewById(R.id.button).setOnClickListener(this);
        view.findViewById(R.id.only1).setOnClickListener(this);
        view.findViewById(R.id.only2).setOnClickListener(this);
        view.findViewById(R.id.bothoff).setOnClickListener(this);
        view.findViewById(R.id.bothon).setOnClickListener(this);
        final TextView sim1 = (TextView) view.findViewById(R.id.sim1State);
        final TextView sim2 = (TextView) view.findViewById(R.id.sim2State);
        TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(new PhoneStateListener() {

            @Override
            public void onServiceStateChanged(ServiceState serviceState) {
                super.onServiceStateChanged(serviceState);
                new CountDownTimer(1000, 1000) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        try {
                            boolean[] simState = MobileUtils.getSimState(getActivity().getApplicationContext());
                            sim1.setText(simState[0] ? "On" : "Off");
                            sim2.setText(simState[1] ? "On" : "Off");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        }, PhoneStateListener.LISTEN_SERVICE_STATE);
        return view;
    }

    @Override
    public void onClick(View v) {
        String key = "";
        int input = Integer.valueOf(mEditText.getText().toString());
        switch (v.getId()) {
            case R.id.button:
                Intent localIntent;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Settings.System.putInt(getActivity().getContentResolver(), "dual_sim_mode_setting", input);
                    localIntent = new Intent("android.intent.action.DUAL_SIM_MODE");
                } else {
                    Settings.System.putInt(getActivity().getContentResolver(), "msim_mode_setting", input);
                    localIntent = new Intent("android.intent.action.MSIM_MODE");
                }
                localIntent.putExtra("mode", input);
                getActivity().sendBroadcast(localIntent);
                break;
            case R.id.only1:
                key = "sim1";
                break;
            case R.id.only2:
                key = "sim2";
                break;
            case R.id.bothoff:
                key = "off";
                break;
            case R.id.bothon:
                key = "on";
                break;
        }
        if (!key.equals("")) {
            getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE)
                    .edit()
                    .putInt(key, input)
                    .apply();
        }
    }
}
