package com.roadmap.utility.taxCalculation;

public class FullVatPrice implements TaxVisitable {

    private Price price;

    public FullVatPrice(Price calculatedTax) {
        price = calculatedTax;
    }

    public Price accept(Tax tax) {
        return tax.visit (this);
    }

    public Price getPrice() {
        return price;
    }
}
