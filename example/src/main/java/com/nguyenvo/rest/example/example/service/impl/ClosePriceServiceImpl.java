package com.nguyenvo.rest.example.example.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nguyenvo.rest.example.example.domain.ClosePrice;
import com.nguyenvo.rest.example.example.domain.Price;
import com.nguyenvo.rest.example.example.domain.TwoHundredDaysMovingAverage;
import com.nguyenvo.rest.example.example.domain.TwoHundredDaysMovingAverageValue;
import com.nguyenvo.rest.example.example.service.ClosePriceService;
import com.nguyenvo.rest.example.example.service.connector.QuandlConnector;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;

@Service
public class ClosePriceServiceImpl implements ClosePriceService {

    QuandlConnector quandlConnector;
    private String dataFormatterString;

    private static final int MOVING_AVARAGE_DAYS_NUM = 200;
    private static final int CLOSE_INDEX = 4;

    @Autowired
    public ClosePriceServiceImpl(QuandlConnector quandlConnector, @Value("${app.date-formatter}") final String dataFormatterString) {
        this.quandlConnector = quandlConnector;
        this.dataFormatterString = dataFormatterString;
    }

    @Override
    public ClosePrice getCloseForTickerInDateRange(String tickerSymbol, String startDate, String endDate) {
        String responseFromQuandl = quandlConnector.getCloseForTickerInDateRange(tickerSymbol, startDate, endDate);
        return buildClosePriceFromResponse(responseFromQuandl, tickerSymbol);
    }

    @Override
    public TwoHundredDaysMovingAverage getMVAForTicker(String tickerSymbol, String startDateString) {
        DateFormat df = new SimpleDateFormat(dataFormatterString);
        Date startDate;
        try {
            startDate = df.parse(startDateString);
        } catch (ParseException e) {
            throw new RuntimeException("Wrong format date for startDate: " + startDateString);
        }

        Date endDate =  DateUtils.addDays(startDate, MOVING_AVARAGE_DAYS_NUM - 1);
        String endDateString = df.format(endDate);

        String responseFromQuandl = quandlConnector.getCloseForTickerInDateRange(tickerSymbol, startDateString, endDateString);
        return build200MVAFromRespose(responseFromQuandl,tickerSymbol);
    }

    private TwoHundredDaysMovingAverage build200MVAFromRespose(String responseFromQuandl, String tickerSymbol) {
        TwoHundredDaysMovingAverage twoHundredDaysMovingAverage = new TwoHundredDaysMovingAverage();
        TwoHundredDaysMovingAverageValue twoHundredDaysMovingAverageValue = new TwoHundredDaysMovingAverageValue();
        twoHundredDaysMovingAverageValue.setTicker(tickerSymbol);
        double totalClose = 0;
        for (Iterator<Entry<String, JsonNode>> iter = getDataNodeFromQuandlResponse(responseFromQuandl).fields(); iter.hasNext(); ) {
            Entry<String, JsonNode> entry = iter.next();
            totalClose =  totalClose + entry.getValue().get(CLOSE_INDEX).asDouble();
        }
        twoHundredDaysMovingAverageValue.setAverage(String.valueOf(totalClose/MOVING_AVARAGE_DAYS_NUM));
        twoHundredDaysMovingAverage.setValue(twoHundredDaysMovingAverageValue);
        return twoHundredDaysMovingAverage;
    }

    private ClosePrice buildClosePriceFromResponse(final String responseFromQuandl, final String tickerSymbol) {
        ClosePrice closePrice = new ClosePrice();
        Price prices = new Price();
        prices.setTicker(tickerSymbol);
        getDataNodeFromQuandlResponse(responseFromQuandl).forEach(e -> {
            prices.getDateClose().add(buildDateClose(e.get(0).textValue(),e.get(CLOSE_INDEX).toString()));
        });
        closePrice.setPrice(prices);
        return closePrice;
    }
    private JsonNode getDataNodeFromQuandlResponse(String responseFromQuandl){
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(responseFromQuandl);

            return rootNode.get("dataset").findValue("data");

        } catch (IOException e) {
            throw new RuntimeException("Could not process reponse from Quandl");
        }
    }
    private String[] buildDateClose(final String date, final String closePriceString) {
        String[] dateClose = new String[2];
        dateClose[0] = date;
        dateClose[1] = closePriceString;
        return dateClose;
    }
}
