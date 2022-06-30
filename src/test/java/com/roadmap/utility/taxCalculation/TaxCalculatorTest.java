package com.roadmap.utility.taxCalculation;

import com.roadmap.utility.CommonConstants;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.DecimalFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaxCalculatorTest {
    private double netPrice = 1.2;
    private double expectedNetPrice = netPrice;
    private double expectedTax;
    private double expectedGrossPrice;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat (CommonConstants.DECIMAL_FOORMAT_PATTERN);
    private TaxCalculator taxCalculator = new TaxCalculator ();
    private Price price = new Price (netPrice);

    TaxCalculatorTest() throws IOException {
    }


    @Test
    void visitFullTax() {
        FullVatPrice fullVatPrice = new FullVatPrice (price);
        expectedTax = Double.valueOf (DECIMAL_FORMAT.format (netPrice * 0.21));
        expectedGrossPrice = expectedNetPrice + expectedTax;
        price = fullVatPrice.accept (taxCalculator);
        assertEquals (expectedNetPrice, price.getNetPrice ());
        assertEquals (expectedTax, price.getTax ());
        assertEquals (expectedGrossPrice, price.getGrossPrice ());
    }

    @Test
    void visitReducedTax() {
        ReducedVatPrice reducedVatPrice = new ReducedVatPrice (price);
        expectedTax = Double.valueOf (DECIMAL_FORMAT.format (netPrice * 0.05));
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