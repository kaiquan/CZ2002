package sg.ntu.core.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.Date;

public class PreferencesHelper {

    private static PreferencesHelper complexPreferences;
    private static Gson GSON = new Gson();
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private PreferencesHelper(Context context, String namePreferences, int mode) {
        if (namePreferences == null || namePreferences.equals("")) {
            namePreferences = "complex_preferences";
        }
        preferences = context.getSharedPreferences(namePreferences, mode != 0 ? mode : Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static PreferencesHelper getInstance(Context context,
                                                String namePreferences, int mode) {

        if (complexPreferences == null) {
            complexPreferences = new PreferencesHelper(context,
                    namePreferences, mode);
        }

        return complexPreferences;
    }

    public static PreferencesHelper getInstance(Context context, int mode) {
        return getInstance(context, null, mode);
    }

    public static PreferencesHelper getInstance(Context context) {
        return getInstance(context, null, 0);
    }

    public void putObject(String key, Object object) {
        if (object == null) {
            throw new IllegalArgumentException("object is null");
        }

        if (key.equals("") || key == null) {
            throw new IllegalArgumentException("key is empty or null");
        }

        editor.putString(key, GSON.toJson(object)).commit();
    }

    public void increment(String key) {
        if (preferences.contains(key)) {
            putObject(key, getObject(key, Integer.class) + 1);
        } else {
            putObject(key, 1);
        }
    }


    /**
     * Set an int value in the preferences editor
     *
     * @param key   : name of preference to modify
     * @param value : the new value of preference
     * @return : MyPreferenceII to continue edit
     */
    public void putInt(String key, int value) {
        editor.putInt(key, value).commit();
    }

    /**
     * Retrieve an int value from the preferences.
     *
     * @param key          : the name of preference to retrieve
     * @param defaultValue : value to return if this preference does not exist
     * @return : return the preference value if it exist, else return default
     * value
     */
    public int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    /**
     * Set a long value in preference editor
     *
     * @param key   : name of preference to modify
     * @param value : the new value of preference
     * @return : MyPreferenceII to continue edit
     */
    public void putLong(String key, long value) {
        editor.putLong(key, value).commit();
    }

    /**
     * Retrieve an long value from the preferences
     *
     * @param key          : the name of preference to retrieve
     * @param defaultValue : value to return if this preference does not exist
     * @return : return the preference value if exist, else return default value
     */
    public long getLong(String key, long defaultValue) {
        return preferences.getLong(key, defaultValue);
    }

    /**
     * Set a String value in preference editor
     *
     * @param key   : name of preference to modify
     * @param value : the new value of preference
     * @return : return MyPreferenceII to continue edit
     */
    public void putString(String key, String value) {
        editor.putString(key, value).commit();
    }

    /**
     * Retrieve a String value from the preference
     *
     * @param key          : the name of preference to retrieve
     * @param defaultValue : value to return if preference does not exist
     * @return : return the preference value if exist, else return default value
     */
    public String getString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    /**
     * Set a boolean value in preference editor
     *
     * @param key   : name of preference to modify
     * @param value : the new value of preference
     * @return : return MyPreferenceII to continue edit
     */
    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value).commit();
    }

    /**
     * Retrieve a boolean value from the preferences
     *
     * @param key          : the name of preference to retrieve
     * @param defaultValue : value to return if preference does not exist
     * @return : return the preference value if exist, else return default value
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    /**
     * Set a float value in preference editor
     *
     * @param key   : name of preference to modify
     * @param value : the new value of preference
     * @return : return MyPreferenceII to continue edit
     */
    public void putFloat(String key, float value) {
        editor.putFloat(key, value).commit();
    }

    /**
     * Retrieve a float value from the preferences
     *
     * @param key          : the name of preference to retrieve
     * @param defaultValue : value to return if preference dose not exist
     * @return : return the preference value if exist, else return default value
     */
    public float getFloat(String key, float defaultValue) {
        return preferences.getFloat(key, defaultValue);
    }


    public void putDate(String key, Date date) {
        editor.putLong(key, date.getTime()).commit();
    }

    public Date getDate(String key, Date defaultValue) {
        return new Date(preferences.getLong(key, defaultValue.getTime()));
    }


    public void commit() {
        editor.commit();
    }

    public <T> T getObject(String key, Class<T> a) {

        String gson = preferences.getString(key, null);
        if (gson == null) {
            return null;
        } else {
            try {
                return GSON.fromJson(gson, a);
            } catch (Exception e) {
                throw new IllegalArgumentException("Object stored with key " + key + " is instanceof other class");
            }
        }
    }

    public int getInt(String key) {
        return getObject(key, Integer.class);
    }


    public void clear() {
        editor.clear().commit();
    }
}