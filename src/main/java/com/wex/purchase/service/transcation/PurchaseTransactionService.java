package com.wex.purchase.service.transcation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wex.purchase.connector.ExchangeRateClient;
import com.wex.purchase.connector.model.ExchangeRate;
import com.wex.purchase.service.exception.ExchangeRateNotFound;
import com.wex.purchase.service.transcation.db.PurchaseTransaction;
import com.wex.purchase.service.transcation.db.PurchaseTransactionRepository;
import com.wex.purchase.service.transcation.holder.ImmutablePurchaseTransactionHolder;
import com.wex.purchase.service.transcation.holder.PurchaseTransactionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class PurchaseTransactionService {
    @Autowired
    private PurchaseTransactionRepository purchaseTransactionRepository;

    @Autowired
    ExchangeRateClient exchangeRateClient;
    private static final Logger log = LoggerFactory.getLogger(PurchaseTransactionService.class);

    /**
     *
     * @param purchaseTransactionHolder purchase TransAction that we need to save
     * @return the purchased transaction that was saved with its unique id assigned by the system
     */
    public PurchaseTransactionHolder createTransAction(PurchaseTransactionHolder purchaseTransactionHolder) {
        PurchaseTransaction purchaseTransaction = new PurchaseTransaction();
        purchaseTransaction.setDescription(purchaseTransactionHolder.description());
        purchaseTransaction.setTransactionDate(purchaseTransactionHolder.transactionDate());
        purchaseTransaction.setAmount(purchaseTransactionHolder.amount());
        PurchaseTransaction createdPurchaseTransaction = purchaseTransactionRepository.save(purchaseTransaction);
        return ImmutablePurchaseTransactionHolder
                .builder()
                .id(BigInteger.valueOf(createdPurchaseTransaction.getId()))
                .description(createdPurchaseTransaction.getDescription())
                .transactionDate(createdPurchaseTransaction.getTransactionDate())
                .amount(BigDecimal.valueOf(createdPurchaseTransaction.getAmount().doubleValue()))
                .build();

    }

    /**
     *
     * @param purchaseId The purchaseTransactionId we need to convert the amount
     * @param currency The new currency which we need the converted amount
     * @return
     * @throws JsonProcessingException
     * @throws ExchangeRateNotFound When the Exchange rate is not found for  given time reason
     */

    public PurchaseTransactionHolder convertTransactionTo(Integer purchaseId, String currency) throws JsonProcessingException, ExchangeRateNotFound {
        Optional<PurchaseTransaction> selected = purchaseTransactionRepository.findById(Long.valueOf(purchaseId));
        log.info("Loaded Transaction "+purchaseId+"from db");
        PurchaseTransaction selectedTransaction = selected.orElseThrow(NoSuchElementException::new);
        LocalDate endDate = selectedTransaction.getTransactionDate();
        LocalDate startDate = endDate.minusMonths(6);
        ExchangeRate[] exchangeRates = exchangeRateClient.getExchangeRateOf(currency, startDate, endDate);
        log.info("Got  "+exchangeRates.length+"for currency"+currency);
        Double exchangeRate = 1.0;
        if (exchangeRates.length > 0) {
            exchangeRate = exchangeRates[0].getExchangeRate();
        } else {
            log.error("Exchange rate nof found for currency " + currency + " on date " + endDate.toString());
            throw new ExchangeRateNotFound("Exchange rate nof found for currency " + currency + " on date " + endDate.toString());


        }
        BigDecimal convertedAmmount = selectedTransaction.getAmount().multiply(BigDecimal.valueOf(exchangeRate)).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        log.info("Converted Value is : "+ convertedAmmount.toString());
        return ImmutablePurchaseTransactionHolder
                .builder()
                .id(BigInteger.valueOf(selectedTransaction.getId()))
                .description(selectedTransaction.getDescription())
                .transactionDate(selectedTransaction.getTransactionDate())
                .amount(selectedTransaction.getAmount())
                .convertedAmmount(convertedAmmount)
                .exchangeRate(exchangeRate)
                .build();
    }


}

