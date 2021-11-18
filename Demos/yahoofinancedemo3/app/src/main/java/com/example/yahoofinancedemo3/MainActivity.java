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

import org.json.JSONException;
import org.json.JSONObject;

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
                        txtResponse.setText("Response: " + response.toString());
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
}