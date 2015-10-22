package sg.ntu.cz2002.controller;

import org.json.JSONObject;

/**
 * Created by Moistyburger on 22/10/15.
 */
public interface Callback<T> {
        /**
         * Successful HTTP response.
         */
        void success(T t, JSONObject response);
        /**
         * Unsuccessful HTTP response due to network failure, non-2XX status code, or unexpected
         * exception.
         */
        void failure(String error);
    }

