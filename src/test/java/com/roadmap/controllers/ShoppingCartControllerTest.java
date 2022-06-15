package com.roadmap.controllers;

import com.roadmap.services.ItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShoppingCartControllerTest {

    @Autowired
    ItemServiceImpl itemService;
//    @Autowired
//    ShoppingCartServiceImpl shoppingCartService;
    private HashMap<Long, Integer> shoppingCart = new HashMap<> ();


    void setUp(){

    }


    @Test
    void addItemToShoppingCart() {
    }

    @Test
    void removeFromChoppingCart() {
    }

    @Test
    void removeFromChoppingCart1() {
    }

    @Test
    void calculateTotalValue() {
        shoppingCart.put (1L, 2);
        shoppingCart.put (2L, 1);
        Double totalValue = 0.00;
//        totalValue = shoppingCartService.calculateTotalValue (shoppingCart);
        assertEquals (4.75, totalValue);

    }
}