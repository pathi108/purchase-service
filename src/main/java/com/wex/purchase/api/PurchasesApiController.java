package com.wex.purchase.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wex.purchase.api.problem.ExchangeRateNotFoundProblem;
import com.wex.purchase.api.problem.PurchaseTransactionNotFoundProblem;
import com.wex.purchase.connector.ExchangeRateClient;
import com.wex.purchase.model.BasePurchaseTransaction;
import com.wex.purchase.model.PurchaseTransaction;

import com.wex.purchase.service.exception.ExchangeRateNotFound;
import com.wex.purchase.service.transcation.PurchaseTransactionService;
import com.wex.purchase.service.transcation.holder.ImmutablePurchaseTransactionHolder;
import com.wex.purchase.service.transcation.holder.PurchaseTransactionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.NoSuchElementException;

@RestController
public class PurchasesApiController implements PurchasesApi {
    @Autowired
    PurchaseTransactionService purchaseTransactionService;
    @Autowired
    ExchangeRateClient exchangeRateClient;

    private static final Logger log = LoggerFactory.getLogger(PurchasesApiController.class);

    @Override
    public ResponseEntity<BasePurchaseTransaction> createPurchase(@Valid BasePurchaseTransaction body) {
        PurchaseTransactionHolder purchaseTransactionHolder= ImmutablePurchaseTransactionHolder
                .builder()
                .description(body.getDescription())
                .transactionDate(body.getTransactionDate())
                .amount(body.getAmount())
                .build();
        PurchaseTransactionHolder newPurchaseTransactionHolder=purchaseTransactionService.createTransAction(purchaseTransactionHolder);
        BasePurchaseTransaction purchaseTransaction = new BasePurchaseTransaction();
        Integer newId=newPurchaseTransactionHolder.id().orElse(BigInteger.ZERO).intValue();
        log.debug("New Purchase Transaction Created "+newId);
        purchaseTransaction = purchaseTransaction.id(newId).description(newPurchaseTransactionHolder.description()).transactionDate(newPurchaseTransactionHolder.transactionDate()).amount(newPurchaseTransactionHolder.amount());
        return  new ResponseEntity<>(purchaseTransaction, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PurchaseTransaction>  getPurchase(Integer purchaseId, @NotNull @Valid String currency) {
        PurchaseTransactionHolder purchaseTransactionHolder= null;
        try {
            purchaseTransactionHolder = purchaseTransactionService.convertTransactionTo(purchaseId, currency);
            PurchaseTransaction purchaseTransaction = new PurchaseTransaction();

            if (purchaseTransactionHolder.id().isPresent()) {
                purchaseTransaction.id(purchaseTransactionHolder.id().orElse(BigInteger.ZERO).intValue());
            }
            if (purchaseTransactionHolder.exchangeRate().isPresent()) {
                purchaseTransaction.exchangeRate(BigDecimal.valueOf(purchaseTransactionHolder.exchangeRate().get()));
            }
            if (purchaseTransactionHolder.convertedAmmount().isPresent()) {
                purchaseTransaction.convertedAmmount(purchaseTransactionHolder.convertedAmmount().get());
            }


            purchaseTransaction
                    .id(purchaseTransactionHolder.id().orElse(BigInteger.ZERO).intValue())
                    .description(purchaseTransactionHolder.description())
                    .transactionDate(purchaseTransactionHolder.transactionDate())
                    .amount(purchaseTransactionHolder.amount());
            return new ResponseEntity<>(purchaseTransaction, HttpStatus.OK);
        }
        catch (JsonProcessingException  e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage(),e);
        }
        catch (NoSuchElementException  e) {
            throw new PurchaseTransactionNotFoundProblem(purchaseId);
        }
        catch ( ExchangeRateNotFound e) {
            throw new ExchangeRateNotFoundProblem(e.getMessage());
        }
    }



}
