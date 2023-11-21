package com.ejs.affirmations.fragment;

import static com.ejs.affirmations.utils.Utils.*;
import static com.ejs.affirmations.utils.handler.*;
import static com.ejs.affirmations.utils.msg.*;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ejs.affirmations.MyWallpaperService;
import com.ejs.affirmations.R;
import com.ejs.affirmations.activity.aboutActivity;

import java.io.IOException;
import java.util.Calendar;

public class Fragment_Preferences extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final String TAG = this.getClass().getSimpleName();
    private final AppCompatActivity appCompatActivity;
    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedTrans;
    private SharedPreferences sharedOnce;
    private LinearLayout ll_cat, ll_bac;
    private final CheckBox[] chb_affirmation = new CheckBox[10];
    private final CheckBox[] chb_bac = new CheckBox[15];
    private boolean expanded = false;
    private boolean expanded_bac = false;
    private String tables = "";

    public Fragment_Preferences(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.getDefaultSharedPreferences(appCompatActivity).registerOnSharedPreferenceChangeListener(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appCompatActivity);
        //sharedPreferences = appCompatActivity.getSharedPreferences(appCompatActivity.getPackageName(), appCompatActivity.MODE_PRIVATE);
        sharedTrans = appCompatActivity.getSharedPreferences("trans_" + getLocale(), Context.MODE_PRIVATE);
        sharedOnce = appCompatActivity.getSharedPreferences("runonce", Context.MODE_PRIVATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment__preferences, container, false);
        ll_cat = view.findViewById(R.id.ll_cat);
        TextView txt_cat = view.findViewById(R.id.select_category);
        //txt_cat.setText(changeName(appCompatActivity,"select_category",sharedTrans));
        txt_cat.setOnClickListener(view18 -> {
            if (expanded){
                expanded = collapse(ll_cat);
            } else {
                expanded = expand(ll_cat);
            }
        });
        ll_bac = view.findViewById(R.id.ll_bac);
        TextView txt_bac = view.findViewById(R.id.select_background);
        txt_bac.setOnClickListener(view17 -> {
            if (expanded_bac){
                expanded_bac = collapse(ll_bac);
            } else {
                expanded_bac = expand(ll_bac);
            }
        });
        //txt_bac.setText(changeName(appCompatActivity,"select_background",sharedTrans));
        expanded = collapse(ll_cat);
        expanded_bac = collapse(ll_bac);

        for (int i = 0; i < 15; i++){
            String chb_name_bac = ("image_"+i);
            String chb_name_gif = ("image_"+i+".gif");
            @SuppressLint("DiscouragedApi") int chbResourceId = this.getResources().getIdentifier(chb_name_bac, "id", appCompatActivity.getPackageName());
            chb_bac[i] = view.findViewById(chbResourceId);
            chb_bac[i].setBackground(loadDrawableFromAssets(appCompatActivity, chb_name_gif, chb_bac[i].getWidth(), chb_bac[i].getHeight()));

            chb_bac[i].setOnCheckedChangeListener((buttonView, isChecked) -> savePreferBool(sharedPreferences,chb_name_bac, isChecked));
            chb_bac[i].setChecked(sharedPreferences.getBoolean(chb_name_bac, false));
        }
        for (int i = 0; i < 10; i++){
            String chb_name = ("affirmation_"+i);
            @SuppressLint("DiscouragedApi") int chbResourceId = this.getResources().getIdentifier(chb_name, "id", appCompatActivity.getPackageName());
            chb_affirmation[i] = view.findViewById(chbResourceId) ;
            //chb_affirmation[i].setText(changeName(appCompatActivity,chb_name,sharedTrans))
            chb_affirmation[i].setOnCheckedChangeListener((buttonView, isChecked) -> savePreferBool(sharedPreferences,chb_name, isChecked));
            chb_affirmation[i].setChecked(sharedPreferences.getBoolean(chb_name, false));
        }

        TextView change_quote = view.findViewById(R.id.change_quote);
        //change_quote.setText(changeName(appCompatActivity,"change_quote",sharedTrans));
        change_quote.setOnClickListener(view1 -> {
            tables = checkCat(sharedPreferences);
            if (!tables.equals("")){
                Calendar c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_WEEK); // Or other Calendar value
                //int lastCheckedDay = sharedOnce.getInt("DAY_START", -1); // "KEY" you may change yhe value
                changeQuotte(appCompatActivity,sharedOnce,sharedTrans, tables,day);
            } else {
                findUiHandler().post(() -> {
                    expanded = expand(ll_cat);
                    toast(appCompatActivity, "i",getString(R.string.select_category));
                });
            }
        });
        TextView set_wallpaper = view.findViewById(R.id.set_wallpaper);
        //set_wallpaper.setText(changeName(appCompatActivity,"set_wallpaper",sharedTrans));
        set_wallpaper.setOnClickListener(view12 -> {
            tables = checkCat(sharedPreferences);
            if (!tables.equals("")){
                Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName( appCompatActivity, MyWallpaperService.class));
                startActivity(intent);
            } else {
                findUiHandler().post(() -> {
                    expanded = expand(ll_cat);
                    toast(appCompatActivity, "i",getString(R.string.select_category));
                });
            }
        });
        TextView about = view.findViewById(R.id.about);
        //about.setText(changeName(appCompatActivity,"about",sharedTrans));
        about.setOnClickListener(view13 -> {
            Intent intent = new Intent(appCompatActivity, aboutActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        TextView share = view.findViewById(R.id.share);
        //share.setText(changeName(appCompatActivity,"share",sharedTrans));
        share.setOnClickListener(view14 -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Application Link: https://play.google.com/store/apps/details?id=" + appCompatActivity.getPackageName();
            String shareSub = "App link";
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share App Link Via :"));
        });
        TextView change_bac = view.findViewById(R.id.change_bac);
        //change_bac.setText(changeName(appCompatActivity,"change_bac",sharedTrans));
        change_bac.setOnClickListener(view15 -> {
            try {
                changeGif(sharedPreferences);
            } catch (IOException e) {
                iOe(appCompatActivity, e);
            }
        });
        TextView meditacion = view.findViewById(R.id.meditacion);
        //meditacion.setText(changeName(appCompatActivity,"meditacion",sharedTrans));
        meditacion.setOnClickListener(view16 -> openfragment(appCompatActivity, new fragment_meditacion(appCompatActivity), R.id.settings, getString(R.string.frag_meditacion)));
        return view;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        log( "e",TAG, "sharedPreferences " + s);

    }
    private Handler findUiHandler() {
        return new Handler(Looper.getMainLooper());
    }
}