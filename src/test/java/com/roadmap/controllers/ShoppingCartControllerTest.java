package com.roadmap.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roadmap.models.*;
import com.roadmap.shoppingCartFacade.ShoppingCartFacadeImpl;
import com.roadmap.utility.CommonConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ShoppingCartControllerTest {

    @Mock
    private ShoppingCartFacadeImpl shoppingCartFacade;

    @InjectMocks
    private ShoppingCartController shoppingCartController;

    private MockMvc mockMvc;

    private final long ID = 1L;
    private final String PATH = CommonConstants.BASE_PATH + CommonConstants.SHOPING_CART_PATH;
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
        mockMvc = MockMvcBuilders.standaloneSetup (shoppingCartController).build ();
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
    void addItemToOrder() throws Exception{
        doNothing ().when (shoppingCartFacade).addToOrder (ID, 1.0, order);
        mockMvc.perform (post(PATH + "/addToCart?id=1&amount=1"))
                .andExpect (status().isOk());
    }

    @Test
    void removeFromOrder() throws Exception{
        doNothing ().when (shoppingCartFacade).removeFromOrder (ID, 1.0, order);
        mockMvc.perform (put (PATH + "/removeFromCart?id=1&amount=1"))
                .andExpect (status ().isOk ());
    }

    @Test
    void getOrder() throws Exception{
        doReturn (order).when (shoppingCartFacade).getOrder (order);
        mockMvc.perform (get (PATH + "/shoppingCart"))
                .andExpect (status ().isOk ());
    }

    @Test
    void proceedToCheckout() throws Exception{
        checkout.setOrder (order);
        checkout.setCheckoutDetails (checkoutDetails);
        doReturn (checkout).when (shoppingCartFacade).proceedToCheckout (order);
        mockMvc.perform (get (PATH + "/proceedToCheckout"))
                .andExpect (status ().isOk ());
    }

    @Test
    void addCustomerDetails() throws Exception{
        setIdentity ();
        doReturn (checkout).when (shoppingCartFacade).setIdentity (identity, order);
        mockMvc.perform (post (PATH + "/addCustomerDetails")
                .contentType (MediaType.APPLICATION_JSON)
                .content (mapper.writeValueAsString (identity)))
                .andExpect (status ().isOk ());
    }

    @Test
    void undoCustomerDetails() throws Exception{
        checkoutDetails.setIdentity (null);
        doReturn (checkout).when (shoppingCartFacade).undo ();
        mockMvc.perform (get (PATH + "/addCustomerDetails/undo"))
                .andExpect (status ().isOk ());
    }

    @Test
    void addShippingAddress() throws Exception{
        setIdentity ();
        setShippingAddress();
        doReturn (checkout).when (shoppingCartFacade).setShippingAddress (shippingAddress, order);
        mockMvc.perform (post (PATH + "/addShippingAddress")
                .contentType (MediaType.APPLICATION_JSON)
                .content (mapper.writeValueAsString (shippingAddress)))
                .andExpect (status ().isOk ());
    }

    @Test
    void undoShippingAddress() throws Exception{
        checkoutDetails.setShippingAddress (null);
        doReturn (checkout).when (shoppingCartFacade).undo ();
        mockMvc.perform (get (PATH + "/addShippingAddress/undo"))
                .andExpect (status ().isOk ());
    }

    @Test
    void addPaymentDetails() throws Exception{
        setIdentity ();
        setShippingAddress ();
        setPaymentDetails ();
        doReturn (checkout).when (shoppingCartFacade).proceedToCheckout (order);
        mockMvc.perform (post (PATH + "/addPaymentDetails")
                .contentType (MediaType.APPLICATION_JSON)
                .content (mapper.writeValueAsString (paymentDetails)))
                .andExpect (status ().isOk ());
    }

    @Test
    void undoPaymentDetails() throws Exception{
        checkoutDetails.setPaymentDetails (null);
        doReturn (checkout).when (shoppingCartFacade).undo ();
        mockMvc.perform (get (PATH + "/addPaymentDetails/undo"))
                .andExpect (status ().isOk ());
    }

    @Test
    void proceedPayment() throws Exception{
        setIdentity ();
        setShippingAddress ();
        setPaymentDetails ();
        checkoutDetails.setCheckedOut (true);
        doReturn (checkout).when (shoppingCartFacade).proceedPayment (order);
        mockMvc.perform (get (PATH + "/proceedPayment"))
                .andExpect (status ().isOk ());
    }

    private void setIdentity(){
        identity.setName ("Name");
        identity.setLastName ("Surname");
        checkoutDetails.setIdentity (identity);
    }

    private void setShippingAddress(){
        shippingAddress.setCountry ("Latvia");
        shippingAddress.setStreet ("OneWay");
        shippingAddress.setHouseNameOrNumber ("1");
        shippingAddress.setZip ("LV-1000");
        checkoutDetails.setShippingAddress (shippingAddress);
    }

    private void setPaymentDetails(){
        paymentDetails.setCardOwner ("Card Owner");
        paymentDetails.setCardNumber ("1234123412341234");
        paymentDetails.setExpiryDate ("01/25");
        paymentDetails.setCvc ("123");
        checkoutDetails.setPaymentDetails (paymentDetails);
    }
}