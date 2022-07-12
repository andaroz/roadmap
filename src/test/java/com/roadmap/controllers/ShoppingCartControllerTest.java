package com.roadmap.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roadmap.exceptions.BadRequestException;
import com.roadmap.exceptions.GlobalControllerAdvice;
import com.roadmap.models.*;
import com.roadmap.shoppingCartFacade.ShoppingCartFacadeImpl;
import com.roadmap.utility.CommonConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ShoppingCartControllerTest {

    @Mock
    private ShoppingCartFacadeImpl shoppingCartFacade;

    @InjectMocks
    private ShoppingCartController shoppingCartController;

    @Autowired
    private MockMvc mockMvc;

    private final long ID = 1L;
    private final String PATH = CommonConstants.SHOPING_CART_PATH;
    private Item item;
    private List<Item> items = new ArrayList<> ();
    private Order order;
    private Checkout checkout = new Checkout ();
    private CheckoutDetails checkoutDetails = new CheckoutDetails ();
    private Identity identity = new Identity ();
    private ShippingAddress shippingAddress = new ShippingAddress ();
    private PaymentDetails paymentDetails = new PaymentDetails ();
    private ObjectMapper mapper = new ObjectMapper ();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks (this);
        mockMvc = MockMvcBuilders.standaloneSetup (shoppingCartController)
                .setControllerAdvice (new GlobalControllerAdvice ())
                .build ();
        item = new Item ();
        item.setId (ID);
        item.setAmountAvailable (2.0);
        item.setPrice (2.0);
        item.setName ("Apple");
        item.setDescription ("Red apple");
        item.setType (Type.FRUIT.toString ());
        items.add (item);

        this.order = new Order ();
    }

    @Test
    void addItemToOrder() throws Exception {
        doReturn (order).when (shoppingCartFacade).addToOrder (ID, 1.0);
        mockMvc.perform (post (PATH + "/addToCart?id=1&amount=1"))
                .andExpect (status ().isOk ())
                .andDo (print ());
    }

    @Test
    void removeFromOrder() throws Exception {
        doReturn (order).when (shoppingCartFacade).removeFromOrder (ID, 1.0);
        mockMvc.perform (put (PATH + "/removeFromCart?id=1&amount=1"))
                .andExpect (status ().isOk ())
                .andDo (print ());
    }

    @Test
    void getOrder() throws Exception {
        doReturn (order).when (shoppingCartFacade).getOrder ();
        mockMvc.perform (get (PATH + "/shoppingCart"))
                .andExpect (status ().isOk ())
                .andDo (print ());
    }

    @Test
    void proceedToCheckout() throws Exception {
        checkout.setOrder (order);
        checkout.setCheckoutDetails (checkoutDetails);
        doReturn (checkout).when (shoppingCartFacade).proceedToCheckout ();
        mockMvc.perform (get (PATH + "/proceedToCheckout"))
                .andExpect (status ().isOk ())
                .andDo (print ());
    }

    @Test
    void addCustomerDetails() throws Exception {
        setIdentity ();
        doReturn (checkout).when (shoppingCartFacade).setIdentity (identity);
        mockMvc.perform (post (PATH + "/addCustomerDetails")
                .contentType (MediaType.APPLICATION_JSON)
                .content (mapper.writeValueAsString (identity)))
                .andExpect (status ().isOk ())
                .andDo (print ());
    }

    @Test
    void addCustomerDetails_throwsBadRequestException() throws Exception {
        Identity emptyIdentity = new Identity ();
        doThrow (new BadRequestException ()).when (shoppingCartFacade).setIdentity (any (Identity.class));
        mockMvc.perform (post (PATH + "/addCustomerDetails")
                .contentType (MediaType.APPLICATION_JSON)
                .content (mapper.writeValueAsString (emptyIdentity)))
                .andDo (print ())
                .andExpect (status ().isBadRequest ())
                .andExpect (result -> assertEquals (CommonConstants.MESSAGE_BAD_REQUEST, result.getResolvedException ().getMessage ()));
    }

    @Test
    void undoCustomerDetails() throws Exception {
        checkoutDetails.setIdentity (null);
        doReturn (checkout).when (shoppingCartFacade).undo ();
        mockMvc.perform (get (PATH + "/addCustomerDetails/undo"))
                .andExpect (status ().isOk ())
                .andDo (print ());
    }

    @Test
    void addShippingAddress() throws Exception {
        setIdentity ();
        setShippingAddress ();
        doReturn (checkout).when (shoppingCartFacade).setShippingAddress (shippingAddress);
        mockMvc.perform (post (PATH + "/addShippingAddress")
                .contentType (MediaType.APPLICATION_JSON)
                .content (mapper.writeValueAsString (shippingAddress)))
                .andExpect (status ().isOk ())
                .andDo (print ());
    }

    @Test
    void addShippingAddress_throwsBadRequestException() throws Exception {
        setIdentity ();
        ShippingAddress emptyShippingAddress = new ShippingAddress ();
        BadRequestException exception = new BadRequestException ();
        when (shoppingCartFacade.setShippingAddress (any (ShippingAddress.class))).thenThrow (exception);
        mockMvc.perform (post (PATH + "/addShippingAddress")
                .contentType (MediaType.APPLICATION_JSON)
                .content (mapper.writeValueAsString (emptyShippingAddress)))
                .andDo (print ())
                .andExpect (status ().isBadRequest ())
                .andExpect (result -> assertEquals (CommonConstants.MESSAGE_BAD_REQUEST, result.getResolvedException ().getMessage ()));
    }

    @Test
    void undoShippingAddress() throws Exception {
        checkoutDetails.setShippingAddress (null);
        doReturn (checkout).when (shoppingCartFacade).undo ();
        mockMvc.perform (get (PATH + "/addShippingAddress/undo"))
                .andExpect (status ().isOk ())
                .andDo (print ());
    }

    @Test
    void addPaymentDetails() throws Exception {
        setIdentity ();
        setShippingAddress ();
        setPaymentDetails ();
        doReturn (checkout).when (shoppingCartFacade).setPaymentDetails (any (PaymentDetails.class));
        mockMvc.perform (post (PATH + "/addPaymentDetails")
                .contentType (MediaType.APPLICATION_JSON)
                .content (mapper.writeValueAsString (paymentDetails)))
                .andExpect (status ().isOk ())
                .andDo (print ());
    }

    @Test
    void addPaymentDetails_throwsBadRequestException() throws Exception {
        setIdentity ();
        setShippingAddress ();
        PaymentDetails emptyPaymentDetails = new PaymentDetails ();
        doThrow (new BadRequestException ()).when (shoppingCartFacade).setPaymentDetails (any (PaymentDetails.class));
        mockMvc.perform (post (PATH + "/addPaymentDetails")
                .contentType (MediaType.APPLICATION_JSON)
                .content (mapper.writeValueAsString (emptyPaymentDetails)))
                .andDo (print ())
                .andExpect (status ().isBadRequest ())
                .andExpect (result -> assertEquals (CommonConstants.MESSAGE_BAD_REQUEST, result.getResolvedException ().getMessage ()));
    }

    @Test
    void undoPaymentDetails() throws Exception {
        checkoutDetails.setPaymentDetails (null);
        doReturn (checkout).when (shoppingCartFacade).undo ();
        mockMvc.perform (get (PATH + "/addPaymentDetails/undo"))
                .andExpect (status ().isOk ())
                .andDo (print ());
    }

    @Test
    void proceedPayment_throwsBadRequestException_MissingItemsInOrder() throws Exception {
        BadRequestException exception = new BadRequestException (CommonConstants.MESSAGE_MISSING_ITEMS_IN_ORDER, true);
        setIdentity ();
        setShippingAddress ();
        setPaymentDetails ();
        checkoutDetails.setCheckedOut (true);
        doThrow (exception).when (shoppingCartFacade).proceedPayment ();
        mockMvc.perform (get (PATH + "/proceedPayment"))
                .andDo (print ())
                .andExpect (status ().isBadRequest ())
                .andExpect (result -> assertEquals (CommonConstants.MESSAGE_MISSING_ITEMS_IN_ORDER, result.getResolvedException ().getMessage ()));
    }

    @Test
    void proceedPayment() throws Exception {
        setIdentity ();
        setShippingAddress ();
        setPaymentDetails ();
        checkoutDetails.setCheckedOut (true);
        doReturn (checkout).when (shoppingCartFacade).proceedPayment ();
        mockMvc.perform (get (PATH + "/proceedPayment"))
                .andExpect (status ().isOk ())
                .andDo (print ());
    }

    private void setIdentity() {
        identity.setName ("Name");
        identity.setLastName ("Surname");
        checkoutDetails.setIdentity (identity);
    }

    private void setShippingAddress() {
        shippingAddress.setCountry ("Latvia");
        shippingAddress.setStreet ("OneWay");
        shippingAddress.setHouseNameOrNumber ("1");
        shippingAddress.setZip ("LV-1000");
        checkoutDetails.setShippingAddress (shippingAddress);
    }

    private void setPaymentDetails() {
        paymentDetails.setCardOwner ("Card Owner");
        paymentDetails.setCardNumber ("1234123412341234");
        paymentDetails.setExpiryDate ("01/25");
        paymentDetails.setCvc ("123");
        checkoutDetails.setPaymentDetails (paymentDetails);
    }
}