package com.roadmap.shoppingCartFacade;

import com.roadmap.models.Order;

public interface ShoppingCartFacade {
    public void addToOrder(Long itemId, double amount, Order order);
    public Order getOrder(Order order);
    public void removeFromOrder(Long itemId, double amount, Order order);
}
