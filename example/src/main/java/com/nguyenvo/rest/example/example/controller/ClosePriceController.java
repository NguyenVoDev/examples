package com.nguyenvo.rest.example.example.controller;

import com.nguyenvo.rest.example.example.domain.ClosePrice;
import com.nguyenvo.rest.example.example.domain.MultipleTickersWith200DMA;
import com.nguyenvo.rest.example.example.domain.TwoHundredDaysMovingAverage;
import com.nguyenvo.rest.example.example.exception.ClosePriceException;
import com.nguyenvo.rest.example.example.service.ClosePriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/v2")
public class ClosePriceController {

    ClosePriceService closePriceService;

    @Autowired
    public ClosePriceController(ClosePriceService closePriceService) {
        this.closePriceService = closePriceService;
    }

    @RequestMapping("/{tickerSymbol}/closePrice")
    public ClosePrice getCloseForTickerInDateRange(@PathVariable("tickerSymbol") String tickerSymbol,
                                                   @RequestParam("startDate") String startDate,
                                                   @RequestParam("endDate") String endDate) throws ClosePriceException {

        return closePriceService.getCloseForTickerInDateRange(tickerSymbol, startDate, endDate);
    }

    @RequestMapping("/{tickerSymbol}/200dma")
    public TwoHundredDaysMovingAverage getMVAForTicker(@PathVariable("tickerSymbol") String tickerSymbol,
                                                       @RequestParam("startDate") String startDate) throws ClosePriceException {

        return closePriceService.getMVAForTicker(tickerSymbol, startDate);
    }

    @RequestMapping("/200dmaList")
    public MultipleTickersWith200DMA getMVAForTickers(@RequestParam("tickers") String tickers,
                                                      @RequestParam("startDate") String startDate) throws ClosePriceException {

        String[] tickerArray = tickers.split(",");

        return closePriceService.getMultipleTickersWith200DMA(Arrays.asList(tickerArray), startDate);
    }
}
