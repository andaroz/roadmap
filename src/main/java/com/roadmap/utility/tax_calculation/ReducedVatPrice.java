package com.roadmap.utility.tax_calculation;

public class ReducedVatPrice {
    private Price price;

    public ReducedVatPrice(Price newPrice) {
        price = newPrice;
    }

    public Price accept(Tax tax) {
        return tax.visit (this);
    }

    public Price getPrice() {
        return price;
    }
}
