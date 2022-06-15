package com.roadmap.utility.taxCalculation;

public interface Tax {
    public Price visit(FullVatPrice fullVatPrice);

    public Price visit(ReducedVatPrice reducedVatPrice);

    public Price visit(ZeroVatPrice zeroVatPrice);
}
