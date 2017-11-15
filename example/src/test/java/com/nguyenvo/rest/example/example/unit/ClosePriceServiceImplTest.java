package com.nguyenvo.rest.example.example.unit;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

import com.nguyenvo.rest.example.example.domain.ClosePrice;
import com.nguyenvo.rest.example.example.domain.Price;
import com.nguyenvo.rest.example.example.exception.ClosePriceException;
import com.nguyenvo.rest.example.example.service.ClosePriceService;
import com.nguyenvo.rest.example.example.service.connector.QuandlConnector;
import com.nguyenvo.rest.example.example.service.impl.ClosePriceServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Matchers;
import org.mockito.Mock;

@RunWith(JUnit4.class)
public class ClosePriceServiceImplTest {

    @Mock
    QuandlConnector quandlConnector;

    ClosePriceService closePriceService;

    @Before
    public void setUp() {
        initMocks(this);
        closePriceService = new ClosePriceServiceImpl(quandlConnector,"yyyy-mm-dd");
    }

    @Test
    public void testGetCloseForTickerInDateRange() throws ClosePriceException {
        String mockData = "{\"dataset\":{\"id\":9775709,\"dataset_code\":\"GE\",\"database_code\":\"WIKI\",\"name\":\"General Electric Co (GE) Prices, Dividends, Splits and Trading Volume\",\"description\":\"End of day open, high, low, close and volume, dividends and splits, and split/dividend adjusted open, high, low close and volume for General Electric Company (GE). Ex-Dividend is non-zero on ex-dividend dates. Split Ratio is 1 on non-split dates. Adjusted prices are calculated per CRSP (www.crsp.com/products/documentation/crsp-calculations)\\n\\nThis data is in the public domain. You may copy, distribute, disseminate or include the data in other products for commercial and/or noncommercial purposes.\\n\\nThis data is part of Quandl's Wiki initiative to get financial data permanently into the public domain. Quandl relies on users like you to flag errors and provide data where data is wrong or missing. Get involved: connect@quandl.com\\n\",\"refreshed_at\":\"2017-11-02T21:48:41.335Z\",\"newest_available_date\":\"2017-11-02\",\"oldest_available_date\":\"1962-01-02\",\"column_names\":[\"Date\",\"Open\",\"High\",\"Low\",\"Close\",\"Volume\",\"Ex-Dividend\",\"Split Ratio\",\"Adj. Open\",\"Adj. High\",\"Adj. Low\",\"Adj. Close\",\"Adj. Volume\"],\"frequency\":\"daily\",\"type\":\"Time Series\",\"premium\":false,\"limit\":null,\"transform\":null,\"column_index\":null,\"start_date\":\"1999-09-29\",\"end_date\":\"1999-09-30\",\"data\":[[\"1999-09-30\",117.12,119.94,115.37,118.56,6805700.0,0.0,1.0,22.588921006709,23.132814084228,22.251398706831,22.866653642038,20417100.0],[\"1999-09-29\",117.69,118.94,116.37,116.44,5044200.0,0.0,1.0,22.698856841527,22.939944198583,22.444268592476,22.457769484471,15132600.0]],\"collapse\":null,\"order\":null,\"database_id\":4922}}";
        when(quandlConnector.getCloseForTickerInDateRange(Matchers.anyString(), Matchers.anyString(), Matchers.anyString())).thenReturn(mockData);
        ClosePrice closePrice = closePriceService.getCloseForTickerInDateRange("GE", "1999-09-29", "1999-09-30");
        Price price  = closePrice.getPrice();
        assertEquals(price.getTicker(), "GE");
        assertEquals(price.getDateClose().get(1)[0], "1999-09-29");
        assertEquals(price.getDateClose().get(1)[1], "116.44");
    }
}
