package com.roadmap.utility.taxCalculation;

public interface TaxVisitable {
    Price accept(Tax tax);
}
