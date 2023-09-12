package com.wex.purchase.connector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wex.purchase.connector.model.ExchangeRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Rest Client used to retrieve exchange rates
 */
@Component
public class ExchangeRateClient {

    String url = "https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange?fields=country_currency_desc,exchange_rate,record_date&filter=country_currency_desc:in:(%s),record_date:gte:%s,record_date:lte:%s&sort=-record_date";
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final Logger log = LoggerFactory.getLogger(ExchangeRateClient.class);


    public  ExchangeRate[] getExchangeRateOf(String countryCurrencyDesc, LocalDate startDate, LocalDate endDate) throws JsonProcessingException {
        String formattedStartDate = dtf.format(startDate);
        String formattedEndDate = dtf.format(endDate);
        RestTemplate restTemplate = new RestTemplate();
        String formattedUrl = String.format(url, countryCurrencyDesc,formattedStartDate,formattedEndDate);
        log.info(formattedUrl);
        ResponseEntity<String> response
                = restTemplate.getForEntity(formattedUrl , String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        JsonNode data = root.get("data");
        ExchangeRate[] rates = mapper.readValue(data.toString(), ExchangeRate[].class);
        log.info(data.toString());
        return rates;
    }
}
