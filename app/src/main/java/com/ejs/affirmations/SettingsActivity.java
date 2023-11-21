package com.ejs.affirmations;

import static com.ejs.affirmations.utils.handler.iOe;
import static com.ejs.affirmations.utils.msg.log;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryPurchasesParams;
import com.ejs.affirmations.db.DbHelper;
import com.ejs.affirmations.fragment.Fragment_Preferences;
import com.ejs.affirmations.fragment.RateItDialogFragment;

import java.io.IOException;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener  {
    private final String TAG = this.getClass().getSimpleName();
    private AppCompatActivity appCompatActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        appCompatActivity = this;

        //UncaughtException(getApplicationContext());
        RateItDialogFragment.show(getApplicationContext(), getFragmentManager());
        //PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            /*int insertCount = */DbHelper.insertFromFile(this, R.raw.afirmaciones1, db);
        } catch (IOException e) {
            iOe(this, e);
        }


        checkSubscription();
        checkBuy();
        /*
        if (!sharedTrans.getBoolean("TRANSLATED", false)){
            translateText(sharedTrans, getResources().getString(R.string.select_background),"select_category");
            translateText(sharedTrans, getResources().getString(R.string.select_category),"select_category");
            translateText(sharedTrans, getResources().getString(R.string.set_wallpaper),"set_wallpaper");
            translateText(sharedTrans, getResources().getString(R.string.affirmation_0),"affirmation_0");
            translateText(sharedTrans, getResources().getString(R.string.affirmation_1),"affirmation_1");
            translateText(sharedTrans, getResources().getString(R.string.affirmation_2),"affirmation_2");
            translateText(sharedTrans, getResources().getString(R.string.affirmation_3),"affirmation_3");
            translateText(sharedTrans, getResources().getString(R.string.affirmation_4),"affirmation_4");
            translateText(sharedTrans, getResources().getString(R.string.affirmation_5),"affirmation_5");
            translateText(sharedTrans, getResources().getString(R.string.affirmation_6),"affirmation_6");
            translateText(sharedTrans, getResources().getString(R.string.affirmation_7),"affirmation_7");
            translateText(sharedTrans, getResources().getString(R.string.affirmation_8),"affirmation_8");
            translateText(sharedTrans, getResources().getString(R.string.affirmation_9),"affirmation_9");
            translateText(sharedTrans, getResources().getString(R.string.about),"about");
            translateText(sharedTrans, getResources().getString(R.string.info),"info");
            translateText(sharedTrans, getResources().getString(R.string.donacion),"donacion");
            translateText(sharedTrans, getResources().getString(R.string.go_playstore),"go_playstore");
            translateText(sharedTrans, getResources().getString(R.string.share),"share");
            translateText(sharedTrans, getResources().getString(R.string.change_quote),"change_quote");
            translateText(sharedTrans, getResources().getString(R.string.change_bac),"change_bac");
            translateText(sharedTrans, getResources().getString(R.string.meditacion),"meditacion");
        }*/

        FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
        Fragment_Preferences fragment_preferences = new Fragment_Preferences(appCompatActivity);
        fragmentManager.beginTransaction().replace(R.id.settings, fragment_preferences, TAG).commit();
        /*preferenceFragment = new MyPreferenceFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, preferenceFragment).commit();*/

    }

    private BillingClient billingClient;
    private PurchasesUpdatedListener purchasesUpdatedListenerEmpty(){
        return (billingResult, list) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                assert list != null;
                for (Purchase purchase : list) {
                    if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                        log("e", TAG, "purchasesResponseListenerEmpty isAcknowledged " + purchase.isAcknowledged());
                        //consumePurchase(purchase);
                        //acknowlegePurchase(purchase);
                    }
                }
            }

        };
    }
    private QueryPurchasesParams queryPurchasesParams(String productType){
        return QueryPurchasesParams.newBuilder().setProductType(productType).build();
    }
    private void checkSubscription(){
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(purchasesUpdatedListenerEmpty()).build();
        final BillingClient finalBillingClient = billingClient;

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {

            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    QueryPurchasesParams queryPurchasesParams = queryPurchasesParams(BillingClient.ProductType.SUBS);
                    PurchasesResponseListener purchasesResponseListener = (billingResult1, list) -> {
                        if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK){
                            if(list.size()>0){ //prefs.setPremium(1); // set 1 to activate premium feature
                                int i = 0;
                                for (Purchase purchase: list){
                                    //Here you can manage each product, if you have multiple subscription
                                    log("e", TAG,"checkSubscription " + purchase.getOriginalJson()); // Get to see the order information
                                    log("e", TAG,"checkSubscription " + " index " + i);
                                    i++;
                                }
                            }

                        }
                    };
                    finalBillingClient.queryPurchasesAsync(queryPurchasesParams, purchasesResponseListener);
                }
            }
        });

    }
    private void checkBuy(){
        billingClient = BillingClient.newBuilder(appCompatActivity).enablePendingPurchases().setListener(purchasesUpdatedListenerEmpty()).build();
        final BillingClient finalBillingClient = billingClient;

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {

            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    QueryPurchasesParams queryPurchasesParams = queryPurchasesParams(BillingClient.ProductType.INAPP);
                    PurchasesResponseListener purchasesResponseListener = (billingResult1, list) -> {
                        if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK){
                            if(list.size()>0){
                                //prefs.setPremium(1); // set 1 to activate premium feature
                                int i = 0;
                                for (Purchase purchase: list){
                                    //Here you can manage each product, if you have multiple subscription
                                    log("e", TAG,"checkBuy " + purchase.getOriginalJson()); // Get to see the order information
                                    log("e", TAG, "checkBuy index " + i);
                                    i++;
                                }
                            }else {
                                //prefs.setPremium(0); // set 0 to de-activate premium feature
                                log("e", TAG, "set 0 to de-activate premium feature");
                            }
                        }
                    };
                    finalBillingClient.queryPurchasesAsync(queryPurchasesParams, purchasesResponseListener);
                }
            }
        });

    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
}
