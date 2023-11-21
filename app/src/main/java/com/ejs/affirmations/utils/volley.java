package com.ejs.affirmations.utils;


import static com.ejs.affirmations.utils.handler.*;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class volley {

    /**
     *
     * @param mCompat get app context
     * @param URLstring url server
     * @param res response listener
     * @param error error listener
     * @param hashMap hashmap
     */
    public static void run(Context mCompat, String URLstring, Response.Listener<String> res, Response.ErrorListener error, HashMap<String, String> hashMap){
        int socketTimeout = 10000; //DefaultRetryPolicy.DEFAULT_TIMEOUT_MS;
        int defaultMaxRetries = 3; //DefaultRetryPolicy.DEFAULT_MAX_RETRIES;
        float defaultBackOffMult = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLstring, res, error){
            @Override
            protected Map<String, String> getParams() {
                return hashMap;
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, defaultMaxRetries, defaultBackOffMult);
        stringRequest.setRetryPolicy(policy);
        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(mCompat);
        requestQueue.add(stringRequest);
    }

    /**
     *
     * @param mCompat app context
     * @return return error listener
     */
    public static Response.ErrorListener error(Context mCompat, ProgressAlert progressAlert){
        return error -> {
            volley(mCompat, error);
            if (progressAlert != null) progressAlert.dismiss();
        };
    }
}
