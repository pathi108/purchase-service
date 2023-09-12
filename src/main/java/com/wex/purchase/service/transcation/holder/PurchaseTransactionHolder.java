package com.wex.purchase.service.transcation.holder;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Value.Immutable
public interface PurchaseTransactionHolder {

   Optional<BigInteger> id();
   String description();
   LocalDate transactionDate();
   BigDecimal amount();
   Optional<Double> exchangeRate();
   Optional<BigDecimal> convertedAmmount();

}
