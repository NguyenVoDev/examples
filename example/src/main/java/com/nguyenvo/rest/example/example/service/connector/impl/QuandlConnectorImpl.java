package com.nguyenvo.rest.example.example.service.connector.impl;
import com.nguyenvo.rest.example.example.exception.ClosePriceException;
import com.nguyenvo.rest.example.example.service.connector.QuandlConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class QuandlConnectorImpl implements QuandlConnector {

    private RestTemplate restTemplate;
    private String quandlRootUrl;

    @Autowired
    public QuandlConnectorImpl(final  RestTemplate restTemplate, @Value("${app.connector.quandl}") final String quandlRootUrl) {
        this.restTemplate = restTemplate;
        this.quandlRootUrl = quandlRootUrl;
    }

    @Override
    public String getCloseForTickerInDateRange(String tickerSymbol, String startDate, String endDate) throws ClosePriceException {
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
        Map<String, String> params = new HashMap<String, String>();
        params.put("start_date", startDate);
        params.put("end_date", endDate);
        String url = quandlRootUrl + "/" + tickerSymbol + ".json";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        builder.queryParam("start_date", startDate);
        builder.queryParam("end_date", endDate);
        builder.queryParam("order","asc");
        builder.queryParam("api_key","Yz2D2aN_MCwbK9zgxGdN");
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            protected boolean hasError(HttpStatus statusCode){
                return false;
            }
        });
        ResponseEntity<String> response = restTemplate.exchange(builder.build().toUriString(), HttpMethod.GET,httpEntity, String.class);
        if(response.getStatusCode() != HttpStatus.OK){
            throw new ClosePriceException(response.getBody());
        }
        return response.getBody();
    }
}
