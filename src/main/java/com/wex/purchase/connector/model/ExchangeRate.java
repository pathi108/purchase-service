package com.wex.purchase.connector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
@Data
public class ExchangeRate implements Serializable {
    @JsonProperty("country_currency_desc")
    private String countryCurrencyDesc;

    @JsonProperty("exchange_rate")
    private Double exchangeRate;

    @JsonProperty("record_date")
    private String date;

}
