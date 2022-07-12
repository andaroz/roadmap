package com.roadmap.shoppingCartFacade;

import com.roadmap.exceptions.BadRequestException;
import com.roadmap.models.*;
import com.roadmap.repositories.ItemRepository;
import com.roadmap.services.ItemServiceImpl;
import com.roadmap.utility.CommonConstants;
import com.roadmap.utility.taxCalculation.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;

class ShoppingCartFacadeImplTest {

    @MockBean
    private ItemRepository itemRepository;

    @Mock
    private ItemServiceImpl itemService;

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
    private static final Double NET_PRICE = 2.0;
    private static final Double REDUCED_VAT = 0.05;
    private static final Double VAT = 0.21;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat (CommonConstants.DECIMAL_FOORMAT_PATTERN);
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

    @BeforeEach
    void setUp() throws IOException {
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
        shoppingCartFacade = new ShoppingCartFacadeImpl (itemRepository, order);
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
        Price totalPrice = shoppingCartFacade.calculateTotalPrice (itemsInOrder);
        order.setTotalPrice (totalPrice);
        assertEquals (itemWithPrice.getPrice ().getGrossPrice () * 2.0, order.getTotalPrice ().getGrossPrice ());
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
        Checkout newCheckout = shoppingCartFacade.proceedToCheckout ();
        assertEquals (1, newCheckout.getOrder ().getOrderItems ().size ());
        assertNull (newCheckout.getCheckoutDetails ().getIdentity ());
    }

    @Test
    void setIdentity() {
        Checkout newCheckout = shoppingCartFacade.setIdentity (new Identity (NAME, LAST_NAME));
        assertEquals (1, newCheckout.getOrder ().getOrderItems ().size ());
        assertEquals (NAME, newCheckout.getCheckoutDetails ().getIdentity ().getName ());
        assertEquals (LAST_NAME, newCheckout.getCheckoutDetails ().getIdentity ().getLastName ());
    }

    @Test
    void setIdentity_ReturnsBadRequestException() {
        BadRequestException exception = assertThrows (BadRequestException.class, () -> {
            shoppingCartFacade.setIdentity (new Identity ());
        });

        assertEquals (CommonConstants.MESSAGE_BAD_REQUEST, exception.getMessage ());
    }

    @Test
    void setShippingAddress() {
        shoppingCartFacade.proceedToCheckout ();
        shoppingCartFacade.setIdentity (setIdentityData ());
        Checkout newCheckout = shoppingCartFacade.setShippingAddress (setShippingAddressData ());
        Identity savedIdentity = newCheckout.getCheckoutDetails ().getIdentity ();
        ShippingAddress savedShippingAddress = newCheckout.getCheckoutDetails ().getShippingAddress ();
        assertEquals (1, newCheckout.getOrder ().getOrderItems ().size ());
        assertEquals (NAME, savedIdentity.getName ());
        assertEquals (COUNTRY, savedShippingAddress.getCountry ());
        assertEquals (STREET, savedShippingAddress.getStreet ());
        assertEquals (HOUSE_NUMBER, savedShippingAddress.getHouseNameOrNumber ());
        assertEquals (ZIP, savedShippingAddress.getZip ());
    }

    @Test
    void setShippingAddress_ReturnsBadRequestException() {
        shoppingCartFacade.proceedToCheckout ();
        shoppingCartFacade.setIdentity (setIdentityData ());
        BadRequestException exception = assertThrows (BadRequestException.class, () -> {
            shoppingCartFacade.setShippingAddress (new ShippingAddress ());
        });

        assertEquals (CommonConstants.MESSAGE_BAD_REQUEST, exception.getMessage ());
    }

    @Test
    void setShippingAddress_ReturnsBadRequestException_MissingIdentity() {
        shoppingCartFacade.proceedToCheckout ();
        shoppingCartFacade.setIdentity (setIdentityData ());
        shoppingCartFacade.undo ();
        BadRequestException exception = assertThrows (BadRequestException.class, () -> {
            shoppingCartFacade.setShippingAddress (setShippingAddressData ());
        });

        assertEquals (CommonConstants.MESSAGE_MISSING_IDENTITY_INFORMATION, exception.getMessage ());
    }

    @Test
    void setPaymentDetails() {
        shoppingCartFacade.proceedToCheckout ();
        shoppingCartFacade.setIdentity (setIdentityData ());
        shoppingCartFacade.setShippingAddress (setShippingAddressData ());
        Checkout newCheckout = shoppingCartFacade.setPaymentDetails (setPaymentDetailsData ());
        Identity savedIdentity = newCheckout.getCheckoutDetails ().getIdentity ();
        ShippingAddress savedShippingAddress = newCheckout.getCheckoutDetails ().getShippingAddress ();
        PaymentDetails savedPaymentDetails = newCheckout.getCheckoutDetails ().getPaymentDetails ();
        assertEquals (1, newCheckout.getOrder ().getOrderItems ().size ());
        assertEquals (NAME, savedIdentity.getName ());
        assertEquals (COUNTRY, savedShippingAddress.getCountry ());
        assertEquals (CARD_OWNER, savedPaymentDetails.getCardOwner ());
        assertEquals (CARD_NUMBER, savedPaymentDetails.getCardNumber ());
        assertEquals (EXPIRY_DATE, savedPaymentDetails.getExpiryDate ());
        assertEquals (CVC, savedPaymentDetails.getCvc ());
    }

    @Test
    void setPaymentDetails_ReturnsBadRequestException() {
        shoppingCartFacade.proceedToCheckout ();
        shoppingCartFacade.setIdentity (setIdentityData ());
        shoppingCartFacade.setShippingAddress (setShippingAddressData ());
        BadRequestException exception = assertThrows (BadRequestException.class, () -> {
            shoppingCartFacade.setPaymentDetails (new PaymentDetails ());
        });

        assertEquals (CommonConstants.MESSAGE_BAD_REQUEST, exception.getMessage ());
    }

    @Test
    void setPaymentDetails_ReturnsBadRequestException_MissingIdentity() {
        shoppingCartFacade.proceedToCheckout ();
        BadRequestException exception = assertThrows (BadRequestException.class, () -> {
            shoppingCartFacade.setPaymentDetails (setPaymentDetailsData ());
        });

        assertEquals (CommonConstants.MESSAGE_MISSING_IDENTITY_INFORMATION, exception.getMessage ());
    }

    @Test
    void setPaymentDetails_ReturnsBadRequestException_MissingShippingAddress() {
        shoppingCartFacade.proceedToCheckout ();
        shoppingCartFacade.setIdentity (setIdentityData ());
        BadRequestException exception = assertThrows (BadRequestException.class, () -> {
            shoppingCartFacade.setPaymentDetails (setPaymentDetailsData ());
        });

        assertEquals (CommonConstants.MESSAGE_MISSING_SHIPPING_INFORMATION, exception.getMessage ());
    }

    @Test
    void proceedPayment() {
        shoppingCartFacade.proceedToCheckout ();
        shoppingCartFacade.setIdentity (setIdentityData ());
        shoppingCartFacade.setShippingAddress (setShippingAddressData ());
        shoppingCartFacade.setPaymentDetails (setPaymentDetailsData ());
        Checkout newCheckout = shoppingCartFacade.proceedPayment ();
        Identity savedIdentity = newCheckout.getCheckoutDetails ().getIdentity ();
        ShippingAddress savedShippingAddress = newCheckout.getCheckoutDetails ().getShippingAddress ();
        PaymentDetails savedPaymentDetails = newCheckout.getCheckoutDetails ().getPaymentDetails ();
        assertEquals (1, newCheckout.getOrder ().getOrderItems ().size ());
        assertEquals (NAME, savedIdentity.getName ());
        assertEquals (COUNTRY, savedShippingAddress.getCountry ());
        assertEquals (CARD_OWNER, savedPaymentDetails.getCardOwner ());
        assertTrue (newCheckout.getCheckoutDetails ().isCheckedOut ());
    }

    @Test
    void proceedPayment_ReturnsBadRequestException_MissingPaymentInformation() {
        shoppingCartFacade.proceedToCheckout ();
        shoppingCartFacade.setIdentity (setIdentityData ());
        shoppingCartFacade.setShippingAddress (setShippingAddressData ());
        BadRequestException exception = assertThrows (BadRequestException.class, () -> {
            shoppingCartFacade.proceedPayment ();
        });

        assertEquals (CommonConstants.MESSAGE_MISSING_PAYMENT_INFORMATION, exception.getMessage ());
    }

    @Test
    void proceedPayment_ReturnsBadRequestException_MissingIdentityInformation() {
        shoppingCartFacade.proceedToCheckout ();
        BadRequestException exception = assertThrows (BadRequestException.class, () -> {
            shoppingCartFacade.proceedPayment ();
        });

        assertEquals (CommonConstants.MESSAGE_MISSING_IDENTITY_INFORMATION, exception.getMessage ());
    }

    @Test
    void proceedPayment_ReturnsBadRequestException_MissingSHippingAddress() {
        shoppingCartFacade.proceedToCheckout ();
        shoppingCartFacade.setIdentity (setIdentityData ());
        BadRequestException exception = assertThrows (BadRequestException.class, () -> {
            shoppingCartFacade.proceedPayment ();
        });

        assertEquals (CommonConstants.MESSAGE_MISSING_SHIPPING_INFORMATION, exception.getMessage ());
    }

    @Test
    void undo() {
        // proceedPayment and validate checkout which is saved in in Memento list
        shoppingCartFacade.proceedToCheckout ();
        Checkout newCheckout = shoppingCartFacade.proceedToCheckout ();
        assertEquals (1, newCheckout.getOrder ().getOrderItems ().size ());
        assertNull (checkoutDetails.getIdentity ());
        // add identity and validate new state
        Checkout checkoutWithIdentity = shoppingCartFacade.setIdentity (setIdentityData ());
        Identity savedIdentity = checkoutWithIdentity.getCheckoutDetails ().getIdentity ();
        assertEquals (1, checkoutWithIdentity.getOrder ().getOrderItems ().size ());
        assertEquals (NAME, savedIdentity.getName ());
        assertEquals (LAST_NAME, savedIdentity.getLastName ());
        // undo setIdentity and validate that current state is the same as before
        Checkout undoSetIdentity = shoppingCartFacade.undo ();
        Identity previousIdentity = undoSetIdentity.getCheckoutDetails ().getIdentity ();
        assertEquals (1, undoSetIdentity.getOrder ().getOrderItems ().size ());
        assertNull (previousIdentity);
    }

    @Test
    void getItemWithPrice() {
        ItemWithPrice newItemWithPrice = shoppingCartFacade.getItemWithPrice (item);
        Price newPrice = newItemWithPrice.getPrice ();
        assertEquals (newPrice.getNetPrice (), price.getNetPrice ());
        assertEquals (newPrice.getTax (), price.getTax ());
        assertEquals (newPrice.getGrossPrice (), price.getGrossPrice ());
    }


    @Test
    void getPrice_reducedVatPrice_Fruits() {
        Price fruitPrice = shoppingCartFacade.getPrice (NET_PRICE, Type.FRUIT.toString ());
        assertEquals (NET_PRICE, fruitPrice.getNetPrice ());
        assertEquals (NET_PRICE * REDUCED_VAT, fruitPrice.getTax ());
        assertEquals (NET_PRICE * REDUCED_VAT + NET_PRICE, fruitPrice.getGrossPrice ());
    }

    @Test
    void getPrice_reducedVatPrice_Vegetables() {
        Price fruitPrice = shoppingCartFacade.getPrice (NET_PRICE, Type.VEGETABLE.toString ());
        assertEquals (NET_PRICE, fruitPrice.getNetPrice ());
        assertEquals (NET_PRICE * REDUCED_VAT, fruitPrice.getTax ());
        assertEquals (NET_PRICE * REDUCED_VAT + NET_PRICE, fruitPrice.getGrossPrice ());
    }

    @Test
    void getPrice_reducedVatPrice() {
        Price fruitPrice = shoppingCartFacade.getPrice (NET_PRICE, Type.DRINK.toString ());
        assertEquals (NET_PRICE, fruitPrice.getNetPrice ());
        assertEquals (NET_PRICE * VAT, fruitPrice.getTax ());
        assertEquals (NET_PRICE * VAT + NET_PRICE, fruitPrice.getGrossPrice ());
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