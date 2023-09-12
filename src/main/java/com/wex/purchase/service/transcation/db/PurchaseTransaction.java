package com.wex.purchase.service.transcation.db;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;

@Data
@Entity
@Table(name = "purchase")
public class PurchaseTransaction {
    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(name = "DESCRIPTION",nullable = false, length = 50)
    private String description;

    @Basic
    @Column(name = "TRANSACTIONDATE",nullable = false)
    private LocalDate transactionDate;
    @Basic
    @Column(name = "AMOUNT",nullable = false)
    private BigDecimal amount;
}
