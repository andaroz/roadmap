package com.roadmap.controllers;

import com.roadmap.models.*;
import com.roadmap.shoppingCartFacade.ShoppingCartFacadeImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping("/cart")
public class ShoppingCartController {

    private ShoppingCartFacadeImpl shoppingCartFacade;

    public ShoppingCartController(ShoppingCartFacadeImpl shoppingCartFacade) {
        this.shoppingCartFacade = shoppingCartFacade;
    }

    @PostMapping(path = "/addToCart{id}{amount}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Order addItemToOrder(@PathParam("id") Long id, @PathParam("amount") double amount) {
        return shoppingCartFacade.addToOrder (id, amount);
    }

    @PutMapping(path = "/removeFromCart{id}{amount}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> removeFromOrder(@PathParam("id") Long id, @PathParam("amount") double amount) {
        return new ResponseEntity<> (shoppingCartFacade.removeFromOrder (id, amount), HttpStatus.OK);
    }

    @GetMapping(path = "/shoppingCart", produces = MediaType.APPLICATION_JSON_VALUE)
    public Order getOrder() {
        return shoppingCartFacade.getOrder ();
    }

    @GetMapping(path = "/proceedToCheckout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> proceedToCheckout() {
        return new ResponseEntity<Checkout> (shoppingCartFacade.proceedToCheckout (), HttpStatus.OK);
    }

    @PostMapping(path = "/addCustomerDetails", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addCustomerDetails(@RequestBody Identity identity) {
        return new ResponseEntity<> (shoppingCartFacade.setIdentity (identity), HttpStatus.OK);
    }

    @GetMapping(path = "/addCustomerDetails/undo", produces = MediaType.APPLICATION_JSON_VALUE)
    public Checkout undoCustomerDetails() {
        return shoppingCartFacade.undo ();
    }

    @PostMapping(path = "/addShippingAddress", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addShippingAddress(@RequestBody ShippingAddress shippingAddress) {
        return new ResponseEntity<Checkout> (shoppingCartFacade.setShippingAddress (shippingAddress), HttpStatus.OK);
    }

    @GetMapping(path = "/addShippingAddress/undo", produces = MediaType.APPLICATION_JSON_VALUE)
    public Checkout undoShippingAddress() {
        return shoppingCartFacade.undo ();
    }

    @PostMapping(path = "/addPaymentDetails", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addPaymentDetails(@RequestBody PaymentDetails paymentDetails) {
        return new ResponseEntity<Checkout> (shoppingCartFacade.setPaymentDetails (paymentDetails), HttpStatus.OK);
    }

    @GetMapping(path = "/addPaymentDetails/undo", produces = MediaType.APPLICATION_JSON_VALUE)
    public Checkout undoPaymentDetails() {
        shoppingCartFacade.undo ();
        return shoppingCartFacade.undo ();
    }

    @GetMapping(path = "/proceedPayment", produces = MediaType.APPLICATION_JSON_VALUE)
    public Checkout proceedPayment() {
        return shoppingCartFacade.proceedPayment ();
    }
}
