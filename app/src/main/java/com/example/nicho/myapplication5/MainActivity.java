package com.example.nicho.myapplication5;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ArrayList<Stock> Stock_List = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swiper;

    Boolean Connected = true;
    String UserInput;
    private StockAdapter stockAdapter;
    AsyncStockname asyncStockname;
    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        stockAdapter = new StockAdapter(this, Stock_List);
        recyclerView.setAdapter(stockAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        asyncStockname = new AsyncStockname(this);
        asyncStockname.execute("456");
        databaseHandler = new DatabaseHandler(this);
        swiper=(SwipeRefreshLayout)findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            //this method is used to refrsh the code and update stocks and also check the network connectivity
            public void onRefresh() {
                if(!NetworkConnectionChecking())
                {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertBuilder.setMessage("Warning...Please connect to internet");
                    alertBuilder.setTitle("No Internet");
                    AlertDialog alertdialog = alertBuilder.create();
                    alertdialog.show();
                }
                else
                    Refreshing();
                swiper.setRefreshing(false);
            }
        });
    }

    private void Refreshing() {
        databaseHandler.dumptoLog();
        Log.d(TAG, "onRefreshing: ");
        ArrayList<String[]> Arraylist = databaseHandler.loadStocks();
        ArrayList<Stock> Stocks_List = new ArrayList<>();
        for (int k = 0; k < Arraylist.size(); k++) {
            new AsyncLoadFinancialData(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Arraylist.get(k)[0]);
        }
        Stock_List.clear();
        Stock_List.addAll(Stocks_List);
        Log.d(TAG, "onResume: " + Arraylist);
        stockAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stock_menu, menu);
        return true;
    }

    public RecyclerView getRecyclerView()
    {
        return recyclerView;
    }

    public DatabaseHandler getdatabaseHandler() {

        return databaseHandler;
    }

    @Override
    protected void onResume() {
        databaseHandler.dumptoLog();

        if(!NetworkConnectionChecking())
        {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
            alertBuilder.setMessage("Warning...Please connect to internet");
            alertBuilder.setTitle("No Internet");

            AlertDialog alertdialog = alertBuilder.create();
            alertdialog.show();
        }
        else {
            Log.d(TAG, "onResume: ");
            ArrayList<String[]> Arraylist = databaseHandler.loadStocks();

            ArrayList<Stock> Stocks_List = new ArrayList<>();
            for (int k = 0; k < Arraylist.size(); k++) {
                new AsyncLoadFinancialData(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Arraylist.get(k)[0]);
            }

            Stock_List.clear();

            Stock_List.addAll(Stocks_List);
            Log.d(TAG, "onResume: " + Arraylist);
            stockAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        databaseHandler.close();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_stock:
                Log.d(TAG, "onOptionsItemSelected: Add Stock");
                NetworkConnectionChecking();
                if (Connected == true) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    final EditText editText = new EditText(this);
                    editText.setInputType(InputType.TYPE_CLASS_TEXT);
                    editText.setGravity(Gravity.CENTER_HORIZONTAL);
                    editText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

                    alertBuilder.setView(editText);
                    alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d(TAG, "onClick: ");
                            UserInput = editText.getText().toString();
                            asyncStockname = new AsyncStockname(MainActivity.this);
                            asyncStockname.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, UserInput.toString());

                        }
                    });

                    alertBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d(TAG, "onClick: ");
                        }
                    });

                    alertBuilder.setMessage("Please Enter The Stock Symbol:");
                    alertBuilder.setTitle("Stock Selection");

                    AlertDialog alertdialog = alertBuilder.create();
                    alertdialog.show();
                } else {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setMessage("Warning...No internet Connection");
                    alertBuilder.setTitle("No Internet");

                    AlertDialog alertdialog = alertBuilder.create();
                    alertdialog.show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean NetworkConnectionChecking() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = connectivityManager.getActiveNetworkInfo();
        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            Connected = true;
        } else {
            Connected = false;
        }
        return Connected;
    }

    public void invalidstockentry(String UserInput) {
        String input = UserInput;
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Symbol Not Found :" + input.toUpperCase());
        alertBuilder.setMessage("Data for Stock Symbol");

        AlertDialog alertdialog = alertBuilder.create();
        alertdialog.show();
    }


    public void AsyncSymbolData(HashMap Symbol_Stock, String stock) {

        if (Symbol_Stock.size() == 0) {
            invalidstockentry(stock);

        }

        else if (Symbol_Stock.size() == 1) {
            getdatastockfromfinancial(stock);
        }

        else {
            multiplestocksymbol(Symbol_Stock);
        }
    }


    public void multiplestocksymbol(HashMap MapOfStock) {
        HashMap<String, String> stocks = MapOfStock;
        Log.d(TAG, "multiplestocksymbol: " + MapOfStock.size());
        int k = 0;
        final CharSequence[] stockdisplay = new CharSequence[stocks.size()];
        for (Map.Entry<String, String> entry : stocks.entrySet()) {
            String line = entry.getKey() + " - " + entry.getValue();
            Log.d(TAG, "displayMultipleStockListDialog: map= " + line);
            stockdisplay[k++] = line;
        }
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Make a Selection");
        alertBuilder.setItems(stockdisplay, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String stockName = stockdisplay[i].toString().substring(0, (stockdisplay[i].toString().indexOf("-") - 1));
                getdatastockfromfinancial(stockName);
                Log.d(TAG, "onClick: ");
            }
        });

        alertBuilder.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog alertdialog = alertBuilder.create();
        alertdialog.show();
    }

    public void getdatastockfromfinancial(String Stock_Symbol) {
        Log.d(TAG, "getStockFinancialDetails: " + Stock_Symbol);
        boolean flag = false;
        for(int i=0; i< Stock_List.size() && flag == false; i++)
        {
            if(Stock_List.get(i).getSymbolOfStock().equals(Stock_Symbol)){
            Log.d(TAG, "getdatastockfromfinancialdata ");

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle("Duplicate Stock");
                alertBuilder.setMessage("Stock Symbol"+ " "+Stock_Symbol + " is already in the list.");
                alertBuilder.setIcon(R.drawable.ic_warning);

                AlertDialog alertdialog = alertBuilder.create();
                alertdialog.show();
                flag =true;
        }
        }
        if(flag == false)
            new AsyncLoadFinancialData(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Stock_Symbol);
    }


    public void AsyncFinancialData(HashMap Financial_Stock) {
        if (Financial_Stock == null || Financial_Stock.size() == 0) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle("Data not Exists");
            alertBuilder.setMessage("There is no Financial Data available for this stock");

            AlertDialog alertdialog = alertBuilder.create();
            alertdialog.show();

        }
        else {
            Log.d(TAG, "AsyncFinancialData:");
            Stock stock1 = new Stock();
            stock1.setSymbolOfStock(Financial_Stock.get("symbol").toString());
            stock1.setCompanyName(Financial_Stock.get("CompanyName").toString());

            try {
                Log.d(TAG, "AsyncFinancialData: in try");
                Double val = Double.parseDouble(Financial_Stock.get("latestprice").toString());
                stock1.setTradinPrice(val);

                val = Double.parseDouble(Financial_Stock.get("Chnage in Price").toString());
                stock1.setChangeInAmount(val);

                val = Double.parseDouble(Financial_Stock.get("Change in Percent").toString());
                stock1.setPriceInPercent(val);
            } catch (Exception e) {
                Log.d(TAG, "getFromAsyncTaskStockFinancialData: Number Format Exception");
            }

            Stock_List.add(stock1);
            stockAdapter.sortList();
            stockAdapter.notifyDataSetChanged();
            databaseHandler.addStock(stock1);
        }
    }
}




