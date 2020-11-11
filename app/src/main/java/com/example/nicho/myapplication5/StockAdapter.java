package com.example.nicho.myapplication5;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class StockAdapter extends RecyclerView.Adapter<StockViewHolder>{

    private static final String TAG = "StockAdapter";
    private ArrayList<Stock> stocks_listdata;
    private DatabaseHandler databaseHandler;
    private RecyclerView recyclerView;
    private static String Financial_URL=  "http://www.marketwatch.com/investing/stock/";

    private static DecimalFormat decimalFormat = new DecimalFormat(".##");
    private MainActivity mainActivity;

    public StockAdapter(MainActivity ma,ArrayList<Stock> stocks){
        mainActivity=ma;
        stocks_listdata=stocks;
    }
    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");


        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_stockentry, parent, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                final int position=mainActivity.getRecyclerView().getChildLayoutPosition(view);
                String url=Financial_URL + stocks_listdata.get(position).getSymbolOfStock();
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                mainActivity.startActivity(intent);
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            //on long click deletes the particular data clicked from list and database
            public boolean onLongClick(View view) {
                Log.d(TAG, "onLongClick: IN");
                final int position=mainActivity.getRecyclerView().getChildLayoutPosition(view);
                AlertDialog.Builder alertBuilder=new AlertDialog.Builder(mainActivity);
                alertBuilder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mainActivity.getdatabaseHandler().deleteStock(stocks_listdata.get(position).getSymbolOfStock());
                        stocks_listdata.remove(position);
                        notifyDataSetChanged();
                    }
                });
                alertBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                alertBuilder.setMessage("Delete Stock Symbol "+stocks_listdata.get(position).getSymbolOfStock()+ "?");
                alertBuilder.setTitle("Delete Stock");
                alertBuilder.setIcon(R.drawable.ic_delete);
                AlertDialog alertdialog=alertBuilder.create();
                alertdialog.show();
                Log.d(TAG, "onLongClick: ");
                return true;

            }
        });

        return new StockViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {

        Stock stock_view=stocks_listdata.get(position);

        if(stock_view.getChangeInAmount() < 0)
        {
            holder.SymbolOfStock.setTextColor(Color.RED);
            holder.CompanyName.setTextColor(Color.RED);
            holder.TradinPrice.setTextColor(Color.RED);
            holder.ChangeInAmount.setTextColor(Color.RED);
            holder.PriceInPercent.setTextColor(Color.RED);

            holder.SymbolOfStock.setText(stock_view.getSymbolOfStock());
            holder.CompanyName.setText(stock_view.getCompanyName());
            holder.TradinPrice.setText(""+stock_view.getTradinPrice());

            holder.ChangeInAmount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_down_24,0,0,0);
            holder.ChangeInAmount.setText(""+stock_view.getChangeInAmount());
            holder.PriceInPercent.setText("("+ String.format("%.2f",(stock_view.getPriceInPercent()*100))+ "%)");

        }
        else {
            holder.SymbolOfStock.setTextColor(Color.GREEN);
            holder.CompanyName.setTextColor(Color.GREEN);
            holder.TradinPrice.setTextColor(Color.GREEN);
            holder.ChangeInAmount.setTextColor(Color.GREEN);
            holder.PriceInPercent.setTextColor(Color.GREEN);
            holder.SymbolOfStock.setText(stock_view.getSymbolOfStock());
            holder.CompanyName.setText(stock_view.getCompanyName());
            holder.ChangeInAmount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_up_24,0,0,0);
            holder.TradinPrice.setText("" + stock_view.getTradinPrice());
            holder.ChangeInAmount.setText("" + stock_view.getChangeInAmount());
            holder.PriceInPercent.setText("(" + String.format("%.2f", (stock_view.getPriceInPercent() * 100)) + "%)");
        }
    }

    @Override
    public int getItemCount() {
        return stocks_listdata.size();
    }


    public ArrayList<Stock> sortList()
    {
        Collections.sort(stocks_listdata, new Comparator<Stock>() {
            @Override
            public int compare(Stock stock, Stock stock1) {
                return stock.compare(stock.getSymbolOfStock(), stock1.getSymbolOfStock());
            }
        });
        return stocks_listdata;
    }
}
