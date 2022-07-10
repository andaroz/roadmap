package com.roadmap.utility.checkout;

import com.roadmap.models.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Originator {

    private Checkout checkout;
    private CheckoutDetails checkoutDetails;

    public Memento proceedToCheckout(Order order) {
        checkout = new Checkout ();
        checkoutDetails = new CheckoutDetails ();
        checkout.setCheckoutDetails (checkoutDetails);
        checkout.setOrder (order);
        return new Memento (checkout);
    }

    public void setIdentity(Order order, Identity identity) {
        checkout = new Checkout ();
        checkoutDetails = new CheckoutDetails ();
        checkoutDetails.setIdentity (identity);
        checkout.setCheckoutDetails (checkoutDetails);
        checkout.setOrder (order);
    }

    public void setShippingAddress(Order order, ShippingAddress shippingAddress, CheckoutDetails previousCheckoutDetails) {
        if (!previousCheckoutDetails.isCheckedOut ()) {
            checkout = new Checkout ();
            checkout.setOrder (order);
            checkoutDetails = new CheckoutDetails ();
            Identity identity = new Identity ();
            identity.setName (previousCheckoutDetails.getIdentity ().getName ());
            identity.setLastName (previousCheckoutDetails.getIdentity ().getLastName ());
            checkoutDetails.setIdentity (identity);
            checkoutDetails.setShippingAddress (shippingAddress);
            checkout.setCheckoutDetails (checkoutDetails);
        }
    }

    public void setPaymentDetails(Order order, PaymentDetails paymentDetails, CheckoutDetails previousCheckoutDetails) {
        if (!previousCheckoutDetails.isCheckedOut ()) {
            checkout = new Checkout ();
            checkout.setOrder (order);
            checkoutDetails = new CheckoutDetails ();
            checkoutDetails.setIdentity (previousCheckoutDetails.getIdentity ());
            checkoutDetails.setShippingAddress (previousCheckoutDetails.getShippingAddress ());
            checkoutDetails.setPaymentDetails (paymentDetails);
            checkout.setCheckoutDetails (checkoutDetails);
        }
    }

    public void proceedPayment(Order order, CheckoutDetails currentCheckoutDetails) {
        if (!currentCheckoutDetails.isCheckedOut ()) {
            checkout = new Checkout ();
            checkout.setOrder (order);
            currentCheckoutDetails.setCheckedOut (true);
            checkout.setCheckoutDetails (currentCheckoutDetails);
        }
    }

    public Memento storeInMemento() {
        return new Memento (checkout);
    }
}