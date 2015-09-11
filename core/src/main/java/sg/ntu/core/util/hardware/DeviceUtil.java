package sg.ntu.core.util.hardware;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;


/**
 * Get information of device
 */
public final class DeviceUtil {

    /**
     * check device is tablet
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    /**
     * check device is handler normal or small screen
     *
     * @param context
     * @return
     */
    public static boolean isNormalOrSmall(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL);
        return (xlarge || large);
    }

    /**
     * get device information
     *
     * @param activity
     * @return
     */
    @SuppressWarnings("deprecation")
    public static Device getDeviceInfo(Activity activity) {
        /* First, get the Display from the WindowManager */
        Display display = ((WindowManager) activity
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		/* Now we can retrieve all display-related infos */
        int width = display.getWidth();
        int height = display.getHeight();
        int orientation = getOrientation(activity);
        Device device = new Device();
        device.mWidth = width;
        device.mHeight = height;
        device.mOrientation = orientation;
        device.mIsTablet = isTablet(activity);
        return device;
    }

    /**
     * get orientation of activity
     *
     * @param activity
     * @return ORIENTATION_LANDSCAPE or ORIENTATION_PORTRAIT
     */
    public static int getOrientation(Activity activity) {
        Configuration config = activity.getResources().getConfiguration();
        return config.orientation;
    }

    /**
     * @return serial number device
     */
    public static String getSerialNumber(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String imei = telephonyManager.getDeviceId();
            return imei;
        } catch (Exception e) {
            String serialNum;
            try {
                Class<?> classLoader = Class
                        .forName("android.os.SystemProperties");
                Method method = classLoader.getMethod("get", String.class,
                        String.class);
                serialNum = (String) (method.invoke(classLoader, "ro.serialno"));
            } catch (Exception exception) {
                serialNum = "W80082PE8YA";
            }
            return serialNum;
        }
    }

    /**
     * The name of the industrial design.
     *
     * @return
     */
    public static String getDeviceName() {
        return Build.DEVICE;
    }

    /**
     * get model & product of device
     *
     * @return
     */
    public static String getModelAndProduct() {
        return Build.MODEL + " (" + Build.PRODUCT + ")";
    }

    public static String getNameModelAndProduct() {
        return Build.DEVICE + " " + Build.MODEL + " (" + Build.PRODUCT + ")";
    }

    /**
     * Get device ID of each device
     *
     * @param context
     * @return number device id
     */
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
}
