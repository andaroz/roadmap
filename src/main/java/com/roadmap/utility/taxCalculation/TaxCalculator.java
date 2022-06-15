package com.roadmap.utility.taxCalculation;

import com.roadmap.utility.CommonConstants;

import java.text.DecimalFormat;

public class TaxCalculator implements Tax {
    private static final DecimalFormat df = new DecimalFormat (CommonConstants.DECIMAL_FOORMAT_PATTERN);

    @Override
    public Price visit(FullVatPrice fullVatPrice) {
        Price price = new Price ();
        Double netoPrice = fullVatPrice.getPrice ().getNetPrice ();
        Double tax = netoPrice * 0.21;
        price.setNetPrice (netoPrice);
        price.setTax (Double.valueOf (df.format (tax)));
        price.setGrossPrice (Double.valueOf (df.format(netoPrice + tax)));
        return price;
    }

    @Override
    public Price visit(ReducedVatPrice reducedVatPrice) {
        Price price = new Price ();
        Double netoPrice = reducedVatPrice.getPrice ().getNetPrice ();
        Double tax = netoPrice * 0.05;
        price.setNetPrice (netoPrice);
        price.setTax (Double.valueOf (df.format(tax)));
        price.setGrossPrice (Double.valueOf (df.format(netoPrice + tax)));
        return price;
    }

    @Override
    public Price visit(ZeroVatPrice zeroVatPrice) {
        Price price = new Price ();
        Double netoPrice = zeroVatPrice.getPrice ().getNetPrice ();
        Double tax = 0.0;
        price.setNetPrice (netoPrice);
        price.setTax (Double.valueOf (df.format(tax)));
        price.setGrossPrice (Double.valueOf (df.format(netoPrice + tax)));
        return price;
    }
}
