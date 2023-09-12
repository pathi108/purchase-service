package com.wex.purchase.api;

import com.wex.purchase.api.problem.ExchangeRateNotFoundProblem;
import com.wex.purchase.api.problem.PurchaseTransactionNotFoundProblem;
import com.wex.purchase.model.ResponseDefault;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler
        extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value
            = { PurchaseTransactionNotFoundProblem.class, ExchangeRateNotFoundProblem.class })
    protected ResponseEntity<Object> handleNotFoundProblems(
            RuntimeException ex, WebRequest request) {
        ResponseDefault responseDefault = new ResponseDefault();
        responseDefault.setResponseDesc( ex.getMessage());
        responseDefault.setResponseCode(HttpStatus.NOT_FOUND.value() );
        return handleExceptionInternal(ex, responseDefault,
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
    @ExceptionHandler(value
            = { DataAccessException.class })
    protected ResponseEntity<Object> handleDatabaseIssues(
            RuntimeException ex, WebRequest request) {
        ResponseDefault responseDefault = new ResponseDefault();
        responseDefault.setResponseDesc( ex.getMessage());
        responseDefault.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value() );
        return handleExceptionInternal(ex, responseDefault,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }



}