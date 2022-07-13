package com.roadmap.utility.tax_calculation;

public class ZeroVatPrice {
    private Price price;

    ZeroVatPrice(Price newPrice) {
        price = newPrice;
    }

    public Price accept(Tax tax) {
        return tax.visit (this);
    }

    public Price getPrice() {
        return price;
    }
}
