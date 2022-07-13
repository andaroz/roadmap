package com.roadmap.utility.currency_converter;

import com.roadmap.config.PropertiesLoader;
import com.roadmap.utility.CommonConstants;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Properties;

public class ConvertEurToGbp implements Expression {

    private Double value;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat (CommonConstants.DECIMAL_FOORMAT_PATTERN);

    public void getEur(Double amount) {
        this.value = amount;
    }

    public void getGbp(Double amount) throws IOException {
        Properties properties = PropertiesLoader.loadProperties (CommonConstants.PROPERTIES_FILE);
        String eurToGbp = properties.getProperty (CommonConstants.PROPERTY_KEY_EUR_TO_GBP);
        this.value = amount * Double.valueOf (eurToGbp);
    }

    @Override
    public Double interpret() {
        return Double.valueOf (DECIMAL_FORMAT.format (this.value));
    }
}
