package com.roadmap.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddress {
    private String country;
    private String street;
    private String houseNameOrNumber;
    private String zip;
}
