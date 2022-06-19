package com.roadmap.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Checkout {
    private Order order;
    private CheckoutDetails checkoutDetails;
}
