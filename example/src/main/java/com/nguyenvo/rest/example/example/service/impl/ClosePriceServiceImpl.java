package com.nguyenvo.rest.example.example.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nguyenvo.rest.example.example.domain.ClosePrice;
import com.nguyenvo.rest.example.example.domain.MultipleTickersWith200DMA;
import com.nguyenvo.rest.example.example.domain.Price;
import com.nguyenvo.rest.example.example.domain.TwoHundredDaysMovingAverage;
import com.nguyenvo.rest.example.example.domain.TwoHundredDaysMovingAverageValue;
import com.nguyenvo.rest.example.example.exception.ClosePriceException;
import com.nguyenvo.rest.example.example.service.ClosePriceService;
import com.nguyenvo.rest.example.example.service.connector.QuandlConnector;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ClosePriceServiceImpl implements ClosePriceService {

    QuandlConnector quandlConnector;

    private String dataFormatterString;

    private static final int MOVING_AVARAGE_DAYS_NUM = 200;

    private static final int CLOSE_INDEX = 4;

    private static final int TOTAL_TICKER_ELEMENTS_ON_MEMORY = 4;
    private static final int LAST_INDEX = TOTAL_TICKER_ELEMENTS_ON_MEMORY - 1;

    List<String> cachedTickers = new CopyOnWriteArrayList<String>();

    Map<String, Map> cachedDataOfTicketMap = new ConcurrentHashMap<>();


    @Autowired
    public ClosePriceServiceImpl(QuandlConnector quandlConnector, @Value("${app.date-formatter}") final String dataFormatterString) {
        this.quandlConnector = quandlConnector;
        this.dataFormatterString = dataFormatterString;
    }

    @Override
    public ClosePrice getCloseForTickerInDateRange(String tickerSymbol, String startDate, String endDate) throws ClosePriceException {
        ClosePrice cachedData = getFromCache(tickerSymbol, startDate, endDate);
        if (cachedData != null) {
            System.out.println("get from cache for: ticker" + tickerSymbol + " StartDate: " + startDate + " EndDate: " + endDate);
            return cachedData;
        }

        ClosePrice closePriceFromQuandl = getFromQuandl(tickerSymbol, startDate, endDate);
        cache(tickerSymbol, startDate, endDate, closePriceFromQuandl);
        return closePriceFromQuandl;
    }

    private ClosePrice getFromQuandl(String tickerSymbol, String startDate, String endDate) throws ClosePriceException {
        String responseFromQuandl = quandlConnector.getCloseForTickerInDateRange(tickerSymbol, startDate, endDate);
        return buildClosePriceFromResponse(responseFromQuandl, tickerSymbol);
    }

    @Override
    public TwoHundredDaysMovingAverage getMVAForTicker(String tickerSymbol, String startDateString) throws ClosePriceException {
        DateFormat df = new SimpleDateFormat(dataFormatterString);
        Date startDate;
        try {
            startDate = df.parse(startDateString);
        } catch (ParseException e) {
            throw new RuntimeException("Wrong format date for startDate: " + startDateString);
        }

        Date endDate = DateUtils.addDays(startDate, MOVING_AVARAGE_DAYS_NUM - 1);
        String endDateString = df.format(endDate);

        String responseFromQuandl = quandlConnector.getCloseForTickerInDateRange(tickerSymbol, startDateString, endDateString);
        return build200MVAFromRespose(responseFromQuandl, tickerSymbol);
    }

    @Override
    public MultipleTickersWith200DMA getMultipleTickersWith200DMA(List<String> tickers, String startDate) {
        MultipleTickersWith200DMA result = new MultipleTickersWith200DMA();
        List<Thread> threadList = new ArrayList<>();
        System.out.println("========= Start with with thread mechanism :" + new Date() + "========");
        for (String ticker : tickers) {
            Runnable quandlRunnable = () -> {
                try {
                    TwoHundredDaysMovingAverage twoHundredDaysMovingAverage = getMVAForTicker(ticker, startDate);
                    result.getTwoHundredDaysMovingAverages().add(twoHundredDaysMovingAverage.getValue());
                } catch (ClosePriceException e) {
                    result.getErrorTickers().add("Invalid tickers - " + ticker);
                }
            };

            Thread quandlThread = new Thread(quandlRunnable);
            quandlThread.start();
            threadList.add(quandlThread);
        }
        for (Thread thread : threadList) {
            synchronized (thread) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
//        System.out.println("========= End with with thread mechanism :" + new Date() + "========");
//
//
//        MultipleTickersWith200DMA result1 = new MultipleTickersWith200DMA();
//        System.out.println("========= Start with with NON thread mechanism :" + new Date() + "========");
//        for (String ticker : tickers) {
//            try {
//                TwoHundredDaysMovingAverage twoHundredDaysMovingAverage = getMVAForTicker(ticker, startDate);
//                result1.getTwoHundredDaysMovingAverages().add(twoHundredDaysMovingAverage.getValue());
//            } catch (ClosePriceException e) {
//                result1.getErrorTickers().add("Invalid tickers - " + ticker);
//            }
//        }
//        System.out.println("========= End with with NON thread mechanism :" + new Date() + "========");

        return result;
    }

    private TwoHundredDaysMovingAverage build200MVAFromRespose(String responseFromQuandl, String tickerSymbol) throws ClosePriceException {
        TwoHundredDaysMovingAverage twoHundredDaysMovingAverage = new TwoHundredDaysMovingAverage();
        TwoHundredDaysMovingAverageValue twoHundredDaysMovingAverageValue = new TwoHundredDaysMovingAverageValue();
        twoHundredDaysMovingAverageValue.setTicker(tickerSymbol);
        double totalClose = 0;


        for (Iterator<JsonNode> iter = ((ArrayNode) getDataNodeFromQuandlResponse(responseFromQuandl)).elements(); iter.hasNext(); ) {
            JsonNode element = iter.next();
            totalClose = totalClose + element.get(CLOSE_INDEX).asDouble();
        }
        twoHundredDaysMovingAverageValue.setAverage(String.valueOf(totalClose / MOVING_AVARAGE_DAYS_NUM));
        twoHundredDaysMovingAverage.setValue(twoHundredDaysMovingAverageValue);
        return twoHundredDaysMovingAverage;
    }

    private ClosePrice buildClosePriceFromResponse(final String responseFromQuandl, final String tickerSymbol) throws ClosePriceException {
        ClosePrice closePrice = new ClosePrice();
        Price prices = new Price();
        prices.setTicker(tickerSymbol);
        getDataNodeFromQuandlResponse(responseFromQuandl).forEach(e -> {
            prices.getDateClose().add(buildDateClose(e.get(0).textValue(), e.get(CLOSE_INDEX).toString()));
        });
        closePrice.setPrice(prices);
        return closePrice;
    }

    private JsonNode getDataNodeFromQuandlResponse(String responseFromQuandl) throws ClosePriceException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(responseFromQuandl);

            return rootNode.get("dataset").findValue("data");

        } catch (IOException e) {
            throw new ClosePriceException("Could not process reponse from Quandl");
        }
    }

    private String[] buildDateClose(final String date, final String closePriceString) {
        String[] dateClose = new String[2];
        dateClose[0] = date;
        dateClose[1] = closePriceString;
        return dateClose;
    }

    private void cache(String ticker, String startDate, String endDate, ClosePrice data) {
        if(data == null || getFromCache(ticker, startDate, endDate) != null){
            return;
        }
        if (cachedTickers.contains(ticker)) {
            Collections.swap(cachedTickers, 0, cachedTickers.indexOf(ticker));
        } else {
            if (cachedTickers.size() == TOTAL_TICKER_ELEMENTS_ON_MEMORY) {
                evitLastOne();
            }
            cachedTickers.add(0, ticker);
        }
        putData(cachedDataOfTicketMap, ticker, buildKeyOfCachedValue(startDate, endDate), data);
    }

    private void evitLastOne() {
        final String lastOne = cachedTickers.get(LAST_INDEX);
        cachedDataOfTicketMap.remove(lastOne);
        cachedTickers.remove(LAST_INDEX);
    }

    private String buildKeyOfCachedValue(String startDate, String endDate) {
        return startDate + "-" + endDate;
    }

    private void putData(Map<String, Map> cachedDataOfTicketMap, String ticker, String s, ClosePrice data) {
        Map<String, ClosePrice> cachedValueMap = cachedDataOfTicketMap.get(ticker);
        if (cachedValueMap == null) {
            cachedValueMap = new HashMap<>();
        }
        cachedValueMap.put(s, data);
        cachedDataOfTicketMap.put(ticker, cachedValueMap);
    }

    private ClosePrice getFromCache(String ticker, String startDate, String endDate) {
        if (!cachedTickers.contains(ticker)) {
            return null;
        }
        Object fromMap = cachedDataOfTicketMap.get(ticker).get(buildKeyOfCachedValue(startDate, endDate));
        if (!(fromMap instanceof ClosePrice)) {
            return null;
        }
        return (ClosePrice) fromMap;
    }
}