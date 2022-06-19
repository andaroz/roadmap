package com.roadmap.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutDetails {
    private Identity identity;
    private ShippingAddress shippingAddress;
    private PaymentDetails paymentDetails;
    private boolean checkedOut = false;
}
