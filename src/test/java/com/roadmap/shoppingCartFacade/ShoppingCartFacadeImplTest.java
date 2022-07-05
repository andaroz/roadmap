package com.roadmap.shoppingCartFacade;

import com.roadmap.models.*;
import com.roadmap.services.ItemServiceImpl;
import com.roadmap.utility.CommonConstants;
import com.roadmap.utility.checkout.CareTaker;
import com.roadmap.utility.checkout.Memento;
import com.roadmap.utility.checkout.Originator;
import com.roadmap.utility.taxCalculation.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

class ShoppingCartFacadeImplTest {

    @Mock
    private ItemServiceImpl itemService;
    @Mock
    private ShoppingCartFacadeImpl shoppingCartFacade;

    private static final Long ID = 1L;
    private static final String NAME = "Name";
    private static final String LAST_NAME = "Surname";
    private static final String COUNTRY = "Latvia";
    private static final String STREET = "OneWay";
    private static final String HOUSE_NUMBER = "1";
    private static final String ZIP = "LV-1000";
    private static final String CARD_OWNER = "Card Owner";
    private static final String CARD_NUMBER = "1234123412341234";
    private static final String EXPIRY_DATE = "01/25";
    private static final String CVC = "123";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat (CommonConstants.DECIMAL_FOORMAT_PATTERN);
    private int currentState = 0;
    private Originator originator = new Originator ();
    private CareTaker careTaker = new CareTaker ();
    private Checkout checkout = new Checkout ();
    private Item item;
    private List<Item> items = new ArrayList<> ();
    private Order order;
    private CheckoutDetails checkoutDetails = new CheckoutDetails ();
    private HashMap<Long, ItemWithPrice> itemsInOrder = new HashMap<> ();
    private Price price = new Price ();
    private ItemWithPrice itemWithPrice = new ItemWithPrice ();
    private Identity identity = new Identity ();
    private ShippingAddress shippingAddress = new ShippingAddress ();
    private PaymentDetails paymentDetails = new PaymentDetails ();
    private Item vegetable = new Item ();

    ShoppingCartFacadeImplTest() throws IOException {
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks (this);
        item = new Item ();
        item.setId (ID);
        item.setAmountAvailable (2.0);
        item.setPrice (2.0);
        item.setName ("Apple");
        item.setDescription ("Red apple");
        item.setType (Type.FRUIT.toString ());
        items.add (item);

        vegetable.setId (2L);
        vegetable.setType (Type.VEGETABLE.toString ());
        vegetable.setName ("Carrot");
        vegetable.setDescription ("Eco carrot");
        vegetable.setPrice (1.0);
        vegetable.setAmountAvailable (4.0);

        this.order = new Order ();

        double itemPrice = item.getPrice ();
        double tax = Double.valueOf (DECIMAL_FORMAT.format (itemPrice * 0.05));
        price.setNetPrice (itemPrice);
        price.setTax (tax);
        price.setGrossPrice (itemPrice + tax);
        itemWithPrice.setId (ID);
        itemWithPrice.setName (item.getName ());
        itemWithPrice.setDescription (item.getDescription ());
        itemWithPrice.setUom (item.getUom ());
        itemWithPrice.setAmountOrdered (2.0);
        itemWithPrice.setPrice (price);
        itemWithPrice.setType (item.getType ());
        itemsInOrder.put (1L, itemWithPrice);
        order.setOrderItems (itemsInOrder);
        order.setTotalPrice (price);
    }

    @Test
    void addToOrder() {
        double amount = 1.0;
        long id = 2L;
        ItemWithPrice newItemWithThePrice = getItemWithPrice (vegetable);
        newItemWithThePrice.setAmountOrdered (amount);
        itemsInOrder.put (id, newItemWithThePrice);
        assertEquals (2, itemsInOrder.size ());
        vegetable.setAmountAvailable (vegetable.getAmountAvailable () + amount);
        doNothing ().when (itemService).reduceAvailableAmount (amount, id);
        assertEquals (5.0, vegetable.getAmountAvailable ());
        assertEquals (1.0, itemsInOrder.get (id).getAmountOrdered ());
    }

    @Test
    void getOrder() {
        HashMap<Long, ItemWithPrice> itemsInOrder = order.getOrderItems ();
        doReturn (price).when (shoppingCartFacade).calculateTotalPrice (itemsInOrder);
        order.setTotalPrice (price);
        assertEquals (itemWithPrice.getPrice ().getGrossPrice (), order.getTotalPrice ().getGrossPrice ());
        assertEquals (1, order.getOrderItems ().size ());
    }

    @Test
    void removeFromOrder() {
        double amountToRemove = 1.0;
        itemsInOrder.get (ID).setAmountOrdered (itemsInOrder.get (ID).getAmountOrdered () - amountToRemove);
        doNothing ().when (itemService).increaseAvailableAmount (amountToRemove, ID);
        assertEquals (1.0, itemsInOrder.get (ID).getAmountOrdered ());
    }

    @Test
    void proceedToCheckout() {
        Memento memento = originator.proceedToCheckout (order);
        careTaker.addMemento (memento);
        currentState = careTaker.getCurrentState ();
        Checkout newCheckout = careTaker.getMemento (currentState).getCheckout ();
        assertEquals (1, newCheckout.getOrder ().getOrderItems ().size ());
        assertNull (checkoutDetails.getIdentity ());
    }

    @Test
    void setIdentity() {
        originator.setIdentity (order, setIdentityData ());
        careTaker.addMemento (originator.storeInMemento ());
        currentState = careTaker.getCurrentState ();
        Checkout savedCheckout = careTaker.getMemento (currentState).getCheckout ();
        Identity savedIdentity = savedCheckout.getCheckoutDetails ().getIdentity ();
        assertEquals (1, savedCheckout.getOrder ().getOrderItems ().size ());
        assertEquals (NAME, savedIdentity.getName ());
        assertEquals (LAST_NAME, savedIdentity.getLastName ());
    }

    @Test
    void setShippingAddress() {
        checkoutDetails.setIdentity (setIdentityData ());
        originator.setShippingAddress (order, setShippingAddressData (), checkoutDetails);
        careTaker.addMemento (originator.storeInMemento ());
        currentState = careTaker.getCurrentState ();
        Checkout savedCheckout = careTaker.getMemento (currentState).getCheckout ();
        Identity savedIdentity = savedCheckout.getCheckoutDetails ().getIdentity ();
        ShippingAddress savedShippingAddress = savedCheckout.getCheckoutDetails ().getShippingAddress ();
        assertEquals (1, savedCheckout.getOrder ().getOrderItems ().size ());
        assertEquals (NAME, savedIdentity.getName ());
        assertEquals (COUNTRY, savedShippingAddress.getCountry ());
        assertEquals (STREET, savedShippingAddress.getStreet ());
        assertEquals (HOUSE_NUMBER, savedShippingAddress.getHouseNameOrNumber ());
        assertEquals (ZIP, savedShippingAddress.getZip ());
    }

    @Test
    void setPaymentDetails() {
        checkoutDetails.setIdentity (setIdentityData ());
        checkoutDetails.setShippingAddress (setShippingAddressData ());
        originator.setPaymentDetails (order, setPaymentDetailsData (), checkoutDetails);
        careTaker.addMemento (originator.storeInMemento ());
        currentState = careTaker.getCurrentState ();
        Checkout savedCheckout = careTaker.getMemento (currentState).getCheckout ();
        Identity savedIdentity = savedCheckout.getCheckoutDetails ().getIdentity ();
        ShippingAddress savedShippingAddress = savedCheckout.getCheckoutDetails ().getShippingAddress ();
        PaymentDetails savedPaymentDetails = savedCheckout.getCheckoutDetails ().getPaymentDetails ();
        assertEquals (1, savedCheckout.getOrder ().getOrderItems ().size ());
        assertEquals (NAME, savedIdentity.getName ());
        assertEquals (COUNTRY, savedShippingAddress.getCountry ());
        assertEquals (CARD_OWNER, savedPaymentDetails.getCardOwner ());
        assertEquals (CARD_NUMBER, savedPaymentDetails.getCardNumber ());
        assertEquals (EXPIRY_DATE, savedPaymentDetails.getExpiryDate ());
        assertEquals (CVC, savedPaymentDetails.getCvc ());
    }

    @Test
    void proceedPayment() {
        checkoutDetails.setIdentity (setIdentityData ());
        checkoutDetails.setShippingAddress (setShippingAddressData ());
        checkoutDetails.setPaymentDetails (setPaymentDetailsData ());
        originator.proceedPayment (order, checkoutDetails);
        careTaker.addMemento (originator.storeInMemento ());
        currentState = careTaker.getCurrentState ();
        Checkout savedCheckout = careTaker.getMemento (currentState).getCheckout ();
        Identity savedIdentity = savedCheckout.getCheckoutDetails ().getIdentity ();
        ShippingAddress savedShippingAddress = savedCheckout.getCheckoutDetails ().getShippingAddress ();
        PaymentDetails savedPaymentDetails = savedCheckout.getCheckoutDetails ().getPaymentDetails ();
        assertEquals (1, savedCheckout.getOrder ().getOrderItems ().size ());
        assertEquals (NAME, savedIdentity.getName ());
        assertEquals (COUNTRY, savedShippingAddress.getCountry ());
        assertEquals (CARD_OWNER, savedPaymentDetails.getCardOwner ());
        assertTrue (savedCheckout.getCheckoutDetails ().isCheckedOut ());
    }

    @Test
    void undo() {
        // proceedPayment and validate checkout which is saved in in Memento list
        checkout.setOrder (order);
        checkout.setCheckoutDetails (new CheckoutDetails ());
        Memento memento = new Memento (checkout);
        careTaker.addMemento (memento);
        currentState = careTaker.getCurrentState ();
        Checkout newCheckout = careTaker.getMemento (currentState).getCheckout ();
        assertEquals (1, newCheckout.getOrder ().getOrderItems ().size ());
        assertNull (checkoutDetails.getIdentity ());
        // add identity and validate new state
        originator.setIdentity (order, setIdentityData ());
        careTaker.addMemento (originator.storeInMemento ());
        currentState = careTaker.getCurrentState ();
        Checkout savedCheckout = careTaker.getMemento (currentState).getCheckout ();
        Identity savedIdentity = savedCheckout.getCheckoutDetails ().getIdentity ();
        assertEquals (1, savedCheckout.getOrder ().getOrderItems ().size ());
        assertEquals (NAME, savedIdentity.getName ());
        assertEquals (LAST_NAME, savedIdentity.getLastName ());
        // undo setIdentity and validate that current state is the same as before
        careTaker.undo ();
        currentState = careTaker.getCurrentState ();
        Checkout previousCheckout = careTaker.getMemento (currentState).getCheckout ();
        Identity previousIdentity = previousCheckout.getCheckoutDetails ().getIdentity ();
        assertEquals (1, previousCheckout.getOrder ().getOrderItems ().size ());
        assertNull (previousIdentity);
        assertEquals (2, careTaker.getSavedStates ().size ());
    }

    private Identity setIdentityData() {
        identity.setName (NAME);
        identity.setLastName (LAST_NAME);
        return identity;
    }

    private ShippingAddress setShippingAddressData() {
        shippingAddress.setCountry (COUNTRY);
        shippingAddress.setStreet (STREET);
        shippingAddress.setHouseNameOrNumber (HOUSE_NUMBER);
        shippingAddress.setZip (ZIP);
        return shippingAddress;
    }

    private PaymentDetails setPaymentDetailsData() {
        paymentDetails.setCardOwner (CARD_OWNER);
        paymentDetails.setCardNumber (CARD_NUMBER);
        paymentDetails.setExpiryDate (EXPIRY_DATE);
        paymentDetails.setCvc (CVC);
        return paymentDetails;
    }

    private ItemWithPrice getItemWithPrice(Item item) {
        ItemWithPrice itemWithPrice = new ItemWithPrice ();
        double itemPrice = item.getPrice ();
        double tax = Double.valueOf (DECIMAL_FORMAT.format (itemPrice * 0.05));
        Price price = new Price ();
        price.setNetPrice (itemPrice);
        price.setTax (tax);
        price.setGrossPrice (itemPrice + tax);
        itemWithPrice.setId (ID);
        itemWithPrice.setName (item.getName ());
        itemWithPrice.setDescription (item.getDescription ());
        itemWithPrice.setUom (item.getUom ());
        itemWithPrice.setPrice (price);
        itemWithPrice.setType (item.getType ());
        return itemWithPrice;
    }
}