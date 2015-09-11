package sg.ntu.core.util.hardware;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtil {

    /**
     * Use to hide keyboard system
     *
     * @param activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view != null) {
            IBinder binder = view.getWindowToken();
            if (binder != null) {
                inputMethodManager.hideSoftInputFromWindow(binder, 0);
            }
        }

    }

    public static void hideSoftKeyboard(final Dialog dialog) {
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) dialog
                        .getContext().getSystemService(
                                Activity.INPUT_METHOD_SERVICE);
                View view = dialog.getCurrentFocus();
                if (view != null) {
                    IBinder binder = view.getWindowToken();
                    if (binder != null) {
                        inputMethodManager.hideSoftInputFromWindow(binder,
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            }
        });

    }

    /**
     * Clear focus and hide keyboard
     *
     * @param view
     * @param event
     * @param text
     */
    public static void clearFocus(View view, MotionEvent event, View text) {
        if (text.isFocused()) {
            Rect outRect = new Rect();
            text.getGlobalVisibleRect(outRect);
            if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                text.clearFocus();
                InputMethodManager imm = (InputMethodManager) view.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * @param editText
     * @param activity
     */
    public static void showSoftKeyboard(final Activity activity, final View view) {
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                if (view.hasFocus()) {
                    view.clearFocus();
                }
                view.requestFocus();
                InputMethodManager imm = (InputMethodManager) activity
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
            }
        });
    }

    public static void showSoftKeyboard(final Dialog dialog, final View view) {
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                if (view.hasFocus()) {
                    view.clearFocus();
                }
                view.requestFocus();
                InputMethodManager imm = (InputMethodManager) dialog
                        .getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
            }
        });
    }
}
