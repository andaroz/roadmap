package com.roadmap.shoppingCartFacade;

import com.roadmap.models.*;

public interface ShoppingCartFacade {
    Order addToOrder(Long itemId, double amount);

    Order getOrder();

    Order removeFromOrder(Long itemId, double amount);

    Checkout proceedToCheckout();

    Checkout setIdentity(Identity identity);

    Checkout setShippingAddress(ShippingAddress shippingAddress);

    Checkout setPaymentDetails(PaymentDetails paymentDetails);

    Checkout proceedPayment();

    Checkout undo();
}
