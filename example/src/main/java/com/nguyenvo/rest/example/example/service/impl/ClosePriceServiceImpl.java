package com.nguyenvo.rest.example.example.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nguyenvo.rest.example.example.domain.ClosePrice;
import com.nguyenvo.rest.example.example.domain.Price;
import com.nguyenvo.rest.example.example.service.ClosePriceService;
import com.nguyenvo.rest.example.example.service.connector.QuandlConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ClosePriceServiceImpl implements ClosePriceService {

    QuandlConnector quandlConnector;

    @Autowired
    public ClosePriceServiceImpl(QuandlConnector quandlConnector) {
        this.quandlConnector = quandlConnector;
    }

    @Override
    public ClosePrice getCloseForTickerInDateRange(String tickerSymbol, String startDate, String endDate) {
        String responseFromQuandl = quandlConnector.getCloseForTickerInDateRange(tickerSymbol, startDate, endDate);
        return buildClosePriceFromResponse(responseFromQuandl, tickerSymbol);
    }

    private ClosePrice buildClosePriceFromResponse(final String responseFromQuandl, final String tickerSymbol) {
        ClosePrice closePrice = new ClosePrice();
        Price prices = new Price();
        prices.setTicker(tickerSymbol);

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(responseFromQuandl);

            JsonNode dataNode = rootNode.get("dataset").findValue("data");

            dataNode.forEach(e -> {
                prices.getDateClose().add(buildDateClose(e.get(0).textValue(),e.get(4).toString()));
            });

        } catch (IOException e) {
            throw new RuntimeException("Could not process reponse from Quandl");
        }
        closePrice.setPrice(prices);
        return closePrice;
    }

    private String[] buildDateClose(final String date, final String closePriceString) {
        String[] dateClose = new String[2];
        dateClose[0] = date;
        dateClose[1] = closePriceString;
        return dateClose;
    }
}
