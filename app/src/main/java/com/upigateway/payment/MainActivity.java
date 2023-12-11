package com.upigateway.payment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    WebView mWebView;
    Context context;
    String TAG = "MainActivity";
    String paymentUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        context = this;
        mWebView = (WebView) findViewById(R.id.payment_webview);

        createPaymentOrder();

        initWebView();

        // 👍 Call the Create Order API from your server and you will get the Payment URL.
        //    you will also get UPI intent if you are using Enterprise Plan.
        //    you can use upi intent in payment url and it will directly ask for UPI App.
        // 🚫 Do not Call UPIGateway API in Android App Directly
//        String PAYMENT_URL = "https://merchant.upigateway.com/gateway/pay/xxxxxxxxxxxxxxxx";
//        String PAYMENT_URL = "upi://pay?pa=...";

//        if (PAYMENT_URL.startsWith("upi:")) {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
////            intent.setData(Uri.parse(PAYMENT_URL));
//            intent.setData(Uri.parse(paymentUrl));
//            startActivity(intent);
//        } else {
////            mWebView.loadUrl(PAYMENT_URL);
//            mWebView.loadUrl(paymentUrl);
//        }
    }


    @SuppressLint({"SetJavaScriptEnabled"})
    private void initWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setSupportMultipleWindows(true);
        // Do not change Useragent otherwise it will not work. even if not working uncommit below
        // mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.4.4; One Build/KTU84L.H4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.135 Mobile Safari/537.36");
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.addJavascriptInterface(new WebviewInterface(), "Interface");
    }

    public class WebviewInterface {
        @JavascriptInterface
        public void paymentResponse(String client_txn_id, String txn_id) {
            Log.i(TAG, client_txn_id);
            Log.i(TAG, txn_id);
            // this function is called when payment is done (success, scanning ,timeout or cancel by user).
            // You must call the check order status API in server and get update about payment.
            // 🚫 Do not Call UpiGateway API in Android App Directly.
            Toast.makeText(context, "Order ID: " + client_txn_id + ", Txn ID: " + txn_id, Toast.LENGTH_SHORT).show();
            // Close the Webview.
        }

        @JavascriptInterface
        public void errorResponse() {
            // this function is called when Transaction in Already Done or Any other Issue.
            Toast.makeText(context, "Transaction Error.", Toast.LENGTH_SHORT).show();
            // Close the Webview.
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("check", "onResume: ");
    }

    private void createPaymentOrder() {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.ekqr.in/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        Log.e("check", "transactionId: " +TransactionId());

        DataModel dataModel = new DataModel("e163fdc5-9296-42f5-818a-3f77972d997f",
                TransactionId(),
                "1",
                "Product Name",
                "Jon Doe",
                "jondoe@gmail.com",
                "9876543210",
                "http://google.com",
                "user defined field 1 (max 25 char)",
                "user defined field 2 (max 25 char)",
                "user defined field 3 (max 25 char)");

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);


        Call<ResponseData> call = retrofitAPI.createOrder(dataModel);

        call.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {


                Toast.makeText(MainActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
                Log.e("check", "Success Response: " + response.message());

                paymentUrl = response.body().getData().getPayment_url();

                if (paymentUrl.startsWith("upi:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setData(Uri.parse(PAYMENT_URL));
                    intent.setData(Uri.parse(paymentUrl));
                    startActivity(intent);
                } else {
//            mWebView.loadUrl(PAYMENT_URL);
                    mWebView.loadUrl(paymentUrl);
                }

            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Toast.makeText(MainActivity.this, "" + t, Toast.LENGTH_SHORT).show();
                Log.e("check", "Failure Response: " + t);

            }
        });

    }

    public String TransactionId(){
        Random random = new Random();
        int number = random.nextInt(999999);
        String transactionId = String.format("%06d", number);
        return "trans-"+transactionId;
    }


}