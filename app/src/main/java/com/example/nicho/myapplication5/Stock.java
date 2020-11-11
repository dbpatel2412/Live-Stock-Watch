package com.example.nicho.myapplication5;

import android.support.annotation.NonNull;

import java.util.Comparator;


public class Stock implements Comparator<String> {

    private String CompanyName;
    private String SymbolOfStock;
    private double PriceInPercent;
    private double ChangeInAmount;
    private double TradinPrice;

    private int counter=1;

    public String getSymbolOfStock() {
        return SymbolOfStock;
    }

    public void setSymbolOfStock(String SymbolOfStock) {
        this.SymbolOfStock = SymbolOfStock;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String CompanyName) {
        this.CompanyName = CompanyName;
    }

    public double getTradinPrice() {
        return TradinPrice;
    }

    public void setTradinPrice(double TradinPrice) {
        this.TradinPrice = TradinPrice;
    }

    public double getChangeInAmount() {
        return ChangeInAmount;
    }

    public void setChangeInAmount(double ChangeInAmount) {
        this.ChangeInAmount = ChangeInAmount;
    }

    public double getPriceInPercent() {
        return PriceInPercent;
    }

    public void setPriceInPercent(double PriceInPercent) {
        this.PriceInPercent = PriceInPercent;
    }

    @Override
    public int compare(String s, String t1) {
        return s.compareTo(t1);
    }
}
