package sg.ntu.core.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import sg.ntu.core.CoreApplication;


public class ToastUtil {

    private static Toast toast;

    private static Toast updatingToast;

    /**
     * Show toast message
     *
     * @param message
     */
    public static void show(String message) {
        if (message != null) {
            Context context = CoreApplication.getInstance();
            if (message.equals(context.getResources().getString(context.getResources().getIdentifier("updating", "string", context.getPackageName())))) {
                View layout = LayoutInflater.from(CoreApplication.getInstance()).inflate(context.getResources().getIdentifier("popup_updating", "layout", context.getPackageName()),
                        null, false);

                updatingToast = new Toast(CoreApplication.getInstance());
                updatingToast.setDuration(Toast.LENGTH_SHORT);
                updatingToast.setView(layout);// setting the view of custom toast layout
                updatingToast.show();
            } else {
                toast = Toast.makeText(CoreApplication.getInstance(), message, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    /**
     * Show toast message following message id
     *
     * @param messageId
     */
    public static void show(int messageId) {
        show(CoreApplication.getInstance().getString(messageId));
    }
}
