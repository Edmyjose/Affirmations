package com.ejs.affirmations.activity;

import static com.ejs.affirmations.utils.Utils.*;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.ejs.affirmations.R;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class aboutActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private BillingClient billingClient;
    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedTrans;
    private Button go_playstore, btn_price;
    private TextView txt_detail, txt_product, donacion, txt_info;
    private List<String> productIds = new ArrayList<>();
    private Handler handler;
    private ProductDetails productDetails;
    private AppCompatActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_support);
        context = this;
        UncaughtException(context);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedTrans = getSharedPreferences("trans_" + getLocale(), MODE_PRIVATE);
        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("productName")){
                    findUiHandler().post(() -> {
                        txt_product.setText(sharedTrans.getString("productName", sharedPreferences.getString("productName", "productName")));
                    });
                }
                if (key.equals("productDescription")){
                    findUiHandler().post(() -> {
                        txt_detail.setText(sharedTrans.getString("productDescription", sharedPreferences.getString("productDescription", "productDescription")));
                    });
                }
            }
        };
        sharedTrans.registerOnSharedPreferenceChangeListener( listener);

        txt_info = findViewById(R.id.txt_info);
        donacion = findViewById(R.id.donacion);
        go_playstore = findViewById(R.id.go_playstore);
        btn_price = findViewById(R.id.btn_prices);
        txt_product = findViewById(R.id.txt_product);
        txt_detail = findViewById(R.id.txt_detail);

        productIds.add("donacion");
        //Initialize a BillingClient with PurchasesUpdatedListener onCreate method
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(purchasesUpdatedListener())
                .build();

        //start the connection after initializing the billing client
        establishConnection();
        go_playstore.setText(sharedTrans.getString("go_playstore", getString(R.string.go_playstore)));
        txt_info.setText(sharedTrans.getString("info", getString(R.string.info)));
        donacion.setText(sharedTrans.getString("donacion", getString(R.string.donacion)));

        go_playstore.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://play.google.com/store/apps/dev?id=5312837177487905338");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(
                        Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                );
                startActivity(intent);
                //purchasesResult();
            }
        });
    }


    private void establishConnection(){
        billingClient.startConnection(billingClientStateListener());
    }
    private void showProducts(){
        ImmutableList<QueryProductDetailsParams.Product> productList = ImmutableList.of(
                //Product 1 = index is 0
                QueryProductDetailsParams.Product.newBuilder().setProductId("donacion").setProductType(BillingClient.ProductType.INAPP).build()
        );
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder().setProductList(productList).build();
        ProductDetailsResponseListener productDetailsResponseListener = new ProductDetailsResponseListener() {
            public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> list) {
                // Process the result
                        /*for (ProductDetails productIds : list) {

                            findUiHandler().post(() -> {
                                //here is the code that needs to be executed on the main thread
                                txt_product.setText(productIds.getName());
                                txt_detail.setText(productIds.getDescription());
                                btn_price.setText(productIds.getOneTimePurchaseOfferDetails().getFormattedPrice());
                                btn_price.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.e(TAG, " btn_price onclick ");
                                        showProducts();
                                        launchPurchaseFlow(productDetails);
                                    }
                                });
                            });
                        }*/
                productDetails = list.get(0);

                findUiHandler().post(() -> {
                    //here is the code that needs to be executed on the main thread

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("productName", productDetails.getName());
                    editor.commit(); // commit is important here.
                    editor.putString("productDescription", productDetails.getDescription());
                    editor.commit(); // commit is important here.
                    /*if (sharedTrans.getString("productName","productName").equals("productName")){
                        translateText(sharedTrans, productDetails.getName(),"productName");

                    }
                    if (sharedTrans.getString("productDescription","productDescription").equals("productDescription")){

                        translateText(sharedTrans, productDetails.getDescription(),"productDescription");
                    }*/
                    txt_product.setText(sharedTrans.getString("productName", productDetails.getName()));
                    txt_detail.setText(sharedTrans.getString("productDescription",productDetails.getDescription()));
                    btn_price.setText(productDetails.getOneTimePurchaseOfferDetails().getFormattedPrice());
                    btn_price.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            launchPurchaseFlow(productDetails);
                        }
                    });
                });

            }
        };
        billingClient.queryProductDetailsAsync( params, productDetailsResponseListener );
        /*billingClient.queryProductDetailsAsync(params, (billingResult, list) -> { });*/
    }
    private void launchPurchaseFlow(ProductDetails productDetails){

        BillingFlowParams.ProductDetailsParams productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(productDetails).build();
        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = ImmutableList.of(productDetailsParams);
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParamsList).build();

        BillingResult billingResult = billingClient.launchBillingFlow(this, billingFlowParams);

    }
    private void uiToast(String text){
        findUiHandler().post(() -> {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        });
    }
    private void consumePurchase(Purchase purchase){
        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();
        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                Log.e(TAG, "purchasesResult all consumed");
                uiToast("Thanks for Buying all consumed, Enjoy!");
            }

        };
        billingClient.consumeAsync(consumeParams, listener);

    }
    private void acknowlegePurchase(Purchase purchase){

        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
        AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = billingResult -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                //user prefs to set premium
                uiToast("Thanks for Buying, Enjoy!");
            }
        };
        Log.e(TAG, "Purchase Token: " + purchase.getPurchaseToken());
        Log.e(TAG, "Purchase Time: " + purchase.getPurchaseTime());
        Log.e(TAG, "Purchase OrderID: " + purchase.getOrderId());
        billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
        consumePurchase(purchase); //buy manytime
        //purchasesResult();

    }
    private void purchasesResult(){
        QueryPurchasesParams queryPurchasesParams = QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build();
        BillingResult.newBuilder().build();
        PurchasesResponseListener purchasesResponseListener = new PurchasesResponseListener() {
            public void onQueryPurchasesResponse(
                    @NonNull BillingResult billingResult, @NonNull List<Purchase> purchases) {
                Log.e(TAG, "purchasesResult purchases.size() " + purchases.size());
                // Process the result
                if(purchases.size()>0){
                    for (Purchase sourcePurchase : purchases) {
                        Log.e(TAG,"purchasesResult purchase " + sourcePurchase.getPurchaseToken() + " isAcknowledged " + sourcePurchase.isAcknowledged());
                        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                                .setPurchaseToken(sourcePurchase.getPurchaseToken())
                                .build();
                        ConsumeResponseListener listener = new ConsumeResponseListener() {
                            @Override
                            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                                Log.e(TAG, "purchasesResult all consumed");
                            }

                        };
                        billingClient.consumeAsync(consumeParams, listener);
                    }
                }
            }
        };
        billingClient.queryPurchasesAsync(queryPurchasesParams,purchasesResponseListener );
    }
    private PurchasesUpdatedListener purchasesUpdatedListener(){
        return new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    for (Purchase purchase : list) {
                        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                            Log.e(TAG, "purchasesUpdatedListener isAcknowledged " + purchase.isAcknowledged());
                            //acknowlegePurchase(purchase); //buy onetime
                        }
                    }
                }
            }
        };
    }
    private PurchasesResponseListener purchasesResponseListener(){
        return new PurchasesResponseListener() {
            @Override
            public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    for (Purchase purchase : list) {
                        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                            Log.e(TAG, "purchasesResponseListener isAcknowledged " + purchase.isAcknowledged());
                            acknowlegePurchase(purchase); //buy onetime
                        }
                    }
                }

            }
        };
    }
    private BillingClientStateListener billingClientStateListener(){
        return new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    showProducts();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                establishConnection();
            }
        };
    }
    private QueryPurchasesParams queryPurchasesParams(String productType){
        return QueryPurchasesParams.newBuilder().setProductType(productType).build();
    }
    private Handler findUiHandler() {
        return new Handler(Looper.getMainLooper());
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        billingClient.queryPurchasesAsync(queryPurchasesParams(BillingClient.ProductType.INAPP), purchasesResponseListener());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        billingClient.endConnection();
        finish();
    }
}