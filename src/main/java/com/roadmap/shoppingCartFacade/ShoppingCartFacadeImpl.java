package com.roadmap.shoppingCartFacade;

import com.roadmap.models.Item;
import com.roadmap.models.ItemWithPrice;
import com.roadmap.models.Order;
import com.roadmap.models.Type;
import com.roadmap.repositories.ItemRepository;
import com.roadmap.services.ItemServiceImpl;
import com.roadmap.utility.CommonConstants;
import com.roadmap.utility.taxCalculation.FullVatPrice;
import com.roadmap.utility.taxCalculation.Price;
import com.roadmap.utility.taxCalculation.ReducedVatPrice;
import com.roadmap.utility.taxCalculation.TaxCalculator;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ShoppingCartFacadeImpl implements ShoppingCartFacade{

    private static final DecimalFormat df = new DecimalFormat (CommonConstants.DECIMAL_FOORMAT_PATTERN);
    private final ItemRepository itemRepository;
    private ItemServiceImpl itemService;
    private TaxCalculator taxCalculator;
    private double totalNetPrice = 0.00;
    private double totalVat = 0.00;
    private double totalGrossPrice = 0.00;

    public ShoppingCartFacadeImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
        this.itemService = new ItemServiceImpl (itemRepository);
        this.taxCalculator = new TaxCalculator();
    }

    @Override
    public void addToOrder(Long itemId, double amount, Order order) {
        HashMap<Long, ItemWithPrice> itemsInOrder = order.getOrderItems ();
        Item item = new Item ();
        Optional<Item> optional = itemRepository.findById (itemId);

        if (optional.isPresent ()){
            item = optional.get ();
        }

        boolean itemAlreadyInTheOrder = false;
        double orderedAmount;

        if (item.getAmountAvailable () < amount) {
            System.out.println ("There is only " + item.getAmountAvailable () + " for item " + item.getName () + ". Please choose amount which is available!");
        }

        if (itemsInOrder.size () > 0) {
            for (Map.Entry<Long, ItemWithPrice> itemListItem : itemsInOrder.entrySet ()){
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
        order.setTotalPrice (calculateTotalPrice (itemsInOrder));
    }

    @Override
    public Order getOrder(Order order) {
        HashMap<Long, ItemWithPrice> itemsInOrder = order.getOrderItems ();
        for (Map.Entry<Long, ItemWithPrice> itemInOrder : itemsInOrder.entrySet ()){
            Long itemId = itemInOrder.getKey ();
            Item item = itemService.getItemById (itemId);

            if (item != null) {
                ItemWithPrice itemWithPrice = getItemWithPrice (item);
            }
        }
        order.setOrderItems (itemsInOrder);
        order.setTotalPrice (calculateTotalPrice(itemsInOrder));

        return order;
    }

    @Override
    public void removeFromOrder(Long itemId, double amount, Order order) {
        HashMap<Long, ItemWithPrice> itemsInOrder = order.getOrderItems ();
        for (Map.Entry<Long, ItemWithPrice> itemInOrder : itemsInOrder.entrySet ()){
            Long orderItemId = itemInOrder.getKey ();
            double orderItemAmount = itemInOrder.getValue ().getAmountOrdered ();
            ItemWithPrice itemWithPrice = itemInOrder.getValue ();

            if (orderItemId.equals (itemId)) {
                if (orderItemAmount > amount) {
                    double reminingAmount = orderItemAmount - amount;
                    itemWithPrice.setAmountOrdered (reminingAmount);
                    itemsInOrder.put (itemId, itemWithPrice);
                } else if (orderItemAmount >= amount) {
                    itemsInOrder.remove (itemId);
                }
            }
        }

        order.setTotalPrice (calculateTotalPrice(itemsInOrder));
    }

    private ItemWithPrice getItemWithPrice(Item item) {
        ItemWithPrice itemWithPrice = new ItemWithPrice ();
        itemWithPrice.setId (item.getId ());
        itemWithPrice.setName (item.getName ());
        itemWithPrice.setDescription (item.getDescription ());
        itemWithPrice.setImage (item.getImage ());
        itemWithPrice.setUom (item.getUom ());
        itemWithPrice.setPrice (getPrice(item.getPrice (), item.getType ()));
        return itemWithPrice;
    }

    private Price getPrice(Double netPrice, String type){
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
            for (Map.Entry<Long, ItemWithPrice> itemListItem : itemsInOrder.entrySet ()){
                Long itemId = itemListItem.getKey ();
                double amount = itemListItem.getValue ().getAmountOrdered ();
                Item item = itemService.getItemById (itemId);

                if (item != null) {
                    Price priceOfItem = getPrice(item.getPrice (), item.getType ());
                    totalNetPrice =+ (priceOfItem.getNetPrice () * amount);
                    totalVat =+ (Double.valueOf (df.format (priceOfItem.getTax () * amount)));
                    totalGrossPrice =+ (Double.valueOf (df.format (priceOfItem.getGrossPrice () * amount)));
                }
            }
            totalPrice.setNetPrice (totalNetPrice);
            totalPrice.setTax (totalVat);
            totalPrice.setGrossPrice (totalGrossPrice);
        }
        return totalPrice;
    }
}
