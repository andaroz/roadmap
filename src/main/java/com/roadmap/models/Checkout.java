package com.roadmap.models;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Checkout {
    @NonNull
    private Order order;
    private CheckoutDetails checkoutDetails;
}
