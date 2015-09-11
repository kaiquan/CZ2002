package sg.ntu.core.util.hardware;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LocaleUtil {

    public static void change(Context context, String language) {
        // get resource
        Resources resources = context.getResources();
        // get display-metrics
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        // get configuration
        Configuration configuration = resources.getConfiguration();
        // set locale
        configuration.locale = getLocale(context, language);
        // update locale
        resources.updateConfiguration(configuration, displayMetrics);
    }

    public static Locale getLocale(final Context context, final String language) {
        Locale locale;

        String name = null;
        // if name of locale contains '_' in zh_cn or zh_tw we get substring cn
        // or zh to set locale
        if (language != null && language.contains("_")) {
            name = language.substring(3);
        }

        if (name == null) {
            if (language.equals("default")) {
                locale = Locale.getDefault();
            } else {
                locale = new Locale(language);
            }
        } else {
            locale = new Locale(language.substring(0, 2), name);
        }
        return locale;
    }

}
