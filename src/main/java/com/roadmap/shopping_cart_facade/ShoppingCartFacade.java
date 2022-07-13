package com.roadmap.shopping_cart_facade;

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
