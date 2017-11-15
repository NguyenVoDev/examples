package com.nguyenvo.rest.example.example.service.connector;

import com.nguyenvo.rest.example.example.exception.ClosePriceException;

import java.util.Map;

public interface QuandlConnector {
    String getCloseForTickerInDateRange(String tickerSymbol, String startDate, String endDate) throws ClosePriceException;
}
