package com.roadmap.shoppingCartFacade;

import com.roadmap.models.*;
import com.roadmap.repositories.ItemRepository;
import com.roadmap.services.ItemServiceImpl;
import com.roadmap.utility.CommonConstants;
import com.roadmap.utility.checkout.CareTaker;
import com.roadmap.utility.checkout.Originator;
import com.roadmap.utility.taxCalculation.FullVatPrice;
import com.roadmap.utility.taxCalculation.Price;
import com.roadmap.utility.taxCalculation.ReducedVatPrice;
import com.roadmap.utility.taxCalculation.TaxCalculator;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

@Service
public class ShoppingCartFacadeImpl implements ShoppingCartFacade {

    private static final DecimalFormat df = new DecimalFormat (CommonConstants.DECIMAL_FOORMAT_PATTERN);
    private final ItemRepository itemRepository;
    private ItemServiceImpl itemService;
    private TaxCalculator taxCalculator;
    private double totalNetPrice = 0.00;
    private double totalVat = 0.00;
    private double totalGrossPrice = 0.00;
    private Originator originator = new Originator ();
    private CareTaker careTaker = new CareTaker ();
    private int currentCheckoutState = 0;
    private int savedCheckoutStates = 0;
    int currentState;
    private Checkout checkout;
    private CheckoutDetails checkoutDetails = new CheckoutDetails ();

    public ShoppingCartFacadeImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
        this.itemService = new ItemServiceImpl (itemRepository);
        this.taxCalculator = new TaxCalculator ();
    }

    @Override
    public void addToOrder(Long itemId, double amount, Order order) {
        HashMap<Long, ItemWithPrice> itemsInOrder = order.getOrderItems ();
        Item item = itemService.getItemById (itemId);

        boolean itemAlreadyInTheOrder = false;
        double orderedAmount = 0.00;

        if (item.getAmountAvailable () < amount) {
            System.out.println ("There is only " + item.getAmountAvailable () + " for item " + item.getName () + ". Please choose amount which is available!");
        } else {
            if (itemsInOrder.size () > 0) {
                for (Map.Entry<Long, ItemWithPrice> itemListItem : itemsInOrder.entrySet ()) {
                    Long listItemId = itemListItem.getKey ();
                    ItemWithPrice itemWithPrice = itemListItem.getValue ();
                    orderedAmount = itemWithPrice.getAmountOrdered () + amount;
                    itemWithPrice.setAmountOrdered (orderedAmount);
                    if (listItemId.equals (itemId)) {
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

    }

    @Override
    public Order getOrder(Order order) {
        HashMap<Long, ItemWithPrice> itemsInOrder = order.getOrderItems ();
        for (Map.Entry<Long, ItemWithPrice> itemInOrder : itemsInOrder.entrySet ()) {
            Long itemId = itemInOrder.getKey ();
            Item item = itemService.getItemById (itemId);

            if (item != null) {
                ItemWithPrice itemWithPrice = getItemWithPrice (item);
            }
        }
        order.setOrderItems (itemsInOrder);
        order.setTotalPrice (calculateTotalPrice (itemsInOrder));

        return order;
    }

    @Override
    public void removeFromOrder(Long itemId, double amount, Order order) {
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
                } else if (orderItemAmount <= amount) {
                    itemsInOrder.remove (itemId);
                    itemService.increaseAvailableAmount (orderItemAmount, itemId);
                }
            }
        }
        order.setTotalPrice (calculateTotalPrice (itemsInOrder));
    }

    @Override
    public Checkout proceedToCheckout(Order order) {
        originator.proceedToCheckout (order);
        careTaker.addMemento (originator.storeInMemento ());
        currentState = careTaker.getCurrentState ();
        return careTaker.getMemento (currentState).getCheckout ();
    }

    @Override
    public Checkout setIdentity(Identity identity, Order order) {
        originator.setIdentity (order, identity);
        careTaker.addMemento (originator.storeInMemento ());
        System.out.println ("Set Identity Get caretaker list size: " + careTaker.getSavedStates ().size ());
        int listSize = careTaker.getSavedStates ().size ();
        System.out.println ("Set Identity Get last added item in caretaker list: " + careTaker.getMemento (listSize - 1).getCheckout ().toString ());
        currentState = careTaker.getCurrentState ();
        System.out.println ("Set Identity Get current state: " + currentState);
        return careTaker.getMemento (currentState).getCheckout ();
    }

    @Override
    public Checkout setShippingAddress(ShippingAddress shippingAddress, Order order) {
        CheckoutDetails previousStateCheckoutDetails = getPreviousStateCheckoutDetails ();
        originator.setShippingAddress (order, shippingAddress, previousStateCheckoutDetails);
        careTaker.addMemento (originator.storeInMemento ());
        currentState = careTaker.getCurrentState ();
        System.out.println ("Set Shipping address current state: " + currentState);
        System.out.println ("Set Shipping address checkout: " + careTaker.getMemento (currentState).getCheckout ().toString ());
        return careTaker.getMemento (currentState).getCheckout ();
    }

    @Override
    public Checkout setPaymentDetails(PaymentDetails paymentDetails, Order order) {
        CheckoutDetails previousStateCheckoutDetails = getPreviousStateCheckoutDetails ();
        originator.setPaymentDetails (order, paymentDetails, previousStateCheckoutDetails);
        careTaker.addMemento (originator.storeInMemento ());
        currentState = careTaker.getCurrentState ();
        return careTaker.getMemento (currentState).getCheckout ();
    }

    @Override
    public Checkout proceedPayment(Order order) {
        CheckoutDetails checkoutDetails = getPreviousStateCheckoutDetails ();
        originator.proceedPayment (order, checkoutDetails);

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
        int currentState = careTaker.getCurrentState ();
        return careTaker.getMemento (currentState).getCheckout ();
    }

    private ItemWithPrice getItemWithPrice(Item item) {
        ItemWithPrice itemWithPrice = new ItemWithPrice ();
        itemWithPrice.setId (item.getId ());
        itemWithPrice.setName (item.getName ());
        itemWithPrice.setDescription (item.getDescription ());
        itemWithPrice.setImage (item.getImage ());
        itemWithPrice.setUom (item.getUom ());
        itemWithPrice.setPrice (getPrice (item.getPrice (), item.getType ()));
        return itemWithPrice;
    }

    private Price getPrice(Double netPrice, String type) {
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

    private Price calculateTotalPrice(HashMap<Long, ItemWithPrice> itemsInOrder) {
        Price totalPrice = new Price ();
        if (itemsInOrder != null && itemsInOrder.size () > 0) {
            for (Map.Entry<Long, ItemWithPrice> itemListItem : itemsInOrder.entrySet ()) {
                Long itemId = itemListItem.getKey ();
                double amount = itemListItem.getValue ().getAmountOrdered ();
                Item item = itemService.getItemById (itemId);

                if (item != null) {
                    Price priceOfItem = getPrice (item.getPrice (), item.getType ());
                    totalNetPrice = +(priceOfItem.getNetPrice () * amount);
                    totalVat = +(Double.valueOf (df.format (priceOfItem.getTax () * amount)));
                    totalGrossPrice = +(Double.valueOf (df.format (priceOfItem.getGrossPrice () * amount)));
                }
            }
            totalPrice.setNetPrice (totalNetPrice);
            totalPrice.setTax (totalVat);
            totalPrice.setGrossPrice (totalGrossPrice);
        }
        return totalPrice;
    }

    private CheckoutDetails getPreviousStateCheckoutDetails() {
        return careTaker.getMemento (careTaker.getCurrentState ()).getCheckout ().getCheckoutDetails ();
    }
}
