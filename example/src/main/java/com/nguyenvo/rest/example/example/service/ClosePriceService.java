package com.nguyenvo.rest.example.example.service;

import com.nguyenvo.rest.example.example.domain.ClosePrice;

public interface ClosePriceService {
    ClosePrice getCloseForTickerInDateRange(String tickerSymbol, String startDate, String endDate);
}
