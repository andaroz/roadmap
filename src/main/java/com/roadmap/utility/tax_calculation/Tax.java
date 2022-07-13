package com.roadmap.utility.tax_calculation;

public interface Tax {
    Price visit(FullVatPrice fullVatPrice);

    Price visit(ReducedVatPrice reducedVatPrice);

    Price visit(ZeroVatPrice zeroVatPrice);
}
