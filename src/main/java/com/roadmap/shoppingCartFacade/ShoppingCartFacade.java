package com.roadmap.shoppingCartFacade;

import com.roadmap.models.*;

public interface ShoppingCartFacade {
    public void addToOrder(Long itemId, double amount, Order order);
    public Order getOrder(Order order);
    public void removeFromOrder(Long itemId, double amount, Order order);
    public Checkout proceedToCheckout(Order order);
    public Checkout setIdentity(Identity identity, Order order);
    public Checkout setShippingAddress(ShippingAddress shippingAddress, Order order);
    public Checkout setPaymentDetails(PaymentDetails paymentDetails, Order order);
    public Checkout proceedPayment(Order order);
    public Checkout undo();
}
