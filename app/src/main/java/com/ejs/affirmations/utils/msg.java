package com.ejs.affirmations.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class msg {

    /**
     *
     * @param type Type msg
     * @param TAG TAG
     * @param msg message String
     */
    public static void log(String type, String TAG, String msg){
        if (msg != null){
            if (!msg.equals("")){
                if (type.equals("e")) Log.e(TAG, msg);
                if (type.equals("i")) Log.i(TAG, msg);
            }
        }
    }

    /**
     *
     * @param TAG TAG
     * @param msg message String
     */
    public static void logE(String TAG, Throwable msg){
        Log.e(TAG, "Error: ", msg);
    }

    /**
     *
     * @param context get app context
     * @param type Type msg
     * @param msg message String
     */
    public static void toast(Context context, String type, final String msg ){

        if (msg != null){
            if (!msg.equals("")){
                if (type.equals("e")){
                    ((Activity)context).runOnUiThread(() -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show());
                }
                if (type.equals("i")){
                    ((Activity)context).runOnUiThread(() -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show());
                }
            }
        }
    }
}
