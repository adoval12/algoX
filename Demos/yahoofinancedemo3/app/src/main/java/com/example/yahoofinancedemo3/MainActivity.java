package com.example.yahoofinancedemo3;

import static com.android.volley.toolbox.Volley.newRequestQueue;

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
import com.android.volley.toolbox.StringRequest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    RequestQueue requestQueue;
    TextView txtResponse;
    EditText etxtTicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtResponse = (TextView) findViewById(R.id.txtResponse);
        etxtTicker = (EditText) findViewById(R.id.etxtTicker);
        // 2. create new default request queue
        requestQueue = newRequestQueue(this);
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }

    public void requestSparkSync(View view){
        String[] temp1 = new String[]{"TSLA", "MSFT"};
        double[][] data = YahooFinance.requestSpark(temp1, "5d", "1d", requestQueue);
        txtResponse.setText(Arrays.deepToString(data));
    }

    public void requestSparkSyncOkHttp(View view){
        String[] temp1 = new String[]{"TSLA", "MSFT"};
        double[][] data = YahooFinanceOkHttp.requestSpark(temp1, "5d", "1d", requestQueue);
        txtResponse.setText(Arrays.deepToString(data));
    }

    public void requestChart(String ticker){
        Context act = getApplicationContext();
        // SECRET KEY
        String key = "3Z8LSHmB1l8lfS6qpRoba35QRos3zDZ69s2JS8IJ";
        // Build URL
        String url = "https://yfapi.net/v8/finance/chart/";
        url = url + ticker + "?";               //add ticker
        url = url + "range=5d";                 //add range
        url = url + "&region=US";               //add region
        url = url + "&interval=1d";             //add sampling interval
        url = url + "&lang=en";                 //add language
        url = url + "&events=div%2C%20split";   //add events

        // 3. create request and add to queue
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
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

                        } catch (Exception e) {
                            e.getStackTrace();
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
    }

    public void requestSpark(String[] tickers, String range, String interval){
        Context act = getApplicationContext();
        String strTickers = "";
        for (String ticker : tickers){
            strTickers += (ticker + ',');
        }
        strTickers = strTickers.substring(0, strTickers.length() - 1);

        // SECRET KEY
        String oldkey = "3Z8LSHmB1l8lfS6qpRoba35QRos3zDZ69s2JS8IJ";
        String key = "PdF8chYrre8QDuxrf6Tdz35AIlhDLgtH4IIymY6J";

        // Build URL
        String url = "https://yfapi.net/v8/finance/spark?";
        url = url + "interval=" + interval;    //add sampling interval
        url = url + "&range=" + range;           //add range
        url = url + "&symbols=" + strTickers;     //add ticker

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    /* Handles a JSON object response. Returns data in the form:
                     * TODO: figure out format to return spark data
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        // parse response
                        double[][] arrData;
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
                            txtResponse.setText(Arrays.deepToString(arrData));
                        } catch (Exception e) {
                            e.getStackTrace();
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
    }


    public void reqOnClick(View view){
        String ticker = etxtTicker.getText().toString();
        requestChart(ticker);
    }

    public void reqSpkOnClick(View view){
        String[] tickers = {"AMZN", "msft", "CGC"};
        requestSpark(tickers, "5d", "1d");
    }

}