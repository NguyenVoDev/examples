package com.nguyenvo.rest.example.example.service.connector;

import java.util.Map;

public interface QuandlConnector {
    String getCloseForTickerInDateRange(String tickerSymbol, String startDate, String endDate);
}
