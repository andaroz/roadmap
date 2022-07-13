package com.roadmap.shopping_cart_facade;

import com.roadmap.exceptions.BadRequestException;
import com.roadmap.models.*;
import com.roadmap.repositories.ItemRepository;
import com.roadmap.services.ItemServiceImpl;
import com.roadmap.utility.CommonConstants;
import com.roadmap.utility.checkout.CareTaker;
import com.roadmap.utility.checkout.Originator;
import com.roadmap.utility.tax_calculation.FullVatPrice;
import com.roadmap.utility.tax_calculation.Price;
import com.roadmap.utility.tax_calculation.ReducedVatPrice;
import com.roadmap.utility.tax_calculation.TaxCalculator;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

@Log
@Service
public class ShoppingCartFacadeImpl implements ShoppingCartFacade {

    private static final DecimalFormat df = new DecimalFormat (CommonConstants.DECIMAL_FOORMAT_PATTERN);
    private ItemServiceImpl itemService;
    private TaxCalculator taxCalculator;
    private Originator originator = new Originator ();
    private CareTaker careTaker = new CareTaker ();
    private int currentState;
    private Order order;

    public ShoppingCartFacadeImpl(ItemRepository itemRepository, Order order) throws IOException {
        this.itemService = new ItemServiceImpl (itemRepository);
        this.taxCalculator = new TaxCalculator ();
        if (order.getInstance () == null) {
            this.order = new Order ();
        } else {
            this.order = order;
        }
    }

    @Override
    public Order addToOrder(Long itemId, double amount) {
        HashMap<Long, ItemWithPrice> itemsInOrder = order.getOrderItems ();
        Item item = itemService.getItemById (itemId);

        boolean itemAlreadyInTheOrder = false;
        double orderedAmount;

        if (item.getAmountAvailable () < amount) {
            log.info ("There is only " + item.getAmountAvailable () + " for item " + item.getName () + ". Please choose amount which is available!");
        } else {
            if (itemsInOrder.size () > 0) {
                for (Map.Entry<Long, ItemWithPrice> itemListItem : itemsInOrder.entrySet ()) {
                    Long listItemId = itemListItem.getKey ();
                    ItemWithPrice itemWithPrice = itemListItem.getValue ();
                    orderedAmount = itemWithPrice.getAmountOrdered () + amount;
                    if (listItemId.equals (itemId)) {
                        itemWithPrice.setAmountOrdered (orderedAmount);
                        itemsInOrder.put (itemId, itemWithPrice);
                        itemAlreadyInTheOrder = true;
                    }
                }
            }

            if (!itemAlreadyInTheOrder) {
                ItemWithPrice newItemWithPrice = getItemWithPrice (item);
                newItemWithPrice.setAmountOrdered (amount);
                itemsInOrder.put (itemId, newItemWithPrice);
            }

            itemService.reduceAvailableAmount (amount, itemId);
            order.setTotalPrice (calculateTotalPrice (itemsInOrder));
        }
        return order;
    }

    @Override
    public Order getOrder() {
        HashMap<Long, ItemWithPrice> itemsInOrder = order.getOrderItems ();
        order.setTotalPrice (calculateTotalPrice (itemsInOrder));
        return order;
    }

    @Override
    public Order removeFromOrder(Long itemId, double amount) {
        HashMap<Long, ItemWithPrice> itemsInOrder = order.getOrderItems ();
        for (Map.Entry<Long, ItemWithPrice> itemInOrder : itemsInOrder.entrySet ()) {
            Long orderItemId = itemInOrder.getKey ();
            double orderItemAmount = itemInOrder.getValue ().getAmountOrdered ();
            ItemWithPrice itemWithPrice = itemInOrder.getValue ();

            if (orderItemId.equals (itemId)) {
                if (orderItemAmount > amount) {
                    double remainingAmount = orderItemAmount - amount;
                    itemWithPrice.setAmountOrdered (remainingAmount);
                    itemsInOrder.put (itemId, itemWithPrice);
                    itemService.increaseAvailableAmount (amount, itemId);
                } else {
                    itemsInOrder.remove (itemId);
                    itemService.increaseAvailableAmount (orderItemAmount, itemId);
                }
            }
        }
        order.setTotalPrice (calculateTotalPrice (itemsInOrder));
        return order;
    }

    @Override
    public Checkout proceedToCheckout() {
        if (order.getOrderItems ().size () == 0) {
            throw new BadRequestException (CommonConstants.MESSAGE_MISSING_ITEMS_IN_ORDER, true);
        } else {
            originator.proceedToCheckout (order);
            careTaker.addMemento (originator.storeInMemento ());
            currentState = careTaker.getCurrentState ();
            return careTaker.getMemento (currentState).getCheckout ();
        }
    }

    @Override
    public Checkout setIdentity(Identity identity) {
        if (identity == null || identity.getName () == null || identity.getLastName () == null) {
            throw new BadRequestException ();
        } else {
            originator.setIdentity (order, identity);
            careTaker.addMemento (originator.storeInMemento ());
            currentState = careTaker.getCurrentState ();
            return careTaker.getMemento (currentState).getCheckout ();
        }
    }

    @Override
    public Checkout setShippingAddress(ShippingAddress shippingAddress) {
        if (shippingAddress == null || shippingAddress.getCountry () == null || shippingAddress.getStreet () == null ||
                shippingAddress.getHouseNameOrNumber () == null || shippingAddress.getZip () == null) {
            throw new BadRequestException ();
        } else {
            CheckoutDetails previousStateCheckoutDetails = getPreviousStateCheckoutDetails ();
            if (previousStateCheckoutDetails.getIdentity () == null) {
                throw new BadRequestException (CommonConstants.MESSAGE_MISSING_IDENTITY_INFORMATION, true);
            } else {
                originator.setShippingAddress (order, shippingAddress, previousStateCheckoutDetails);
                careTaker.addMemento (originator.storeInMemento ());
                currentState = careTaker.getCurrentState ();
                return careTaker.getMemento (currentState).getCheckout ();
            }
        }
    }

    @Override
    public Checkout setPaymentDetails(PaymentDetails paymentDetails) {
        if (paymentDetails == null || paymentDetails.getCardOwner () == null || paymentDetails.getCardNumber () == null ||
                paymentDetails.getExpiryDate () == null || paymentDetails.getCvc () == null) {
            throw new BadRequestException ();
        } else {
            CheckoutDetails previousStateCheckoutDetails = getPreviousStateCheckoutDetails ();
            if (previousStateCheckoutDetails.getIdentity () == null) {
                throw new BadRequestException (CommonConstants.MESSAGE_MISSING_IDENTITY_INFORMATION, true);
            } else if (previousStateCheckoutDetails.getShippingAddress () == null) {
                throw new BadRequestException (CommonConstants.MESSAGE_MISSING_SHIPPING_INFORMATION, true);
            } else {
                originator.setPaymentDetails (order, paymentDetails, previousStateCheckoutDetails);
                careTaker.addMemento (originator.storeInMemento ());
                currentState = careTaker.getCurrentState ();
                return careTaker.getMemento (currentState).getCheckout ();
            }
        }
    }

    @Override
    public Checkout proceedPayment() {
        CheckoutDetails checkoutDetails = getPreviousStateCheckoutDetails ();
        Identity identity = checkoutDetails.getIdentity ();
        ShippingAddress shippingAddress = checkoutDetails.getShippingAddress ();
        PaymentDetails paymentDetails = checkoutDetails.getPaymentDetails ();
        if (order.getOrderItems () == null) {
            throw new BadRequestException (CommonConstants.MESSAGE_MISSING_ITEMS_IN_ORDER, true);
        } else if (identity == null) {
            throw new BadRequestException (CommonConstants.MESSAGE_MISSING_IDENTITY_INFORMATION, true);
        } else if (shippingAddress == null) {
            throw new BadRequestException (CommonConstants.MESSAGE_MISSING_SHIPPING_INFORMATION, true);
        } else if (paymentDetails == null) {
            throw new BadRequestException (CommonConstants.MESSAGE_MISSING_PAYMENT_INFORMATION, true);
        } else {
            originator.proceedPayment (order, checkoutDetails);
            careTaker.addMemento (originator.storeInMemento ());
            currentState = careTaker.getCurrentState ();
        }

        int mementoListSize = careTaker.getSavedStates ().size ();
        if (mementoListSize > 1) {
            while (mementoListSize != 1) {
                careTaker.getSavedStates ().remove (0);
                mementoListSize--;
            }
        }

        return careTaker.getMemento (mementoListSize - 1).getCheckout ();
    }

    @Override
    public Checkout undo() {
        careTaker.undo ();
        currentState = careTaker.getCurrentState ();
        return careTaker.getMemento (currentState).getCheckout ();
    }

    public ItemWithPrice getItemWithPrice(Item item) {
        ItemWithPrice itemWithPrice = new ItemWithPrice ();
        itemWithPrice.setId (item.getId ());
        itemWithPrice.setName (item.getName ());
        itemWithPrice.setDescription (item.getDescription ());
        itemWithPrice.setImage (item.getImage ());
        itemWithPrice.setUom (item.getUom ());
        itemWithPrice.setPrice (getPrice (item.getPrice (), item.getType ()));
        return itemWithPrice;
    }

    public Price getPrice(Double netPrice, String type) {
        Price price = new Price (netPrice);
        if (type.toUpperCase ().equalsIgnoreCase (Type.FRUIT.toString ()) ||
                type.toUpperCase ().equalsIgnoreCase (Type.VEGETABLE.toString ())) {
            ReducedVatPrice reducedVatPrice = new ReducedVatPrice (price);
            price = reducedVatPrice.accept (taxCalculator);
        } else {
            FullVatPrice fullVatPrice = new FullVatPrice (price);
            price = fullVatPrice.accept (taxCalculator);
        }
        return price;
    }

    public Price calculateTotalPrice(Map<Long, ItemWithPrice> itemsInOrder) {
        Price totalPrice = new Price ();
        double totalNetPrice = 0.00;
        double totalVat = 0.00;
        double totalGrossPrice = 0.00;
        if (itemsInOrder != null && itemsInOrder.size () > 0) {
            for (Map.Entry<Long, ItemWithPrice> itemListItem : itemsInOrder.entrySet ()) {
                ItemWithPrice itemWithPrice = itemListItem.getValue ();
                double amount = itemListItem.getValue ().getAmountOrdered ();
                Price priceOfItem = itemWithPrice.getPrice ();
                totalNetPrice += (priceOfItem.getNetPrice () * amount);
                totalVat += (priceOfItem.getTax () * amount);
                totalGrossPrice += (priceOfItem.getGrossPrice () * amount);
            }
            totalPrice.setNetPrice (Double.valueOf (df.format (totalNetPrice)));
            totalPrice.setTax (Double.valueOf (df.format (totalVat)));
            totalPrice.setGrossPrice (Double.valueOf (df.format (totalGrossPrice)));
        }
        return totalPrice;
    }

    public CheckoutDetails getPreviousStateCheckoutDetails() {
        return careTaker.getMemento (careTaker.getCurrentState ()).getCheckout ().getCheckoutDetails ();
    }
}
