package com.nguyenvo.rest.example.example.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

public class TwoHundredDaysMovingAverage {

    @JsonProperty("200dma")
    private TwoHundredDaysMovingAverageValue value;

    public TwoHundredDaysMovingAverageValue getValue() {
        return value;
    }

    public void setValue(TwoHundredDaysMovingAverageValue value) {
        this.value = value;
    }
}
