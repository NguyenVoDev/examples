package com.nguyenvo.rest.example.example.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nguyenvo on 03/11/2017.
 */
public class TwoHundredDaysMovingAverageValue {
    @JsonProperty("Ticker")
    private String ticker;
    @JsonProperty("Avg")
    private String average;

    public String getAverage() {
        return average;
    }

    public String getTicker() {
        return ticker;
    }

    public void setAverage(String average) {
        this.average = average;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }
}
