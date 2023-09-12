package com.wex.purchase.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wex.purchase.connector.ExchangeRateClient;
import com.wex.purchase.connector.model.ExchangeRate;
import com.wex.purchase.model.PurchaseTransaction;
import com.wex.purchase.service.transcation.PurchaseTransactionService;
import com.wex.purchase.service.transcation.holder.ImmutablePurchaseTransactionHolder;
import com.wex.purchase.service.transcation.holder.PurchaseTransactionHolder;
import io.swagger.v3.core.util.Json;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootTest
@AutoConfigureMockMvc
public class PurchaseTransactionApiTest {

    @Autowired
    private MockMvc mockMvc;



    @Autowired
    PurchaseTransactionService purchaseTransactionService;

    @MockBean
    ExchangeRateClient exchangeRateClient;



    @Test
    public void testCreatePurchaseTransaction() throws Exception {


        PurchaseTransaction purchaseTransaction = new PurchaseTransaction()
                .transactionDate(LocalDate.now())
                .description("A subsidiary")
                .amount(BigDecimal.valueOf(21.0));
        String request = Json.pretty(purchaseTransaction);
        purchaseTransaction.setId(1);
        String response=  Json.pretty(purchaseTransaction);



        mockMvc.perform( post("/purchases")
                .content(request)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(response));
    }

    @Test
    public void testDescriptionMoreThan50Characters() throws Exception {

        PurchaseTransaction purchaseTransaction = new PurchaseTransaction()
                .transactionDate(LocalDate.now())
                .description("Your application must be able to accept and store (i.e., persist)")
                .amount(BigDecimal.valueOf(21.0));
        String request = Json.pretty(purchaseTransaction);


        mockMvc.perform( post("/purchases")
                .content(request)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

    }

    @Test
    public void testCurrencyConversationNotExist() throws Exception {
        String response = "{\"responseCode\":404,\"responseDesc\":\"Not found: Purchase Transaction 2 Notfound\"}";
        mockMvc.perform( get("/purchases/2")
                .queryParam("currency","Canada-Dollar")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(response));

    }

    @Test
    public void testExchangeNotExist() throws Exception {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse("2023-09-11", formatter);
        PurchaseTransactionHolder purchaseTransactionServiceTransAction = purchaseTransactionService.createTransAction(ImmutablePurchaseTransactionHolder
                .builder()
                .description("Food")
                .transactionDate(date)
                .amount(BigDecimal.valueOf(12.0))
                .build());
        when(exchangeRateClient.getExchangeRateOf(any(),any(),any())).thenReturn(new ExchangeRate[]{});
        String response = "{\"responseCode\":404,\"responseDesc\":\"Not found: Exchange rate nof found for currency Canada-Dollar on date 2023-09-11\"}";
        mockMvc.perform( get("/purchases/"+purchaseTransactionServiceTransAction.id().orElse(BigInteger.ONE))
                .queryParam("currency","Canada-Dollar")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(response));

    }


}
