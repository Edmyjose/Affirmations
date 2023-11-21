package com.ejs.affirmations.utils;

import static com.ejs.affirmations.utils.msg.*;
import static com.ejs.affirmations.utils.handler.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.preference.Preference;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Response;
import com.ejs.affirmations.SettingsActivity;
import com.ejs.affirmations.adapter.adapter_meditacion;
import com.ejs.affirmations.db.DbHelper;
import com.ejs.affirmations.model.categoriaMeditacionClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class Utils {
    private final String TAG = this.getClass().getSimpleName();

    /**
     * open fragment
     * @param frag fragment
     * @param layout layout container
     */
    public static void openfragment(AppCompatActivity mCompat, Fragment frag, int layout, String fragmentTag){
        FragmentManager fragmentManager = mCompat.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(layout, frag, fragmentTag);
        //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.addToBackStack(fragmentTag);
        fragmentTransaction.commit();
    }

    /**
     *
     * @param preferences
     * @param name
     * @param value
     */
    public static void savePreferString(SharedPreferences preferences, String name, String value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(name, value);
        editor.apply();
    }

    /**
     *
     * @param preferences
     * @param name
     * @param value
     */
    public static void savePreferLong(SharedPreferences preferences, String name, long value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(name, value);
        editor.apply();
    }

    /**
     *
     * @param preferences
     * @param name
     * @param value
     */
    public static void savePreferInt(SharedPreferences preferences, String name, int value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(name, value);
        editor.apply();
    }

    /**
     *
     * @param preferences
     * @param name
     * @param value
     */
    public static void savePreferBool(SharedPreferences preferences, String name, boolean value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(name, value);
        editor.apply();
    }

    /**
     *
     * @param v
     * @return
     */
    public static boolean collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
        return false;
    }

    /**
     *
     * @param v
     * @return
     */
    public static boolean expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
        return true;
    }

    /**
     *
     * @param sharedPref
     * @return
     */
    public static String checkCat(SharedPreferences sharedPref){
        String tables = "";
        int tb = -1;
        String[] Arraytables = new String[10];
        if (sharedPref.getBoolean("affirmation_0", false)){
            Arraytables[0] = "louisehay";
        } else {
            tb=0;
        }
        if (sharedPref.getBoolean("affirmation_1", false)){
            Arraytables[1] = "dormir";
        } else if (tb==0) {
            tb=1;
        }
        if (sharedPref.getBoolean("affirmation_2", false)){
            Arraytables[2] = "manana";
        } else if (tb==1) {
            tb=2;
        }
        if (sharedPref.getBoolean("affirmation_3", false)){
            Arraytables[3] = "riqueza";
        } else if (tb==2) {
            tb=3;
        }
        if (sharedPref.getBoolean("affirmation_4", false)){
            Arraytables[4] = "amorpropio";
        } else if (tb==3) {
            tb=4;
        }
        if (sharedPref.getBoolean("affirmation_5", false)){
            Arraytables[5] = "salud";
        } else if (tb==4) {
            tb=5;
        }
        if (sharedPref.getBoolean("affirmation_6", false)){
            Arraytables[6] = "mente";
        } else if (tb==5) {
            tb=6;
        }
        if (sharedPref.getBoolean("affirmation_7", false)){
            Arraytables[7] = "amor";
        } else if (tb==6) {
            tb=7;
        }
        if (sharedPref.getBoolean("affirmation_8", false)){
            Arraytables[8] = "negocio";
        } else if (tb==7) {
            tb=8;
        }
        if (sharedPref.getBoolean("affirmation_9", false)){
            Arraytables[9] = "atencion";
        } else if (tb==8) {
            tb=9;
        }
        if (tb < 9) {
            int random = new Random().nextInt(10);
            tables = Arraytables[random];
            while (tables == null) {
                random = new Random().nextInt(10);
                tables = Arraytables[random];
            }
        }
        return tables;
    }

    /**
     *
     * @param shared shared
     * @throws IOException IOException
     */
    public static void changeGif(SharedPreferences shared ) throws IOException {

        ArrayList<String> gifName = new ArrayList<>();
        for (int i = 0; i < 15; i++){
            String chb_name_bac = ("image_"+i);
            String chb_name_gif = ("image_"+i+".gif");
            if (shared.getBoolean(chb_name_bac, false)){
                gifName.add(chb_name_gif);
            }
        }

        int randoms =  new Random().nextInt(gifName.size());
        String nameGif =gifName.get(randoms).toString();

        SharedPreferences.Editor editor = shared.edit();
        editor.putString("nameGif", nameGif);
        editor.apply(); // commit is important here.
    }

    /**
     *
     * @param context app context
     * @param shared shared
     * @param sp sp
     * @param tables tables
     * @param day day
     */
    public static void changeQuotte(Context context, SharedPreferences shared, SharedPreferences sp, String tables, int day){
        SharedPreferences.Editor editor = shared.edit();
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String affirmation = dbHelper.getRandomAffirmations(tables, db);
        editor.putString("DAY_AFFIRMATION", affirmation);
        editor.apply(); // commit is important here.
        editor.putInt("DAY_START", day);
        editor.apply(); // commit is important here.
        //return affirmation;
        translateText(context, sp, affirmation,"DAY_AFFIRMATION");

    }

    /**
     *
     * @param context Context
     * @param sharedOnce sharedOnce
     * @param text text
     * @param value value
     */
    public static void translateText(Context context, SharedPreferences sharedOnce, String text, String value){

        if (!getLocale().equals("ES")){
            try {
                /*String query = URLEncoder.encode(text, "utf-8");
                query = URLEncoder.encode(text.replaceAll("%0A",""), "utf-8");*/
                String output = URLEncoder.encode(text, "UTF-8");
                String query =  output.replaceAll("%0A","");
                String langpair = "ES|"+ getLocale();
                String url = "https://mymemory.translated.net/api/get?q=" + query + "&langpair=" + langpair;

                Response.Listener<String> res = response -> {
                    JSONObject responses = null;
                    try {
                        responses = new JSONObject(response);
                        String translated = responses.getJSONObject("responseData").getString("translatedText");

                        if (!translated.equals("null")){
                            SharedPreferences.Editor editor = sharedOnce.edit();
                            editor.putString(value, translated);
                            editor.apply(); // commit is important here.
                            editor.putBoolean("TRANSLATED", true);
                            editor.apply(); // commit is important here.
                        }

                    } catch (JSONException e) {
                        json(context, e);
                    }

                };
                HashMap<String, String> hashMap = new HashMap<>();
                volley.run(context, url, res, volley.error(context, null), hashMap);
            } catch (UnsupportedEncodingException e) {
                uEncodingException(context,e);
            }
        }
    }

    /**
     *
     * @param context app context
     * @param name name
     * @param sharedTrans sharedTrans
     * @return String
     */
    public static String changeName(Context context, String name, SharedPreferences sharedTrans){
        int identifier = context.getResources().getIdentifier(name, "string", context.getPackageName());
        String string = sharedTrans.getString(name,context.getString(identifier)) ;
        return string;
    }

    /**
     *
     * @return String
     */
    public static String getLocale(){
        return Locale.getDefault().getLanguage().toUpperCase();
    }

    /**
     *
     * @param context app context
     * @param language language
     */
    public static void setLanguageForApp(Context context, String language){

        Locale locale;
        if(language.equals("not-set")){
            locale = Locale.getDefault();
        }
        else {
            locale = new Locale(language);
        }
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    /**
     *
     * @param context app context
     * @param path drawable path
     * @param gW width
     * @param gH height
     * @return
     */
    public static Drawable loadDrawableFromAssets(Context context, String path, int gW, int gH) {
        InputStream stream = null;
        Resources res = context.getResources();
        AssetManager amanager = res.getAssets();
        try
        {
            InputStream imageStream = amanager.open(path);
            Drawable drawable = new BitmapDrawable(res, imageStream);

            /*stream = context.getAssets().open(path);
            Drawable drawable = Drawable.createFromStream(stream, null);*/
            drawable.setBounds(0,0, gW, gH);
            return drawable;
        }
        catch (Exception e) {
            exc(context,e);
        }
        finally {
            try {
                if(stream != null) {
                    stream.close();
                }
            } catch (Exception e) {
                exc(context,e);
            }
        }
        return null;
    }

    /**
     *
     * @param context app context
     */
    public static void UncaughtException(Context context){

        Thread.setDefaultUncaughtExceptionHandler((paramThread, paramThrowable) -> {
            new Thread(() -> {
                Looper.prepare();
                toast(context, "i",paramThrowable.getMessage());
                Looper.loop();
            }).start();
            try {
                Thread.sleep(4000); // Let the Toast display before app will get shutdown
            }
            catch (InterruptedException e) {
                interruptedException(context,e);
                    /*Utils.savePreferBool(getApplicationContext(),"InterruptedException",true);
                    Utils.savePreferString(getApplicationContext(),"InterruptedExceptionMsg",e.getMessage());  */
            }
            System.exit(2);
        });
    }
}
