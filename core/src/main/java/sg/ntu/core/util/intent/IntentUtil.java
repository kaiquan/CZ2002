package sg.ntu.core.util.intent;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.List;

public class IntentUtil {

    public static final int GET_IMAGE_FROM_GALLERY = 1;
    public static final int CAPTURE_IMAGE = 2;
    public static final int CAPTURE_VIDEO = 3;
    public static final int CAPTURE_AUDIO = 4;
    public static final int CROP_IMAGE = 5;

    public static void call(Context context, String phoneNumber) {
        Intent iCall = new Intent(Intent.ACTION_CALL);
        iCall.setData(Uri.parse("tel:" + phoneNumber));
        iCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(iCall);
    }

    public static void dial(Context context, String phoneNumber) {
        Intent iDial = new Intent(Intent.ACTION_DIAL);
        iDial.setData(Uri.parse("tel:" + phoneNumber));
        iDial.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(iDial);
    }

    public static void sendSMS(Context context, String telephoneNumber,
                               String message) {
        Uri smsUri = Uri.parse("tel:" + telephoneNumber);
        Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
        intent.putExtra("sms_body", message);
        intent.setType("vnd.android-dir/mms-sms");
        context.startActivity(intent);
    }

    public static void sendMail(Context context, String[] email, String[] cc,
                                String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, "send"));
    }

    public static void openWifiSetting(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName("com.android.settings",
                "com.android.settings.wifi.WifiSettings");
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static void getImageFromGallery(Activity activity) {
        Intent iGet = new Intent(Intent.ACTION_GET_CONTENT);
        iGet.setType("image/*");
        activity.startActivityForResult(iGet, GET_IMAGE_FROM_GALLERY);
    }

    @SuppressWarnings("deprecation")
    public static String getRealPathFromURI(Activity activity, Uri contentUri) {
        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.managedQuery(contentUri, proj, // Which columns
                // to return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        return path;
    }

    public static void captureImage(Activity activity, String pathOfImage) {
        File fImage = new File(pathOfImage);
        Uri uriImage = Uri.fromFile(fImage);
        Intent iTake = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        iTake.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);
        activity.startActivityForResult(iTake, CAPTURE_IMAGE);
    }

    /**
     * Indicates whether the specified action can be used as an intent. This
     * method queries the package manager for installed packages that can
     * respond to an intent with the specified action. If no suitable package is
     * found, this method returns false.
     *
     * @param context The application's environment.
     * @param action  The Intent action to check for availability.
     * @return True if an Intent with the specified action can be sent and
     * responded to, false otherwise.
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

}
