package com.roadmap.models;

import lombok.*;

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
