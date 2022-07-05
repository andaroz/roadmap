package com.roadmap.models;

import lombok.*;

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
