package com.roadmap.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddress {
    @NonNull
    private String country;
    @NonNull
    private String street;
    @NonNull
    private String houseNameOrNumber;
    @NonNull
    private String zip;
}
