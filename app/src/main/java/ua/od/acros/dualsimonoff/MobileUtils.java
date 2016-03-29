package ua.od.acros.dualsimonoff;

import android.content.Context;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MobileUtils {

    private static final String MEDIATEK = "com.mediatek.telephony.TelephonyManagerEx";
    private static final String GET_SIM_STATE = "getSimState";
    private static final String GET_SUBID = "getSubIdBySlot";
    private static int simQuantity = 2;

    private static ArrayList<Long> getSubIds(Method[] methods, Context context, Class<?> telephony) {
        ArrayList<Long> subIds = new ArrayList<>();
        try {
            for (Method m : methods) {
                if (m.getName().equalsIgnoreCase(GET_SUBID)) {
                    m.setAccessible(true);
                    if (m.getParameterTypes().length > 0) {
                        for (int i = 0; i < simQuantity; i++) {
                            try {
                                subIds.add(i, (long) m.invoke(telephony.getConstructor(Context.class).newInstance(context), i));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subIds;
    }

    public static boolean[] getSimState(Context context){
        String out = " ";
        boolean[] sim = new boolean[] {false, false};
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Class<?> tc = null;
        try {
            tc = Class.forName(tm.getClass().getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            Method[] tcm;
            if (tc != null) {
                tcm = tc.getDeclaredMethods();
                Method getSimState = null;
                for (Method m : tcm) {
                    if (m.getName().equalsIgnoreCase(GET_SIM_STATE)) {
                        m.setAccessible(true);
                        if (m.getParameterTypes().length > 0) {
                            getSimState = m;
                            break;
                        }
                    }
                }
                final SubscriptionManager sm = SubscriptionManager.from(context);
                List<SubscriptionInfo> sl = sm.getActiveSubscriptionInfoList();
                int i = 0;
                for (SubscriptionInfo si : sl) {
                    int state = 0;
                    if (getSimState != null) {
                        try {
                            state = (int) getSimState.invoke(tc.getConstructor(Context.class).newInstance(context), si.getSimSlotIndex());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (state == TelephonyManager.SIM_STATE_READY) {
                        sim[i] = true;
                        i++;
                        out = "getSimStateFromTM " + sim[i];
                    }
                }
            }
        } else {
            if (tc != null) {
                if (simQuantity > 1) {
                    if (CustomApplication.isOldMtkDevice()) {
                        for (int i = 0; i < simQuantity; i++) {
                            int state = -1;
                            try {
                                Class<?> c = Class.forName(MEDIATEK);
                                Method[] cm = c.getDeclaredMethods();
                                for (Method m : cm) {
                                    if (m.getName().equalsIgnoreCase(GET_SIM_STATE)) {
                                        m.setAccessible(true);
                                        if (m.getParameterTypes().length > 0) {
                                            state = (int) m.invoke(c.getConstructor(android.content.Context.class).newInstance(context), i);
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (state == TelephonyManager.SIM_STATE_READY) {
                                sim[i] = true;
                                out = "getDataStateExInt " + sim[i];
                            }
                        }
                        if (Arrays.equals(sim, new boolean[] {false, false})) {
                            for (int i = 0; i < simQuantity; i++) {
                                int state = -1;
                                try {
                                    Class<?> c = Class.forName(MEDIATEK);
                                    Method[] cm = c.getDeclaredMethods();
                                    for (Method m : cm) {
                                        if (m.getName().equalsIgnoreCase(GET_SIM_STATE)) {
                                            m.setAccessible(true);
                                            m.getParameterTypes();
                                            if (m.getParameterTypes().length > 0) {
                                                state = (int) m.invoke(c.getConstructor(android.content.Context.class).newInstance(context), (long) i);
                                                break;
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (state == TelephonyManager.SIM_STATE_READY) {
                                    sim[i] = true;
                                    out = "getDataStateExLong " + sim[i];
                                }
                            }
                        }
                    }
                    if (Arrays.equals(sim, new boolean[] {false, false})) {
                        for (int i = 0; i < simQuantity; i++) {
                            int state = -1;
                            try {
                                Method[] cm = tc.getDeclaredMethods();
                                ArrayList<Long>  mSubIds = getSubIds(cm, context, tc);
                                for (Method m : cm) {
                                    if (m.getName().equalsIgnoreCase(GET_SIM_STATE)) {
                                        m.setAccessible(true);
                                        m.getParameterTypes();
                                        if (m.getParameterTypes().length > 0) {
                                            state = (int) m.invoke(tc.getConstructor(android.content.Context.class).newInstance(context), mSubIds.get(i));
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (state == TelephonyManager.SIM_STATE_READY) {
                                sim[i] = true;
                                out = "getDataStateSubId " + sim[i];
                            }
                        }
                    }
                    if (Arrays.equals(sim, new boolean[] {false, false})) {
                        for (int i = 0; i < simQuantity; i++) {
                            int state = -1;
                            try {
                                Method[] cm = tc.getDeclaredMethods();
                                for (Method m : cm) {
                                    if (m.getName().equalsIgnoreCase(GET_SIM_STATE + "Ext")) {
                                        m.setAccessible(true);
                                        m.getParameterTypes();
                                        if (m.getParameterTypes().length > 0) {
                                            state = (int) m.invoke(tc.getConstructor(android.content.Context.class).newInstance(context), i);
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (state == TelephonyManager.SIM_STATE_READY) {
                                sim[i] = true;
                                out = "getDataStateExt " + sim[i];
                            }
                        }
                    }
                    if (Arrays.equals(sim, new boolean[] {false, false})) {
                        for (int i = 0; i < simQuantity; i++) {
                            int state = -1;
                            try {
                                Method[] cm = tc.getDeclaredMethods();
                                for (Method m : cm) {
                                    if (m.getName().equalsIgnoreCase("getITelephony")) {
                                        m.setAccessible(true);
                                        m.getParameterTypes();
                                        if (m.getParameterTypes().length > 0) {
                                            final Object mTelephonyStub = m.invoke(tm, i);
                                            final Class<?> mTelephonyStubClass = Class.forName(mTelephonyStub.getClass().getName());
                                            final Class<?> mClass = mTelephonyStubClass.getDeclaringClass();
                                            Method getState = mClass.getDeclaredMethod(GET_SIM_STATE);
                                            state = (int) getState.invoke(mClass);
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (state == TelephonyManager.SIM_STATE_READY) {
                                sim[i] = true;
                                out = "getITelephony " + sim[i];
                            }
                        }
                    }

                    if (Arrays.equals(sim, new boolean[] {false, false})) {
                        for (int i = 0; i < simQuantity; i++) {
                            try {
                                int state = -1;
                                Method[] cm = tc.getDeclaredMethods();
                                for (Method m : cm) {
                                    if (m.getName().equalsIgnoreCase("from")) {
                                        m.setAccessible(true);
                                        m.getParameterTypes();
                                        if (m.getParameterTypes().length > 1) {
                                            final Object[] params = {context, i};
                                            final TelephonyManager mTelephonyStub = (TelephonyManager) m.invoke(tm, params);
                                            if (mTelephonyStub != null)
                                                state = mTelephonyStub.getDataState();
                                        }
                                    }
                                }
                                if (state == TelephonyManager.SIM_STATE_READY) {
                                    sim[i] = true;
                                    out = "TelephonyManager.from " + sim[i];
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        try {
            // to this path add a new directory path
            File dir = new File(String.valueOf(context.getFilesDir()));
            // create the file in which we will write the contents
            String fileName = "sim_log.txt";
            File file = new File(dir, fileName);
            FileOutputStream os = new FileOutputStream(file);
            os.write(out.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sim;
    }

}