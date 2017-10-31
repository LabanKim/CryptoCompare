package com.example.kim.cryptocompare;

/**
 * Created by kim on 10/31/17.
 */

public class CustomArray {

    String title, curr;

    public CustomArray(String title, String curr) {
        this.title = title;
        this.curr = curr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCurr() {
        return curr;
    }

    public void setCurr(String curr) {
        this.curr = curr;
    }
}
