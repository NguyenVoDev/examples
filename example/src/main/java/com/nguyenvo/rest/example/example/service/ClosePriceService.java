package com.nguyenvo.rest.example.example.service;

import com.nguyenvo.rest.example.example.domain.ClosePrice;
import com.nguyenvo.rest.example.example.domain.MultipleTickersWith200DMA;
import com.nguyenvo.rest.example.example.domain.TwoHundredDaysMovingAverage;
import com.nguyenvo.rest.example.example.exception.ClosePriceException;

import java.util.List;

public interface ClosePriceService {
    ClosePrice getCloseForTickerInDateRange(String tickerSymbol, String startDate, String endDate) throws ClosePriceException;

    TwoHundredDaysMovingAverage getMVAForTicker(String tickerSymbol, String startDate) throws ClosePriceException;

    MultipleTickersWith200DMA getMultipleTickersWith200DMA(List<String> tickers, String startDate);
}
