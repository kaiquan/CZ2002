package sg.ntu.core.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import sg.ntu.core.CoreApplication;

public class DialogUtils {

    private static Dialog mDialog;

    public static void showProgressLoading() {
        try {
            Context context = CoreApplication.getInstance();
            mDialog = new Dialog(CoreApplication.getInstance(), context.getResources().getIdentifier("LoadingDialogTheme", "style", context.getPackageName()));
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(false);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(context.getResources().getIdentifier("dialog_loading", "layout", context.getPackageName()));
            mDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideProgressLoading() {
        try {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
                mDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
