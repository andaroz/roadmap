package com.roadmap.services;

import com.roadmap.config.PropertiesLoader;
import com.roadmap.exceptions.ItemNotFoundException;
import com.roadmap.models.Item;
import com.roadmap.models.Type;
import com.roadmap.repositories.ItemRepository;
import com.roadmap.utility.CommonConstants;
import com.roadmap.utility.currency_converter.ConvertEurToGbp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    private static final Long ID = 1L;
    private Item item;
    private List<Item> items = new ArrayList<> ();
    private final ConvertEurToGbp convertEurToGbp = new ConvertEurToGbp ();
    private ItemServiceImpl itemService;
    private Properties properties = PropertiesLoader.loadProperties (CommonConstants.PROPERTIES_FILE);
    private double eurToGbp = Double.valueOf (properties.getProperty (CommonConstants.PROPERTY_KEY_EUR_TO_GBP));

    private ItemServiceImplTest() throws IOException {
        // empty constructor to add IOException
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks (this);
        item = new Item ();
        item.setId (ID);
        item.setAmountAvailable (2.0);
        item.setPrice (2.0);
        item.setName ("Apple");
        item.setDescription ("Red apple");
        item.setType (Type.FRUIT.toString ());
        items.add (item);

        Item vegetable = new Item ();
        vegetable.setId (2L);
        vegetable.setType (Type.VEGETABLE.toString ());
        vegetable.setName ("Carrot");
        vegetable.setDescription ("Eco carrot");
        vegetable.setPrice (1.0);
        vegetable.setAmountAvailable (4.0);
        items.add (vegetable);

        itemService = new ItemServiceImpl (itemRepository);
    }

    @Test
    void getItemById() {
        when (itemRepository.findById (ID)).thenReturn (Optional.of (item));
        Item newItem = itemService.getItemById (ID);
        assertNotNull (newItem);
        assertEquals (Type.FRUIT.toString (), newItem.getType ());
        assertEquals (ID, newItem.getId ());
    }

    @Test
    void getItemById_throwsItemNotFoundException() {
        Long id = 20L;
        when (itemRepository.findById (id)).thenThrow (new ItemNotFoundException ());
        ItemNotFoundException exception = assertThrows (ItemNotFoundException.class, () -> {
            itemService.getItemById (id);
        });
        assertEquals (CommonConstants.MESSAGE_NOT_FOUND, exception.getMessage ());
    }

    @Test
    void getItemById_withPriceConvertedToGbp() throws Exception {
        Double priceBeforeConvertion = item.getPrice ();
        when (itemRepository.findById (ID)).thenReturn (Optional.of (item));
        Item newItem = itemService.getItemById (ID, CommonConstants.CURRENCY_GBP);
        assertNotNull (newItem);
        assertEquals (ID, newItem.getId ());
        assertEquals (priceBeforeConvertion * eurToGbp, newItem.getPrice ());
    }

    @Test
    void getAllItems() {
        doReturn (items).when (itemRepository).findAll ();
        List<Item> newItemList = itemService.getAllItems (CommonConstants.CURRENCY_EUR);
        assertEquals (2, newItemList.size ());
        assertEquals (2.0, newItemList.get (0).getPrice ());
        assertEquals (1.0, newItemList.get (1).getPrice ());
    }

    @Test
    void getAllItemsWithConvertedPrices() throws Exception {
        doReturn (items).when (itemRepository).findAll ();
        List<Item> newList = itemService.getAllItems (CommonConstants.CURRENCY_GBP);
        assertEquals (2, newList.size ());
        assertEquals (2.0 * eurToGbp, newList.get (0).getPrice ());
        assertEquals (1.0 * eurToGbp, newList.get (1).getPrice ());
    }

    @Test
    void getAllItemsByType() {
        List<Item> vegetables = items.stream ()
                .filter (i -> i.getType ().equals (Type.VEGETABLE.toString ()))
                .collect (Collectors.toList ());
        doReturn (vegetables).when (itemRepository).getAllItemsByType (Type.VEGETABLE.toString ());
        List<Item> vegetableList = itemService.getAllItemsByType (Type.VEGETABLE.toString (), CommonConstants.CURRENCY_EUR);
        assertEquals (1, vegetableList.size ());
        assertEquals (Type.VEGETABLE.toString (), vegetableList.get (0).getType ());
    }

    @Test
    void reduceAvailableAmount() {
        double amount = 1.0;
        double availableAmount = item.getAmountAvailable ();
        doReturn (Optional.of (item)).when (itemRepository).findById (ID);
        doNothing ().when (itemRepository).updateAvailableAmount (amount, ID);
        if (availableAmount >= amount) {
            item.setAmountAvailable (availableAmount - amount);
        }
        itemService.reduceAvailableAmount (amount, ID);
        doReturn (Optional.of (item)).when (itemRepository).findById (ID);
        assertEquals (availableAmount - amount, item.getAmountAvailable ());
    }

    @Test
    void increaseAvailableAmount() {
        double amount = 1.0;
        double availableAmount = item.getAmountAvailable ();
        doReturn (Optional.of (item)).when (itemRepository).findById (ID);
        doNothing ().when (itemRepository).updateAvailableAmount (amount, ID);
        itemService.increaseAvailableAmount (amount, ID);
        item.setAmountAvailable (availableAmount + amount);
        assertEquals (availableAmount + amount, item.getAmountAvailable ());
    }

    @Test
    void itemListWithConvertedPrices() {
        List<Double> prices = new ArrayList<> ();
        items.forEach (i -> {
            double price = i.getPrice ();
            prices.add (price);
        });

        List<Item> newList = itemService.itemListWithConvertedPrices (items, CommonConstants.CURRENCY_GBP);
        assertEquals (prices.get (0) * eurToGbp, newList.get (0).getPrice ());
        assertEquals (prices.get (1) * eurToGbp, newList.get (1).getPrice ());
    }

    @Test
    void itemWithConvertedPrice_WithInvalidCurrency() throws IOException {
        double price = item.getPrice ();
        Item newItem = itemService.itemWithConvertedPrice (item, "USD");
        assertEquals (price, newItem.getPrice ());
    }
}