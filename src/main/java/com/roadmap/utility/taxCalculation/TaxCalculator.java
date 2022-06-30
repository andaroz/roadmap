package com.roadmap.utility.taxCalculation;

import com.roadmap.config.PropertiesLoader;
import com.roadmap.utility.CommonConstants;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Properties;

public class TaxCalculator implements Tax {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat (CommonConstants.DECIMAL_FOORMAT_PATTERN);
    Properties properties = PropertiesLoader.loadProperties (CommonConstants.PROPERTIES_FILE);
    private double vatFull = Double.valueOf (properties.getProperty ("vat.full"));
    private double vatReduced = Double.valueOf (properties.getProperty ("vat.reduced"));
    private double vatZero = Double.valueOf (properties.getProperty ("vat.zero"));

    public TaxCalculator() throws IOException {
    }

    @Override
    public Price visit(FullVatPrice fullVatPrice) {
        Price price = new Price ();
        Double netoPrice = fullVatPrice.getPrice ().getNetPrice ();
        Double tax = netoPrice * vatFull;
        price.setNetPrice (netoPrice);
        price.setTax (Double.valueOf (DECIMAL_FORMAT.format (tax)));
        price.setGrossPrice (Double.valueOf (DECIMAL_FORMAT.format(netoPrice + tax)));
        return price;
    }

    @Override
    public Price visit(ReducedVatPrice reducedVatPrice) {
        Price price = new Price ();
        Double netoPrice = reducedVatPrice.getPrice ().getNetPrice ();
        Double tax = netoPrice * vatReduced;
        price.setNetPrice (netoPrice);
        price.setTax (Double.valueOf (DECIMAL_FORMAT.format(tax)));
        price.setGrossPrice (Double.valueOf (DECIMAL_FORMAT.format(netoPrice + tax)));
        return price;
    }

    @Override
    public Price visit(ZeroVatPrice zeroVatPrice) {
        Price price = new Price ();
        Double netoPrice = zeroVatPrice.getPrice ().getNetPrice ();
        Double tax = netoPrice * vatZero;
        price.setNetPrice (netoPrice);
        price.setTax (Double.valueOf (DECIMAL_FORMAT.format(tax)));
        price.setGrossPrice (Double.valueOf (DECIMAL_FORMAT.format(netoPrice + tax)));
        return price;
    }
}
