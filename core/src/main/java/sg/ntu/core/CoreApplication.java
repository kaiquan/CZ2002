package sg.ntu.core;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import sg.ntu.core.util.PreferencesHelper;


public class CoreApplication extends Application {

    protected static CoreApplication instance;
    PreferencesHelper preferences;

    public static CoreApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        preferences = PreferencesHelper.getInstance(this, null, Context.MODE_PRIVATE);
        Core.context = this;

        if (isFirstLaunch()) {
            Toast.makeText(this, "Welcome to " + this.getResources().getString(this.getResources().getIdentifier("app_name", "string", this.getPackageName())), Toast.LENGTH_LONG).show();
        }

        preferences.increment("APP_LAUNCH_COUNT");
        preferences.commit();
    }

    public boolean isFirstLaunch() {

        if (!preferences.getBoolean("APP_LAUNCHED_BEFORE", false)) {
            preferences.putBoolean("APP_LAUNCHED_BEFORE", true);
            preferences.commit();
            return true;
        }

        return false;
    }

    public int getLaunchCount() {
        return preferences.getInt("APP_LAUNCH_COUNT");
    }


}