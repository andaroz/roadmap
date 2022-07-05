package com.roadmap.models;

import com.roadmap.utility.taxCalculation.Price;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemWithPrice {
    private Long id;
    private String name;
    private String description;
    private String uom;
    private String type;
    private String image;
    private Price price;
    private Double amountOrdered = 0.00;
}
