package ua.od.acros.dualsimonoff;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;

import com.stericson.RootShell.RootShell;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ReportsCrashes(mailTo = "acras1@gmail.com",
        customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.SETTINGS_GLOBAL, ReportField.SETTINGS_SYSTEM,
                ReportField.STACK_TRACE, ReportField.LOGCAT, ReportField.SHARED_PREFERENCES },
        logcatArguments = { "-t", "300", "MyAppTag:V", "System.err:V", "AndroidRuntime:V", "*:S" },
        mode = ReportingInteractionMode.DIALOG,
        resDialogText = R.string.crash_toast_text,
        resDialogOkToast = R.string.crash_toast_text_ok)
public class CustomApplication extends Application {

    private static Context mContext;
    private static Boolean mIsOldMtkDevice = null;
    private static Boolean mHasRoot = null;
    private static boolean mIsActivityVisible;

    @Override
    public void onCreate() {
        super.onCreate();
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        mContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return  mContext;
    }

    public static boolean isActivityVisible() {
        return mIsActivityVisible;
    }

    public static void activityResumed() {
        mIsActivityVisible = true;
    }

    public static void activityPaused() {
        mIsActivityVisible = false;
    }

    public static boolean isScreenOn() {
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1)
            return pm.isInteractive();
        else
            return pm.isScreenOn();
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass) {
        try {
            ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName()))
                    return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static boolean isPackageExisted(String targetPackage){
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    // Supported MTK devices
    private static final Set<String> OLD_MTK_DEVICES = new HashSet<>(Arrays.asList(
            new String[]{
                    // Single-core SoC
                    "mt6575",
                    // Dual-core SoC
                    "mt6572",
                    "mt6577",
                    "mt8377",
                    // Quad-core SoC
                    "mt6582",
                    "mt6582m",
                    "mt6589",
                    "mt8389",
                    // Octa-core SoC
                    "mt6592"
            }
    ));

    public static boolean isOldMtkDevice() {
        if (mIsOldMtkDevice != null)
            return mIsOldMtkDevice;
        mIsOldMtkDevice = OLD_MTK_DEVICES.contains(Build.HARDWARE.toLowerCase());
        return mIsOldMtkDevice;
    }

    public static boolean hasRoot() {
        if (mHasRoot != null)
            return mHasRoot;
        mHasRoot = RootShell.isRootAvailable() && RootShell.isAccessGiven();
        return mHasRoot;
    }
}
