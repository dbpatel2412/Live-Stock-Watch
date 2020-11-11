package com.example.nicho.myapplication5;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.TimeoutException;


public class StockViewHolder extends RecyclerView.ViewHolder{

    public TextView CompanyName;
    public TextView SymbolOfStock;
    public TextView PriceInPercent;
    public TextView ChangeInAmount;
    public TextView TradinPrice;
    public ImageView changesymbol;



    public StockViewHolder(View itemView) {

        super(itemView);
        CompanyName = (TextView) itemView.findViewById(R.id.CompanyName);
        SymbolOfStock = (TextView) itemView.findViewById(R.id.SymbolOfStock);
        PriceInPercent=(TextView)itemView.findViewById(R.id.PriceInPercent);
        ChangeInAmount=(TextView)itemView.findViewById(R.id.ChangeInAmount);
        TradinPrice=(TextView)itemView.findViewById(R.id.TradinPrice);



    }
}
