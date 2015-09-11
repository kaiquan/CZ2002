package sg.ntu.core.util.intent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Class used to when you want to start activity from another activity.
 */
public class LaunchIntent {

    /**
     * This method is used to when you want to start activity (A) without extra
     * data
     *
     * @param context   :context of another activity (B) which start activity (A)
     * @param className :name of class of activity (A) is started
     */
    public static <T> void start(Context context, Class<T> className) {
        start(context, className, Intent.FLAG_ACTIVITY_NEW_TASK, null);
    }

    /**
     * This method is used to when you want to start activity (A) without extra
     * data
     *
     * @param context   :context of another activity (B) which start activity (A)
     * @param className :name of class of activity (A) is started
     * @param flag      :flag of intent
     */
    public static <T> void start(Context context, Class<T> className, int flag) {
        start(context, className, flag, null);
    }

    /**
     * This method is used to when you want to start activity (A) without extra
     * data
     *
     * @param context   :context of another activity (B) which start activity (A)
     * @param className :name of class of activity (A) is started
     * @param bundle    :extra bundle
     */
    public static <T> void start(Context context, Class<T> className,
                                 Bundle bundle) {
        start(context, className, -1, bundle);
    }

    /**
     * This method is used to when you want to start activity (A) without extra
     * data
     *
     * @param context   :context of another activity (B) which start activity (A)
     * @param className :name of class of activity (A) is started
     * @param flag      :flag of intent
     * @param bundle    :extra bundle
     */
    public static <T> void start(Context context, Class<T> className, int flag,
                                 Bundle bundle) {
        Intent iLaunch = new Intent(context, className);

        // set extra
        if (bundle != null) {
            iLaunch.putExtras(bundle);
        }

        // set flag
        if (flag != -1) {
            iLaunch.addFlags(flag);
        }

        context.startActivity(iLaunch);
    }

    /**
     * This method is used to when you want to start activity (A) with extra
     * data
     *
     * @param context   : The context of another activity (B) which start activity (A)
     * @param className : The name of class of activity (A) is started
     * @param name      : The name of extra data
     * @param value     : The object data value
     * @throws NullPointerException
     */
    @Deprecated
    public static <T> void startWithExtra(Context context, Class<T> className,
                                          String name, Object value) throws NullPointerException {
        Intent iLaunch = new Intent(context, className);
        iLaunch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (value instanceof Integer) {
            iLaunch.putExtra(name, (Integer) value);
        } else if (value instanceof String) {
            iLaunch.putExtra(name, value.toString());
        } else if (value instanceof Long) {
            iLaunch.putExtra(name, (Long) value);
        } else if (value instanceof Float) {
            iLaunch.putExtra(name, (Float) value);
        } else if (value instanceof Boolean) {
            iLaunch.putExtra(name, (Boolean) value);
        } else if (value instanceof Serializable) {
            iLaunch.putExtra(name, (Serializable) value);
        }
        context.startActivity(iLaunch);
    }

    /**
     * Method is used to when you want to start activity (A) with array extra
     * data
     *
     * @param context   The context of another activity (B) which start activity (A)
     * @param className The name of class of activity (A) is started
     * @param values    The hash map contain data value
     * @throws NullPointerException
     */
    @Deprecated
    public static <T> void startWithArrayExtra(Context context,
                                               Class<T> className, Map<String, Object> values)
            throws NullPointerException {

        Intent iLaunch = new Intent(context, className);
        iLaunch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Integer) {
                iLaunch.putExtra(key, (Integer) value);
            } else if (value instanceof String) {
                iLaunch.putExtra(key, value.toString());
            } else if (value instanceof Long) {
                iLaunch.putExtra(key, (Long) value);
            } else if (value instanceof Float) {
                iLaunch.putExtra(key, (Float) value);
            } else if (value instanceof Boolean) {
                iLaunch.putExtra(key, (Boolean) value);
            } else if (value instanceof Serializable) {
                iLaunch.putExtra(key, (Serializable) value);
            }
        }
        context.startActivity(iLaunch);
    }

    /**
     * Find launch intent of activity by package name
     *
     * @param activity    The activity is used to get package manager to find launch
     *                    intent
     * @param packageName : The name of the package to inspect.
     * @return Return a "good" intent to launch a front-door activity in a
     * package or null
     */
    public static Intent findIntent(Activity activity, String packageName) {
        if (activity == null || packageName == null || packageName.equals("")) {
            return null;
        }

        PackageManager packageManager = activity.getPackageManager();

        List<ApplicationInfo> packages = packageManager
                .getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo info : packages) {
            String name = info.packageName;
            if (name.equalsIgnoreCase(packageName)) {
                return packageManager.getLaunchIntentForPackage(name);
            }
        }
        return null;
    }

    /**
     * Start activity when known package name of this activity
     *
     * @param activity    activity used to start
     * @param packageName package name of activity is started
     */
    public static void startByPackageName(Activity activity, String packageName) {
        Intent iLaunchApp = findIntent(activity, packageName);
        if (iLaunchApp != null) {
            iLaunchApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(iLaunchApp);
        }
    }
}
