package com.example.yahoofinancedemo3;

import android.os.StrictMode;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class YahooFinanceOkHttp {

    public static final double[][] arrERROR = {{0}};

    public static double[][] requestSpark(String[] tickers, String range, String interval, RequestQueue requestQueue) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String strTickers = "";
        for (String ticker : tickers) {
            strTickers += (ticker + ',');
        }
        strTickers = strTickers.substring(0, strTickers.length() - 1);

        // SECRET KEY
        String oldkey = "3Z8LSHmB1l8lfS6qpRoba35QRos3zDZ69s2JS8IJ";
        String key = "PdF8chYrre8QDuxrf6Tdz35AIlhDLgtH4IIymY6J";

        // Build URL
        String url = "https://yfapi.net/v8/finance/spark?";
        url = url + "interval=" + interval;     //add sampling interval
        url = url + "&range=" + range;          //add range
        url = url + "&symbols=" + strTickers;   //add ticker
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .addHeader("X-API-KEY", key)
                .build();
        double[][] arrData;
        try {
            Response rawResponse = client.newCall(request).execute();
            if (!rawResponse.isSuccessful()) return arrERROR;
            ResponseBody body = rawResponse.body();
            Log.d("raw data", rawResponse.toString());
            JSONObject response = new JSONObject(rawResponse.toString());

            JSONArray closePrices;
            int numDatapoints = response.getJSONObject(tickers[0]).getJSONArray("close").length();
            arrData = new double[response.length()][numDatapoints];

                for (int i = 0; i < tickers.length; i++) {
                    closePrices = response.getJSONObject(tickers[i]).getJSONArray("close");
                    for (int c = 0; c < closePrices.length(); c++) {
                        double price = closePrices.getDouble(c);
                        arrData[i][c] = price;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                arrData = arrERROR;
            }

            return arrData;
     }
}
