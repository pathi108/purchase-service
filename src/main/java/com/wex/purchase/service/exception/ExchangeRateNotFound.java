package com.wex.purchase.service.exception;

public class ExchangeRateNotFound extends Exception{
    public ExchangeRateNotFound(String errorMessage) {
        super(errorMessage);
    }
}
