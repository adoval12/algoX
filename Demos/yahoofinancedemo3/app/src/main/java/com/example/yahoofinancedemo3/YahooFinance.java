package com.example.yahoofinancedemo3;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class YahooFinance {
    public static final double[][] arrERROR = {{0}};

    public static double[][] requestSpark(String[] tickers, String range, String interval, RequestQueue requestQueue) {
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

        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, new JSONObject(), requestFuture, requestFuture){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("accept", "application/json");
                params.put("X-API-KEY", key);
                return params;
            }
        };
        requestQueue.add(request);
        double[][] arrData;
        JSONArray closePrices;
        Log.d("Url", url);
        try {
            JSONObject response = requestFuture.get(4, TimeUnit.SECONDS);
            Log.d("response", response.toString());
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
