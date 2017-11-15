package com.nguyenvo.rest.example.example.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultipleTickersWith200DMA {
    @JsonProperty("ListOf200dmaValidTickers")
    private
    List<TwoHundredDaysMovingAverageValue> twoHundredDaysMovingAverages = new CopyOnWriteArrayList<>();

    @JsonProperty("InvalidTickers")
    private
    List<String> errorTickers = new CopyOnWriteArrayList<>();

    public List<String> getErrorTickers() {
        return errorTickers;
    }

    public List<TwoHundredDaysMovingAverageValue> getTwoHundredDaysMovingAverages() {
        return twoHundredDaysMovingAverages;
    }

    public void setErrorTickers(List<String> errorTickers) {
        this.errorTickers = errorTickers;
    }

    public void setTwoHundredDaysMovingAverages(List<TwoHundredDaysMovingAverageValue> twoHundredDaysMovingAverages) {
        this.twoHundredDaysMovingAverages = twoHundredDaysMovingAverages;
    }
}
