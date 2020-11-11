package com.example.nicho.myapplication5;

import android.app.AlertDialog;
import android.content.AsyncTaskLoader;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;



public class AsyncStockname extends AsyncTask<String, Integer,String> {

    private MainActivity mainActivity;
    private int count;

    private static final String TAG = "AsyncStockname";
    private HashMap<String, String> stock_map = new HashMap<>();
    private String webURL = " https://api.iextrading.com/1.0/ref-data/symbols ";
    String input;
    StringBuilder stringbuilder;

    public AsyncStockname(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        if (input != "456") {
            String stringdata = stringbuilder.toString();
            Log.d(TAG, "onPostExecute: " + stringdata);
            try {

                JSONArray jsonArray = new JSONArray(stringdata);

                if (jsonArray.length() == 0) {
                    mainActivity.invalidstockentry(input);
                }

                for (int k = 0; k < jsonArray.length(); k++) {
                    JSONObject jsonObject1 = (JSONObject) jsonArray.get(k);
                    String type = jsonObject1.getString("type");
                    String SymbolOfStock = jsonObject1.getString("symbol");
                    String CompanyName = jsonObject1.getString("name");
                    if (!SymbolOfStock.contains(".")) {
                        stock_map.put(SymbolOfStock, CompanyName);
                        Log.d(TAG, "onPostExecute: " + SymbolOfStock + CompanyName);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "onPostExecute: Excption thrown");
            }
            HashMap<String, String> stocks = new HashMap<>();
            for (Map.Entry<String, String> entry : stock_map.entrySet()) {
                if (entry.getKey().toString().startsWith(input)) {
                    stocks.put(entry.getKey().toString(), entry.getValue().toString());
                }
            }

            mainActivity.AsyncSymbolData(stocks, input);
        }
    }


    @Override
    protected String doInBackground(String... strings) {

        Uri.Builder buildURL = Uri.parse(webURL).buildUpon();
        String URLToUse = buildURL.build().toString();

        stringbuilder = new StringBuilder();

        try {
            URL url = new URL(URLToUse);
            Log.d(TAG, "doInBackground: " + URLToUse);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputsteam = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputsteam));

            String line;
            while ((line = reader.readLine()) != null) {
                stringbuilder.append(line).append("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        input = strings[0];
        return null;
    }



}
