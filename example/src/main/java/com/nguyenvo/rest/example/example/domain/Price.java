package com.nguyenvo.rest.example.example.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Price implements Serializable{

    @JsonProperty("Ticker")
    private String ticker;

    @JsonProperty("DateClose")
    private List<String[]> dateClose = new ArrayList<>();

    public List<String[]> getDateClose() {
        return dateClose;
    }

    public String getTicker() {
        return ticker;
    }

    public void setDateClose(List<String[]> dateClose) {
        this.dateClose = dateClose;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }
}
