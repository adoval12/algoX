package com.example.algorx;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import static com.android.volley.toolbox.Volley.newRequestQueue;
import static com.example.algorx.callYahooFinance.requestSpark;
import static com.example.algorx.callYahooFinance.requestChart;


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
import com.example.algorx.fragments.AccountFragment;
import com.example.algorx.fragments.AlgoFragment;
import com.example.algorx.fragments.HistoryFragment;
import com.example.algorx.fragments.HomeFragment;
import com.example.algorx.fragments.TransactionFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity  {
    RequestQueue requestQueue;

    /*
     * @param ticker: string of tickers, max 10, seperated by comma. EX: "MSFT,TSLA,AMZN"
     * @param range: 1d 5d 1mo 3mo 6mo 1y 5y 10y ytd max
     * Returns data in the form:
     * [[open1, high1, low1, close1],
     *  [open2, high2, low2, close2],
     *  ...
     * ]
     */
    public double[][] callChart(String ticker, String range) {
        Context act = getApplicationContext();
        double[][] temp = requestChart(ticker, range, "1d", act, requestQueue);
        Log.d("log", temp.toString());
        return temp;
    }

    /*
     * @param ticker: array of tickers, max 10"
     * @param range: 1d 5d 1mo 3mo 6mo 1y 5y max
     *
     * Returns data in the form: (each row for one ticker)
     * [[close1, close2, close3, close4, close5, ..., closex],
     *  [close1, close2, close3, close4, close5, ..., closex],
     *  ...
     * ]
     */
    public double[][] callSpark(String[] tickers, String range) {
        Context act = getApplicationContext();
        double[][] temp = requestSpark(tickers, range, "1d", act, requestQueue);
        return temp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = newRequestQueue(this);

        //Fragments from the Bottom Navigation Bar
        HomeFragment homeFragment = new HomeFragment();
        TransactionFragment transactionFragment = new TransactionFragment();
        AlgoFragment algoFragment = new AlgoFragment();
        HistoryFragment historyFragment = new HistoryFragment();
        AccountFragment accountFragment = new AccountFragment();

        makeCurrentFragment(homeFragment);

        double[][] temp = callChart("amzn", "5d");
        Log.d("log", temp.toString());


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.btn_home:
                        makeCurrentFragment(homeFragment);
                        break;
                    case R.id.btn_transaction:
                        makeCurrentFragment(transactionFragment);
                        break;
                    case R.id.btn_algo:
                        makeCurrentFragment(algoFragment);
                        break;
                    case R.id.btn_history:
                        makeCurrentFragment(historyFragment);
                        break;
                    case R.id.btn_account:
                        makeCurrentFragment(accountFragment);
                        break;
                }
                return true;
            }
        });

    }

    @Override
    protected void onStop () {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }

    private void makeCurrentFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_wrapper, fragment).commit();
    }

}


