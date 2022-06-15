package com.roadmap.controllers;

import com.roadmap.models.Order;
import com.roadmap.shoppingCartFacade.ShoppingCartFacadeImpl;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@RestController
//@Controller
@RequestMapping("/cart")
public class ShoppingCartController {


    private ShoppingCartFacadeImpl shoppingCartFacade;
    private Order order;

    public ShoppingCartController(ShoppingCartFacadeImpl shoppingCartFacade,
                                  Order order) {
        this.shoppingCartFacade = shoppingCartFacade;
        if (order.getInstance() == null) {
            this.order = new Order();
        } else {
            this.order = order;
        }
    }

    @PostMapping(path = "/addToCart{id}{amount}", produces= MediaType.APPLICATION_JSON_VALUE)
    public Order addItemToOrder(@PathParam("id") Long id, @PathParam("amount") double amount) {

        shoppingCartFacade.addToOrder (id, amount, order);
        return order;
    }

    @PutMapping(path = "/removeFromCart{id}{amount}", produces= MediaType.APPLICATION_JSON_VALUE)
    public void removeFromOrder(@PathParam("id") Long id, @PathParam("amount") double amount, Order order) {
       shoppingCartFacade.removeFromOrder (id, amount, order);
    }

    @GetMapping(path = "/shoppingCart", produces= MediaType.APPLICATION_JSON_VALUE)
    public Order getOrder(){
        return shoppingCartFacade.getOrder (order);
    }

}
