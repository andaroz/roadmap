package com.roadmap.utility.tax_calculation;

public interface TaxVisitable {
    Price accept(Tax tax);
}
