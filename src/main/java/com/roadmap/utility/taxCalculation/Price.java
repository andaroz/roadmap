package com.roadmap.utility.taxCalculation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Price {

    private double netPrice;
    private double tax;
    private double grossPrice;

    public Price(double netPrice) {
        this.netPrice = netPrice;
    }
}
