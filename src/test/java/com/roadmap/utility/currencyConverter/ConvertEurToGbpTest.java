package com.roadmap.utility;
import com.roadmap.utility.currencyConverter.ConvertEurToGbp;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConvertEurToGbpTest {

    Double valutasKurss;


    private ConvertEurToGbp convertEurToGbp = new ConvertEurToGbp ();
    private Double eur = 1.52;
    private Double convertedValue;

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
    }

    @Test
    void interpret() {
    }
}