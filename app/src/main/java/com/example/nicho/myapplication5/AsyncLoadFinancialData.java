package com.example.nicho.myapplication5;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class AsyncLoadFinancialData extends AsyncTask<String, Void, String> {

    private static final String TAG = "AsyncFinancialLoader";
    private MainActivity mainActivity;

    private final String Stock_url = "https://api.iextrading.com";
    private String UserInput;

    public AsyncLoadFinancialData(MainActivity mainActivity) {

        this.mainActivity = mainActivity;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);

        Log.d(TAG, "onPostExecute: ");
        HashMap Show_Stock = JsonFinancialStockData(string);
        Log.d(TAG, "onPostExecute: ");
        mainActivity.AsyncFinancialData(Show_Stock);
    }

    @Override
    protected String doInBackground(String... strings) {

        UserInput = strings[0];

        Uri.Builder buildUri = Uri.parse(Stock_url).buildUpon();

        buildUri.appendPath("1.0");
        buildUri.appendPath("stock");
        buildUri.appendPath(UserInput);
        buildUri.appendPath("quote");

        String URLToUse = buildUri.build().toString();
        Log.d(TAG, "doInBackground:  " + URLToUse);

        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(URLToUse);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.getRequestMethod();
            connection.getResponseCode();
            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            Log.d(TAG, "doInBackground: " + stringBuilder.toString());
        } catch (Exception e) {
            Log.d(TAG, "doInBackground: Exception thrown");
            e.printStackTrace();
            return null;
        }

        String jsonFinancialStock = stringBuilder.toString();
        return jsonFinancialStock;
    }



    private HashMap JsonFinancialStockData(String string) {
        if (string == null)
            return null;

        HashMap Financial_Stock = new HashMap();
        try {
            JSONObject jsonObject = new JSONObject(string);
            String CompanyName = jsonObject.getString("companyName");
            String LatestPrice = jsonObject.getString("latestPrice");
            String Price_Change = jsonObject.getString("change");
            String ChangeInPercent = jsonObject.getString("changePercent");

            Financial_Stock.put("symbol", UserInput);
            Financial_Stock.put("CompanyName", CompanyName);
            Financial_Stock.put("latestprice", LatestPrice);
            Financial_Stock.put("Chnage in Price", Price_Change);
            Financial_Stock.put("Change in Percent", ChangeInPercent);

            Log.d(TAG, "parseFinJSONData: price= " + LatestPrice + " change_in_price = " + Price_Change + " change_in_percent = " + ChangeInPercent);
        }
        catch (Exception e) {
            Log.d(TAG, "parseFinJSONData: Error....");
            e.printStackTrace();
            return null;
        }
        return Financial_Stock;
    }
}

