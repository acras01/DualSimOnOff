package ua.od.acros.dualsimonoff;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

public class ChooseDaysDialog extends DialogFragment {

    private AppCompatButton bOK;
    private int bitSet;
    private String sim;
    private boolean state;
    private Context mContext;

    public static ChooseDaysDialog newInstance(String sim, boolean state, int bitSet) {
        ChooseDaysDialog f = new ChooseDaysDialog();
        Bundle b = new Bundle();
        b.putInt("bitset", bitSet);
        b.putString("sim", sim);
        b.putBoolean("state", state);
        f.setArguments(b);
        return f;
    }

    public interface ChooseDaysDialogClosedListener {
        void OnDialogClosed(String sim, boolean state, int bitSet);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = CustomApplication.getAppContext();
        sim = getArguments().getString("sim");
        bitSet = getArguments().getInt("bitset");
        state = getArguments().getBoolean("state");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final List<DayItem> dayItems = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            DaysOfWeek days = new DaysOfWeek(bitSet);
            DayItem dayItem = new DayItem(DaysOfWeek.DAYS_BITS.get(i), DaysOfWeek.DAYS_OF_WEEK.get(i), false);
            if (days.getSetDays().contains(i + 1))
                dayItem.setChecked(true);
            dayItems.add(dayItem);
        }
        final CustomListAdapter adapter = new CustomListAdapter(mContext, R.layout.days_list_row, dayItems);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Choose days")
                .setAdapter(adapter, null)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                bOK = (AppCompatButton) dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                bOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ChooseDaysDialogClosedListener listener = (ChooseDaysDialogClosedListener) getActivity();
                        bitSet = 0;
                        for (DayItem dayItem : adapter.getList()) {
                            if (dayItem.isChecked())
                                bitSet += dayItem.index;
                        }
                        listener.OnDialogClosed(sim, state, bitSet);
                        dismiss();
                    }
                });
            }
        });
        return dialog;
    }

    private class DayItem {
        private int index;
        private String name;
        private boolean checked;

        DayItem(int index, String name, boolean checked) {
            this.index = index;
            this.name = name;
            this.checked = checked;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }
    }

    private class CustomListAdapter extends ArrayAdapter<DayItem> {

        private ViewHolder holder;
        private List<DayItem> list;
        private int layout;

        public CustomListAdapter(Context context, int layout, List<DayItem> list) {
            super(context, layout, list);
            this.list = list;
            this.layout = layout;
        }

        private class ViewHolder {
            public AppCompatCheckBox checkBox;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = getActivity().getLayoutInflater().inflate(layout, null);
                convertView.setTag(holder);
                holder.checkBox = (AppCompatCheckBox) convertView.findViewById(R.id.checkBox);
            } else
                holder = (ViewHolder) convertView.getTag();
            holder.checkBox.setEnabled(true);
            holder.checkBox.setTag(list.get(position));
            holder.checkBox.setText(list.get(position).getName());
            holder.checkBox.setChecked(list.get(position).isChecked());
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ((DayItem) buttonView.getTag()).setChecked(isChecked);
                }
            });
            return convertView;
        }

        public List<DayItem> getList() {
            return list;
        }
    }
}
