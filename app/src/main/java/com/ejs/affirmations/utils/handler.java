package com.ejs.affirmations.utils;

import static com.ejs.affirmations.utils.msg.log;
import static com.ejs.affirmations.utils.msg.logE;
import static com.ejs.affirmations.utils.msg.toast;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.ParseException;

public class  handler {

    /**
     *
     * @param context get context app
     * @param e get Throwable exception
     */
    public static void json(Context context, JSONException e){
        String TAG = "json";
        logE(TAG, e);
        toast(context, "e", e.getMessage());
    }
    /**
     *
     * @param context get context app
     * @param e get Throwable exception
     */
    public static void uRLmal(Context context, MalformedURLException e){
        String TAG = "URLmal";
        logE(TAG, e);
        toast(context, "e", e.getMessage());
    }
    /**
     *
     * @param context get context app
     * @param e get Throwable exception
     */
    public static void volley(Context context, VolleyError e){
        String TAG = "Volley";
        NetworkResponse networkResponse = e.networkResponse;
        String errorMessage = "Unknown error";
        if (networkResponse == null) {
            if (e.getClass().equals(TimeoutError.class)) {
                errorMessage = "Request timeout";
            } else if (e.getClass().equals(NoConnectionError.class)) {
                errorMessage = "Failed to connect server";
            }
            logE(TAG, e);
        } else {
            String result = new String(networkResponse.data);
            try {
                JSONObject response = new JSONObject(result);
                String message = response.getString("message");

                logE(TAG, e);

                if (networkResponse.statusCode == 404) {
                    errorMessage = "Resource not found";
                } else if (networkResponse.statusCode == 401) {
                    errorMessage = message+" Please login again";
                } else if (networkResponse.statusCode == 400) {
                    errorMessage = message+ " Check your inputs";
                } else if (networkResponse.statusCode == 500) {
                    errorMessage = message+" Something is getting wrong";
                }
            } catch (JSONException ei) {
                json(context, ei);
            }
        }
        log("e",TAG, "Error: " + errorMessage);
        toast(context, "e", e.getMessage());
    }
    /**
     *
     * @param context get context app
     * @param e get Throwable exception
     */
    public static void parseEx(Context context, ParseException e){
        String TAG = "ParseEx";
        logE(TAG, e);
        toast(context, "e", e.getMessage());
    }
    /**
     *
     * @param context get context app
     * @param e get Throwable exception
     */
    public static void fileFound(Context context, FileNotFoundException e){
        String TAG = "FileFound";
        logE(TAG, e);
        toast(context, "e", e.getMessage());
    }
    /**
     *
     * @param context get context app
     * @param e get Throwable exception
     */
    public static void iOe(Context context, IOException e){
        String TAG = "IOe";
        logE(TAG, e);
        toast(context, "e", e.getMessage());
    }
    /**
     *
     * @param context get context app
     * @param e get Throwable exception
     */
    public static void exc(Context context, Exception e){
        String TAG = "Exc";
        logE(TAG, e);
        toast(context, "e", e.getMessage());
    }
    /**
     *
     * @param context get context app
     * @param e get Throwable exception
     */
    public static void uEncodingException(Context context, UnsupportedEncodingException e){
        String TAG = "Exc";
        logE(TAG, e);
        toast(context, "e", e.getMessage());
    }
    /**
     *
     * @param context get context app
     * @param e get Throwable exception
     */
    public static void interruptedException(Context context, InterruptedException e){
        String TAG = "Exc";
        logE(TAG, e);
        toast(context, "e", e.getMessage());
    }
}
