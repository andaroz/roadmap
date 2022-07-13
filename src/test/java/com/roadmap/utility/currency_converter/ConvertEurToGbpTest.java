package com.roadmap.utility.currency_converter;

import com.roadmap.config.PropertiesLoader;
import com.roadmap.utility.CommonConstants;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConvertEurToGbpTest {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat (CommonConstants.DECIMAL_FOORMAT_PATTERN);
    private double eur = 1.52;
    private double convertedValue;
    private ConvertEurToGbp convertEurToGbp = new ConvertEurToGbp ();
    private Properties properties = PropertiesLoader.loadProperties (CommonConstants.PROPERTIES_FILE);
    private double eurToGbp = Double.valueOf (properties.getProperty (CommonConstants.PROPERTY_KEY_EUR_TO_GBP));

    ConvertEurToGbpTest() throws IOException {
    }

    @Test
    void getEur() {
        convertEurToGbp.getEur (eur);
        convertedValue = convertEurToGbp.interpret ();
        assertEquals (1.0 * eur, convertedValue);
    }

    @Test
    void getGbp() throws IOException {
        convertEurToGbp.getGbp (eur);
        convertedValue = convertEurToGbp.interpret ();
        assertEquals (Double.valueOf (DECIMAL_FORMAT.format (eurToGbp * eur)), convertedValue);
    }
}