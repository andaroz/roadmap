package com.roadmap.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetails {
    private String cardNumber;
    private String cardOwner;
    private String expiryDate;
    private String cvc;
}
