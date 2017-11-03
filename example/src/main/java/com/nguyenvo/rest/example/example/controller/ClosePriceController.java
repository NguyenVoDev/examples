package com.nguyenvo.rest.example.example.controller;

import com.nguyenvo.rest.example.example.domain.ClosePrice;
import com.nguyenvo.rest.example.example.service.ClosePriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
                                                   @RequestParam("endDate") String endDate) {

        System.out.println(tickerSymbol + "---" + startDate + "---" + endDate);
        return closePriceService.getCloseForTickerInDateRange(tickerSymbol, startDate, endDate);
    }
}
