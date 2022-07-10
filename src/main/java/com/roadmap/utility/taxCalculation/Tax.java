package com.roadmap.utility.taxCalculation;

public interface Tax {
    Price visit(FullVatPrice fullVatPrice);

    Price visit(ReducedVatPrice reducedVatPrice);

    Price visit(ZeroVatPrice zeroVatPrice);
}
