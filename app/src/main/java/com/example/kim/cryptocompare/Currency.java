package com.example.kim.cryptocompare;

/**
 * Created by kim on 10/25/17.
 */

public class Currency {
    private String title;
    private double btcValue, ethValue;

    public Currency() {
    }

    public Currency(String title, double btcValue, double ethValue) {
        this.title = title;
        this.btcValue = btcValue;
        this.ethValue = ethValue;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getBtcValue() {
        return btcValue;
    }

    public void setBtcValue(double btcValue) {
        this.btcValue = btcValue;
    }

    public double getEthValue() {
        return ethValue;
    }

    public void setEthValue(double ethValue) {
        this.ethValue = ethValue;
    }


}
