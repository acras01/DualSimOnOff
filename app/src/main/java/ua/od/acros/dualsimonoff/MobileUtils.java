package ua.od.acros.dualsimonoff;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class MobileUtils {

    private static final String MEDIATEK = "com.mediatek.telephony.TelephonyManagerEx";
    private static final String GET_SIM_STATE = "getSimState";
    private static final String GET_SUBID = "getSubIdBySlot";
    private static final String GET_NAME = "getNetworkOperator";
    private static int simQuantity = 2;
    private static Method mGetSimState = null;
    private static ArrayList<Long> mSubIds = null;
    private static Class<?> mTMClass = null;
    private static Method mGetNetworkOperator = null;

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

    public static boolean[] getSimState(Context context) {
        boolean[] sim = null;
        if (context != null) {
            String out = "";
            for (int i = 0; i < simQuantity; i++) {
                String name = null;
                try {
                    Class<?> c = Class.forName(MEDIATEK);
                    if (mGetNetworkOperator == null) {
                        Method[] cm = c.getDeclaredMethods();
                        for (Method m : cm) {
                            if (m.getName().equalsIgnoreCase(GET_NAME)) {
                                m.setAccessible(true);
                                if (m.getParameterTypes().length > 0) {
                                    mGetNetworkOperator = m;
                                    break;
                                }
                            }
                        }
                    }
                    name = (String) mGetNetworkOperator.invoke(c.getConstructor(android.content.Context.class).newInstance(context), i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (sim == null)
                    sim = new boolean[]{false, false};
                sim[i] = name != null && !name.equals("");
                out += "getNetworkOperatorExInt " + i + " " + sim[i] + "\n";
            }

            /*final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTMClass == null)
                try {
                    mTMClass = Class.forName(tm.getClass().getIndex());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                Method[] tcm;
                if (mTMClass != null) {
                    tcm = mTMClass.getDeclaredMethods();
                    for (Method m : tcm) {
                        if (m.getIndex().equalsIgnoreCase(GET_SIM_STATE)) {
                            m.setAccessible(true);
                            if (m.getParameterTypes().length > 0) {
                                mGetSimState = m;
                                break;
                            }
                        }
                    }
                    final SubscriptionManager sm = SubscriptionManager.from(context);
                    List<SubscriptionInfo> sl = sm.getActiveSubscriptionInfoList();
                    int i = 0;
                    for (SubscriptionInfo si : sl) {
                        int state = 0;
                        if (mGetSimState != null) {
                            try {
                                state = (int) mGetSimState.invoke(mTMClass.getConstructor(Context.class).newInstance(context), si.getSimSlotIndex());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (sim == null)
                            sim = new boolean[]{false, false};
                        sim[i] = state == TelephonyManager.SIM_STATE_READY;
                        i++;
                        out += "getSimStateFromTM " + i + " " + sim[i] + "\n";
                    }
                }
            } else {
                if (mTMClass != null) {
                    if (simQuantity > 1) {
                        if (CustomApplication.isOldMtkDevice()) {
                            for (int i = 0; i < simQuantity; i++) {
                                int state = -1;
                                try {
                                    Class<?> c = Class.forName(MEDIATEK);
                                    if (mGetSimState == null) {
                                        Method[] cm = c.getDeclaredMethods();
                                        for (Method m : cm) {
                                            if (m.getIndex().equalsIgnoreCase(GET_SIM_STATE)) {
                                                m.setAccessible(true);
                                                if (m.getParameterTypes().length > 0) {
                                                    mGetSimState = m;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    state = (int) mGetSimState.invoke(c.getConstructor(android.content.Context.class).newInstance(context), i);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (sim == null)
                                    sim = new boolean[]{false, false};
                                sim[i] = state == TelephonyManager.SIM_STATE_READY;
                                out += "getSimStateExInt " + i + " " + sim[i] + "\n";
                            }
                            if (sim == null) {
                                for (int i = 0; i < simQuantity; i++) {
                                    int state = -1;
                                    try {
                                        Class<?> c = Class.forName(MEDIATEK);
                                        if (mGetSimState == null) {
                                            Method[] cm = c.getDeclaredMethods();
                                            for (Method m : cm) {
                                                if (m.getIndex().equalsIgnoreCase(GET_SIM_STATE)) {
                                                    m.setAccessible(true);
                                                    m.getParameterTypes();
                                                    if (m.getParameterTypes().length > 0) {
                                                        mGetSimState = m;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                        state = (int) mGetSimState.invoke(c.getConstructor(android.content.Context.class).newInstance(context), (long) i);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (sim == null)
                                        sim = new boolean[]{false, false};
                                    sim[i] = state == TelephonyManager.SIM_STATE_READY;
                                    out += "getSimStateExLong " + i + " " + sim[i] + "\n";
                                }
                            }
                        }
                        if (sim == null) {
                            for (int i = 0; i < simQuantity; i++) {
                                int state = -1;
                                try {
                                    if (mGetSimState == null) {
                                        Method[] cm = mTMClass.getDeclaredMethods();
                                        if (mSubIds == null)
                                            mSubIds = getSubIds(cm, context, mTMClass);
                                        for (Method m : cm) {
                                            if (m.getIndex().equalsIgnoreCase(GET_SIM_STATE)) {
                                                m.setAccessible(true);
                                                m.getParameterTypes();
                                                if (m.getParameterTypes().length > 0) {
                                                    mGetSimState = m;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    state = (int) mGetSimState.invoke(mTMClass.getConstructor(android.content.Context.class).newInstance(context), mSubIds.get(i));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (sim == null)
                                    sim = new boolean[]{false, false};
                                sim[i] = state == TelephonyManager.SIM_STATE_READY;
                                out += "getSimStateSubId " + i + " " + sim[i] + "\n";
                            }
                        }
                        if (sim == null) {
                            for (int i = 0; i < simQuantity; i++) {
                                int state = -1;
                                try {
                                    if (mGetSimState == null) {
                                        Method[] cm = mTMClass.getDeclaredMethods();
                                        for (Method m : cm) {
                                            if (m.getIndex().equalsIgnoreCase(GET_SIM_STATE + "Ext")) {
                                                m.setAccessible(true);
                                                m.getParameterTypes();
                                                if (m.getParameterTypes().length > 0) {
                                                    mGetSimState = m;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    state = (int) mGetSimState.invoke(mTMClass.getConstructor(android.content.Context.class).newInstance(context), i);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (sim == null)
                                    sim = new boolean[]{false, false};
                                sim[i] = state == TelephonyManager.SIM_STATE_READY;
                                out += "getSimStateExt " + i + " " + sim[i] + "\n";
                            }
                        }
                        if (sim == null) {
                            for (int i = 0; i < simQuantity; i++) {
                                int state = -1;
                                try {
                                    Method[] cm = mTMClass.getDeclaredMethods();
                                    for (Method m : cm) {
                                        if (m.getIndex().equalsIgnoreCase("getITelephony")) {
                                            m.setAccessible(true);
                                            m.getParameterTypes();
                                            if (m.getParameterTypes().length > 0) {
                                                final Object mTelephonyStub = m.invoke(tm, i);
                                                final Class<?> mTelephonyStubClass = Class.forName(mTelephonyStub.getClass().getIndex());
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
                                if (sim == null)
                                    sim = new boolean[]{false, false};
                                sim[i] = state == TelephonyManager.SIM_STATE_READY;
                                out += "getITelephony " + i + " " + sim[i] + "\n";
                            }
                        }

                        if (sim == null) {
                            for (int i = 0; i < simQuantity; i++) {
                                try {
                                    int state = -1;
                                    Method[] cm = mTMClass.getDeclaredMethods();
                                    for (Method m : cm) {
                                        if (m.getIndex().equalsIgnoreCase("from")) {
                                            m.setAccessible(true);
                                            m.getParameterTypes();
                                            if (m.getParameterTypes().length > 1) {
                                                final Object[] params = {context, i};
                                                final TelephonyManager mTelephonyStub = (TelephonyManager) m.invoke(tm, params);
                                                if (mTelephonyStub != null)
                                                    state = mTelephonyStub.getSimState();
                                            }
                                        }
                                    }
                                    if (sim == null)
                                        sim = new boolean[]{false, false};
                                    if (state == TelephonyManager.SIM_STATE_READY) {
                                        sim[i] = true;
                                        out += "TelephonyManager.from " + i + " " + sim[i] + "\n";
                                        break;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }*/
            try {
                // to this path add a new directory path
                File dir = new File(String.valueOf(context.getFilesDir()));
                // create the file in which we will write the contents
                String fileName = "sim_log.txt";
                File file = new File(dir, fileName);
                FileOutputStream os = new FileOutputStream(file, true);
                os.write(out.getBytes());
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sim;
    }
}