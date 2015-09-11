package sg.ntu.core.util.cache;

import android.content.Context;
import android.util.Log;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import sg.ntu.core.BuildConfig;
import sg.ntu.core.CoreApplication;

public class DiskCache {

    public static final String FILE_PROFILE_USER = "file_profile_user";
    public static final String CACHE_PROFILE_USER = "cache_profile_user";
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;
    private static final String TAG = "DiskCache";
    private static DiskCache mInstance;
    private static DiskLruCache mDiskCache;
    private static int DISK_CACHE_SIZE = 100 * 1024 * 1024;// 5MB

    private DiskCache() {
    }

    private DiskCache(Context context, String uniqueName, int diskCacheSize) {
        try {
            final File diskCacheDir = getDiskCacheDir(context, uniqueName);
            mDiskCache = DiskLruCache.open(diskCacheDir, APP_VERSION,
                    VALUE_COUNT, diskCacheSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DiskCache getInstance(Context context, String uniqueName) {
        if (mInstance == null) {
            mInstance = new DiskCache();
        }
        try {
            final File diskCacheDir = getDiskCacheDir(context, uniqueName);
            mDiskCache = DiskLruCache.open(diskCacheDir, APP_VERSION,
                    VALUE_COUNT, DISK_CACHE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mInstance;
    }

    private static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use
        // external cache dir
        // otherwise use internal cache dir
        // final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
        // .getExternalStorageState())
        // || !Utils.isExternalStorageRemovable() ? Utils
        // .getExternalCacheDir(context).getPath() : context.getCacheDir()
        // .getPath();
        final String path = context.getCacheDir().getPath();
        String cachePath = path + File.separator + uniqueName;
        Log.d(TAG, "path: " + cachePath);
        return new File(cachePath);
    }

    private boolean writeStringToFile(String response,
                                      DiskLruCache.Editor editor) {
        OutputStream out = null;
        OutputStreamWriter writer = null;
        try {
            // out = new BufferedOutputStream(editor.newOutputStream(0),
            // Utils.IO_BUFFER_SIZE);
            out = editor.newOutputStream(0);
            writer = new OutputStreamWriter(out);
            writer.write(response);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    public void put(String key, String response) {
        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskCache.edit(key);
            if (editor == null) {
                return;
            }

            if (writeStringToFile(response, editor)) {
                mDiskCache.flush();
                editor.commit();
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "image put on disk cache " + key);
                }
            } else {
                editor.abort();
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "ERROR on: image put on disk cache " + key);
                }
            }
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "ERROR on: image put on disk cache " + key);
            }
            try {
                if (editor != null) {
                    editor.abort();
                }
            } catch (IOException ignored) {
            }
        }
    }

    public String getResponse(String key) {
        String response = null;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskCache.get(key);
            if (snapshot == null) {
                return null;
            }
            final InputStream inputStream = snapshot.getInputStream(0);
            if (inputStream != null) {
                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(
                            inputStream);
                    BufferedReader bufferedReader = new BufferedReader(
                            inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((receiveString = bufferedReader.readLine()) != null) {
                        stringBuilder.append(receiveString);
                    }
                    inputStream.close();
                    response = stringBuilder.toString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, response == null ? "" : "response read from disk " + key);
        }
        return response;
    }

    public boolean containsKey(String key) {
        boolean contained = false;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskCache.get(key);
            contained = snapshot != null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }
        return contained;
    }

    public void clearCache() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "disk cache CLEARED");
        }
        try {
            mDiskCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            final File profile = getDiskCacheDir(CoreApplication.getInstance(),
                    FILE_PROFILE_USER);
            if (profile.exists()) profile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public File getCacheFolder() {
        return mDiskCache.getDirectory();
    }
}
