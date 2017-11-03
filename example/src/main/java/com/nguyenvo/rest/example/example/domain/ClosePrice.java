package com.nguyenvo.rest.example.example.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class ClosePrice implements Serializable {
    @JsonProperty("Prices")
    private Price price;

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }
}
