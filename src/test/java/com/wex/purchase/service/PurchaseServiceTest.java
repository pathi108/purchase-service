package com.wex.purchase.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wex.purchase.connector.ExchangeRateClient;
import com.wex.purchase.connector.model.ExchangeRate;
import com.wex.purchase.service.exception.ExchangeRateNotFound;
import com.wex.purchase.service.transcation.PurchaseTransactionService;
import com.wex.purchase.service.transcation.db.PurchaseTransaction;
import com.wex.purchase.service.transcation.db.PurchaseTransactionRepository;
import com.wex.purchase.service.transcation.holder.ImmutablePurchaseTransactionHolder;
import com.wex.purchase.service.transcation.holder.PurchaseTransactionHolder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PurchaseServiceTest {
    @Autowired
    PurchaseTransactionService purchaseTransactionService;

    @MockBean
    private PurchaseTransactionRepository purchaseTransactionRepository;

    @MockBean
    ExchangeRateClient exchangeRateClient;

    PurchaseTransaction purchaseTransaction = new PurchaseTransaction();
    PurchaseTransactionHolder purchaseTransactionHolder;

    ExchangeRate exchangeRate = new ExchangeRate();


    @Before
    public void setupTests(){
        purchaseTransaction.setId(1L);
        purchaseTransaction.setDescription("Food");
        purchaseTransaction.setTransactionDate(LocalDate.now());
        purchaseTransaction.setAmount(BigDecimal.valueOf(12.0));

        purchaseTransactionHolder =ImmutablePurchaseTransactionHolder
                .builder()
                .description("Food")
                .transactionDate(LocalDate.now())
                .amount(BigDecimal.valueOf(12.0))
                .build();

        exchangeRate.setCountryCurrencyDesc("Canada-Dollar");
        exchangeRate.setExchangeRate(1.26);
        exchangeRate.setDate("2023-03-31");

    }




    @Test
    public void testCreatePurchaseTransaction(){




        when(purchaseTransactionRepository.save(any())).thenReturn(purchaseTransaction);
        purchaseTransactionService.createTransAction( purchaseTransactionHolder);

        Mockito.verify(purchaseTransactionRepository, Mockito.times(1)).save(any());
    }

    @Test
    public void testConvertTransActionTo() throws JsonProcessingException, ExchangeRateNotFound {

        when(purchaseTransactionRepository.findById(anyLong())).thenReturn(Optional.of(purchaseTransaction));
        when(exchangeRateClient.getExchangeRateOf(any(),any(),any())).thenReturn(new ExchangeRate[]{exchangeRate});

        PurchaseTransactionHolder purchaseTransactionHolder = purchaseTransactionService.convertTransactionTo(1,"Canada-Dollar");
        Assert.assertEquals(purchaseTransactionHolder.convertedAmmount().orElse(BigDecimal.ZERO),BigDecimal.valueOf(15.12));
        Assert.assertEquals(purchaseTransactionHolder.exchangeRate().orElse(0.0),1.26,0);

    }

    @Test
    public void testConvertTransActionToNotFoundId() throws JsonProcessingException, ExchangeRateNotFound {

        when(purchaseTransactionRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(exchangeRateClient.getExchangeRateOf(any(),any(),any())).thenReturn(new ExchangeRate[]{exchangeRate});

        Assert.assertThrows(NoSuchElementException.class, ()->purchaseTransactionService.convertTransactionTo(2,"Canada-Dollar"));

    }


    @Test
    public void testConvertTransActionToNotFoundExchangeRate() throws JsonProcessingException, ExchangeRateNotFound {

        when(purchaseTransactionRepository.findById(anyLong())).thenReturn(Optional.of(purchaseTransaction));
        when(exchangeRateClient.getExchangeRateOf(any(),any(),any())).thenReturn(new ExchangeRate[]{});
        Assert.assertThrows(ExchangeRateNotFound.class, ()->purchaseTransactionService.convertTransactionTo(2,"Canada-Dollar"));

    }

}
