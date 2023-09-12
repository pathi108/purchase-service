package com.wex.purchase.api.problem;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class PurchaseTransactionNotFoundProblem extends AbstractThrowableProblem {

    private static final URI TYPE
            = URI.create("https://example.org/not-found");

    public PurchaseTransactionNotFoundProblem(Integer id) {
        super(
                TYPE,
                "Not found",
                Status.NOT_FOUND,
                String.format("Purchase Transaction %d Notfound",id));
    }
}
