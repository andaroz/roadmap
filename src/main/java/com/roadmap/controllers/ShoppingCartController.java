package com.roadmap.controllers;

import com.roadmap.models.*;
import com.roadmap.shoppingCartFacade.ShoppingCartFacadeImpl;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping("/cart")
public class ShoppingCartController {


    private ShoppingCartFacadeImpl shoppingCartFacade;
    private Order order;

    public ShoppingCartController(ShoppingCartFacadeImpl shoppingCartFacade,
                                  Order order) {
        this.shoppingCartFacade = shoppingCartFacade;
        if (order.getInstance () == null) {
            this.order = new Order ();
        } else {
            this.order = order;
        }
    }

    @PostMapping(path = "/addToCart{id}{amount}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Order addItemToOrder(@PathParam("id") Long id, @PathParam("amount") double amount) {
        shoppingCartFacade.addToOrder (id, amount, order);
        return order;
    }

    @PutMapping(path = "/removeFromCart{id}{amount}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void removeFromOrder(@PathParam("id") Long id, @PathParam("amount") double amount) {
        shoppingCartFacade.removeFromOrder (id, amount, order);
    }

    @GetMapping(path = "/shoppingCart", produces = MediaType.APPLICATION_JSON_VALUE)
    public Order getOrder() {
        return shoppingCartFacade.getOrder (order);
    }

    @GetMapping(path = "/proceedToCheckout", produces = MediaType.APPLICATION_JSON_VALUE)
    public Checkout proceedToCheckout(){
        return shoppingCartFacade.proceedToCheckout (order);
    }

    @PostMapping(path = "/addCustomerDetails", produces = MediaType.APPLICATION_JSON_VALUE)
    public Checkout addCustomerDetails(@RequestBody Identity identity) {
        return shoppingCartFacade.setIdentity (identity, order);
    }

    @GetMapping(path = "/addCustomerDetails/undo", produces = MediaType.APPLICATION_JSON_VALUE)
    public Checkout undoCustomerDetails() {
        return shoppingCartFacade.undo ();
    }

    @PostMapping(path = "/addShippingAddress", produces = MediaType.APPLICATION_JSON_VALUE)
    public Checkout addShippingAddress(@RequestBody ShippingAddress shippingAddress) {
        return shoppingCartFacade.setShippingAddress (shippingAddress, order);
    }

    @GetMapping(path = "/addShippingAddress/undo", produces = MediaType.APPLICATION_JSON_VALUE)
    public Checkout undoShippingAddress() {
        return shoppingCartFacade.undo ();
    }

    @PostMapping(path = "/addPaymentDetails", produces = MediaType.APPLICATION_JSON_VALUE)
    public Checkout addPaymentDetails(@RequestBody PaymentDetails paymentDetails) {
        return shoppingCartFacade.setPaymentDetails (paymentDetails, order);
    }

    @GetMapping(path = "/addPaymentDetails/undo", produces = MediaType.APPLICATION_JSON_VALUE)
    public Checkout undoPaymentDetails() {
        shoppingCartFacade.undo ();
        return shoppingCartFacade.undo ();
    }

    @GetMapping(path = "/proceedPayment", produces = MediaType.APPLICATION_JSON_VALUE)
    public Checkout proceedPayment() {
        return shoppingCartFacade.proceedPayment (order);
    }
}
