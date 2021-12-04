package com.example.algorx;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class callYahooFinance {

    public static double[][] sparkCallbackData;
    public static double[][] chartCallbackData;
    public static final double[][] arrERROR = {{0}};
    /*
     * @param ticker: string of tickers, max 10, seperated by comma. EX: "MSFT,TSLA,AMZN"
     * @param range: 1d 5d 1mo 3mo 6mo 1y 5y max
     * @param interval: 1m 5m 15m 1d 1wk 1mo
     */
    public static double[][] requestSpark(String[] tickers, String range, String interval, Context act, RequestQueue requestQueue){
        String strTickers = "";
        for (String ticker : tickers){
            strTickers += (ticker + ',');
        }
        strTickers = strTickers.substring(0, strTickers.length() - 1);

        // SECRET KEY
        String key = "3Z8LSHmB1l8lfS6qpRoba35QRos3zDZ69s2JS8IJ";
        // Build URL
        String url = "https://yfapi.net/v8/finance/spark?";
        url = url + "interval=" + interval;     //add sampling interval
        url = url + "&range=" + range;          //add range
        url = url + "&symbols=" + strTickers;   //add ticker

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    /* Handles a JSON object response. Returns data in the form:
                     * TODO: figure out format to return spark data
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        double[][] arrData;
                        // parse response
                        JSONObject stockData;
                        JSONArray closePrices;
                        try {
                            int numDatapoints = response.getJSONObject(tickers[0]).getJSONArray("close").length();
                            arrData = new double[response.length()][numDatapoints];
                            for (int i=0; i<tickers.length; i++) {
                                closePrices = response.getJSONObject(tickers[i]).getJSONArray("close");
                                for(int c=0; c < closePrices.length(); c++) {
                                    double price = closePrices.getDouble(c);
                                    arrData[i][c] = price;
                                }
                            }
                            sparkCallbackData = arrData;
                        } catch (Exception e) {
                            e.getStackTrace();
                            sparkCallbackData = arrERROR;
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("JSONObject VolleyError", "Error: " + error.getMessage());

                        if (error instanceof TimeoutError) {
                            Toast.makeText(act,
                                    "Bad Network, Try again",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof NoConnectionError) {
                            Toast.makeText(act,
                                    "Bad Network, Try again",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(act,
                                    "Auth failed",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(act,
                                    "Server Not Responding",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(act,
                                    "Network Error",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(act,
                                    "try again"+error.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        sparkCallbackData = arrERROR;
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("accept", "application/json");
                params.put("X-API-KEY", key);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
        while(sparkCallbackData == null);
        return sparkCallbackData;
    }

    /*
     * @param ticker: string of tickers, max 10, seperated by comma. EX: "MSFT,TSLA,AMZN"
     * @param range: 1d 5d 1mo 3mo 6mo 1y 5y 10y ytd max
     * @param interval: 1m 5m 15m 1d 1wk 1mo
     */
    public static double[][] requestChart(String ticker, String range, String interval, Context act, RequestQueue requestQueue){
        chartCallbackData = null;
        // SECRET KEY
        String key = "3Z8LSHmB1l8lfS6qpRoba35QRos3zDZ69s2JS8IJ";
        // Build URL
        String url = "https://yfapi.net/v8/finance/chart/";
        url = url + ticker + "?";               //add ticker
        url = url + "range=" + range;           //add range
        url = url + "&region=US";               //add region
        url = url + "&interval=" + interval;    //add sampling interval
        url = url + "&lang=en";                 //add language

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    /* Handles a JSON object response. Returns data in the form:
                     * [[open1, high1, low1, close1],
                     *  [open2, high2, low2, close2],
                     *  ...
                     * ]
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        double[][] finalData;
                        JSONArray JSONopen;
                        JSONArray JSONhigh;
                        JSONArray JSONclose;
                        JSONArray JSONlow;
                        try {
                            JSONArray result = response.getJSONObject("chart").getJSONArray("result");
                            JSONObject indicators = result.getJSONObject(0).getJSONObject("indicators");
                            JSONObject quote = indicators.getJSONArray("quote").getJSONObject(0);
                            JSONopen = quote.getJSONArray("open");
                            JSONhigh = quote.getJSONArray("high");
                            JSONclose = quote.getJSONArray("close");
                            JSONlow = quote.getJSONArray("low");
                            finalData = new double[JSONopen.length()][4];
                            for (int i=0; i < JSONopen.length(); i++){
                                double[] row = {JSONopen.getDouble(i), JSONhigh.getDouble(i), JSONlow.getDouble(i), JSONclose.getDouble(i)};
                                finalData[i] = row;
                            }
                            chartCallbackData = finalData;
                        } catch (Exception e) {
                            e.getStackTrace();
                            chartCallbackData = arrERROR;
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("JSONObject VolleyError", "Error: " + error.getMessage());

                        if (error instanceof TimeoutError) {
                            Toast.makeText(act,
                                    "Bad Network, Try again",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof NoConnectionError) {
                            Toast.makeText(act,
                                    "Bad Network, Try again",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(act,
                                    "Auth failed",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(act,
                                    "Server Not Responding",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(act,
                                    "Network Error",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            Toast.makeText(act,
                                    "try again"+error.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("accept", "application/json");
                params.put("X-API-KEY", key);
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
        while(chartCallbackData == null);
        return chartCallbackData;
    }

}
