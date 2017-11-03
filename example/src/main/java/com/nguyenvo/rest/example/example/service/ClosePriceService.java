package com.nguyenvo.rest.example.example.service;

import com.nguyenvo.rest.example.example.domain.ClosePrice;
import com.nguyenvo.rest.example.example.domain.TwoHundredDaysMovingAverage;

public interface ClosePriceService {
    ClosePrice getCloseForTickerInDateRange(String tickerSymbol, String startDate, String endDate);

    TwoHundredDaysMovingAverage getMVAForTicker(String tickerSymbol, String startDate);
}
