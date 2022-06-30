package com.roadmap.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetails {
    @NonNull
    private String cardNumber;
    @NonNull
    private String cardOwner;
    @NonNull
    private String expiryDate;
    @NonNull
    private String cvc;
}
