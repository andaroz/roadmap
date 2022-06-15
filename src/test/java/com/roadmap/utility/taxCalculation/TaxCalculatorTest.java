package com.roadmap.utility.taxCalculation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaxCalculatorTest {
    private double netPrice = 1.2;
    private double expectedNetPrice = netPrice;
    private double expectedTax;
    private double expectedGrossPrice;
    private TaxCalculator taxCalculator = new TaxCalculator ();
    private Price price = new Price (1.2);


    @Test
    void visitFullTax() {
        FullVatPrice fullVatPrice = new FullVatPrice (price);
        expectedTax = netPrice * 0.21;
        expectedGrossPrice = expectedNetPrice + expectedTax;
        price = fullVatPrice.accept (taxCalculator);
        assertEquals (expectedNetPrice, price.getNetPrice ());
        assertEquals (expectedTax, price.getTax ());
        assertEquals (expectedGrossPrice, price.getGrossPrice ());
    }

    @Test
    void visitReducedTax() {
        ReducedVatPrice reducedVatPrice = new ReducedVatPrice (price);
        expectedTax = netPrice * 0.05;
        expectedGrossPrice = expectedNetPrice + expectedTax;
        price = reducedVatPrice.accept (taxCalculator);
        assertEquals (expectedNetPrice, price.getNetPrice ());
        assertEquals (expectedTax, price.getTax ());
        assertEquals (expectedGrossPrice, price.getGrossPrice ());
    }

    @Test
    void visitZeroTax() {
        ZeroVatPrice zeroVatPrice = new ZeroVatPrice (price);
        expectedTax = 0.00;
        expectedGrossPrice = expectedNetPrice + expectedTax;
        price = zeroVatPrice.accept (taxCalculator);
        assertEquals (expectedNetPrice, price.getNetPrice ());
        assertEquals (expectedTax, price.getTax ());
        assertEquals (expectedGrossPrice, price.getGrossPrice ());
    }
}