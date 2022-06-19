package com.roadmap.utility.checkout;

import com.roadmap.models.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Originator {

//    private Order order;
    private Checkout checkout;
    private CheckoutDetails checkoutDetails;


    public Memento proceedToCheckout(Order order) {
        checkout = new Checkout ();
        checkoutDetails = new CheckoutDetails ();
        checkout.setCheckoutDetails (checkoutDetails);
        checkout.setOrder (order);
        return new Memento (checkout);
    }

    public Memento setIdentity(Order order, Identity identity) {
        checkout = new Checkout ();
        if (!checkoutDetails.isCheckedOut ()) {
            if (identity.getName () != null && identity.getLastName () != null) {
                checkoutDetails = new CheckoutDetails ();
                checkoutDetails.setIdentity (identity);
                checkout.setCheckoutDetails (checkoutDetails);
                checkout.setOrder (order);
            } else {
                log.info ("Please fill in the name and surname");
            }
        }
        return new Memento (checkout);
    }

    public Memento setShippingAddress(Order order, ShippingAddress shippingAddress, CheckoutDetails previousCheckoutDetails) {
        if (!previousCheckoutDetails.isCheckedOut ()) {
            checkout = new Checkout ();
            checkout.setOrder (order);
            checkoutDetails = new CheckoutDetails ();
            Identity identity = new Identity ();
            identity.setName (previousCheckoutDetails.getIdentity ().getName ());
            identity.setLastName (previousCheckoutDetails.getIdentity ().getLastName ());
            checkoutDetails.setIdentity (identity);

            if (shippingAddress.getCountry () != null && shippingAddress.getHouseNameOrNumber () != null
                    && shippingAddress.getStreet () != null && shippingAddress.getZip () != null) {
                checkoutDetails.setShippingAddress (shippingAddress);
                checkout.setCheckoutDetails (checkoutDetails);
            } else {
                log.info ("Please enter all shipping address information");
            }
        }
        return new Memento (checkout);
    }

    public Memento setPaymentDetails (Order order, PaymentDetails paymentDetails, CheckoutDetails previousCheckoutDetails) {
        if (!previousCheckoutDetails.isCheckedOut ()) {
            checkout = new Checkout ();
            checkout.setOrder (order);
            checkoutDetails = new CheckoutDetails ();
            checkoutDetails.setIdentity (previousCheckoutDetails.getIdentity ());
            checkoutDetails.setShippingAddress (previousCheckoutDetails.getShippingAddress ());
            if (paymentDetails.getCardNumber () != null && paymentDetails.getCardOwner () != null
                    && paymentDetails.getExpiryDate () != null && paymentDetails.getCvc () != null) {
                checkoutDetails.setPaymentDetails (paymentDetails);
                checkout.setCheckoutDetails (checkoutDetails);
            } else {
                log.info ("Please enter payment details");
            }
        }
        return new Memento (checkout);
    }

    public void proceedPayment(Order order, CheckoutDetails currentCheckoutDetails) {
        if (!currentCheckoutDetails.isCheckedOut ()) {
            Identity identity = currentCheckoutDetails.getIdentity ();
            ShippingAddress shippingAddress = currentCheckoutDetails.getShippingAddress ();
            PaymentDetails paymentDetails = currentCheckoutDetails.getPaymentDetails ();
            if (order.getOrderItems () == null) {
                log.info ("There is no items in your basket, please add items in your basket!");
            } else if (identity == null) {
                log.info ("Please add identity information in your order");
            } else if (shippingAddress == null) {
                log.info ("Please add your shipping address details!");
            } else if (paymentDetails == null) {
                log.info ("Please add payment details");
            } else {
                currentCheckoutDetails.setCheckedOut (true);
            }
        }
    }

    public Memento storeInMemento(){
        return new Memento (checkout);
    }
}
