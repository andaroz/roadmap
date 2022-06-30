package com.roadmap.services;

import com.roadmap.config.PropertiesLoader;
import com.roadmap.models.Item;
import com.roadmap.models.Type;
import com.roadmap.repositories.ItemRepository;
import com.roadmap.utility.CommonConstants;
import com.roadmap.utility.currencyConverter.ConvertEurToGbp;
import org.junit.jupiter.api.Assertions;
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

import static org.mockito.Mockito.*;

public class ItemServiceImplTest {

    @Mock
    ItemRepository itemRepository;
    @Mock
    ItemServiceImpl itemService = new ItemServiceImpl (itemRepository);
    Item item;
    List<Item> items = new ArrayList<> ();
    private final ConvertEurToGbp convertEurToGbp = new ConvertEurToGbp ();
    Properties properties = PropertiesLoader.loadProperties (CommonConstants.PROPERTIES_FILE);
    private double eurToGbp = Double.valueOf (properties.getProperty (CommonConstants.PROPERTY_KEY_EUR_TO_GBP));

    public ItemServiceImplTest() throws IOException {
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks (this);
        item = new Item ();
        item.setId (1L);
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
    }

    @Test
    void getItemById() throws Exception {
        when (itemRepository.findById (1L)).thenReturn (Optional.of (item));
        Assertions.assertNotNull (item);
        Assertions.assertEquals (Type.FRUIT.toString (), item.getType ());
        Assertions.assertEquals (1L, item.getId ());
    }

    @Test
    void getItemById1() throws Exception {
        double price = item.getPrice ();
        convertEurToGbp.getGbp (price);
        Double convertedPrice = convertEurToGbp.interpret ();
        item.setPrice (convertedPrice);
        doReturn (item).when (itemService).getItemById (1L, CommonConstants.CURRENCY_GBP);
        Assertions.assertNotNull (item);
        Assertions.assertEquals (1L, item.getId ());
        Assertions.assertEquals (price * eurToGbp, item.getPrice ());
    }

    @Test
    void getAllItems() throws Exception {
        doReturn (items).when (itemService).getAllItems (CommonConstants.CURRENCY_EUR);
        Assertions.assertEquals (2, items.size ());
        Assertions.assertEquals (2.0, items.get (0).getPrice ());
        Assertions.assertEquals (1.0, items.get (1).getPrice ());
    }

    @Test
    void getAllItemsWithConvertedPrices() throws Exception {
        items.forEach (i -> {
            double price = i.getPrice ();
            try {
                convertEurToGbp.getGbp (price);
            } catch (IOException e) {
                e.printStackTrace ();
            }
            Double convertedPrice = convertEurToGbp.interpret ();
            i.setPrice (convertedPrice);
        });
        doReturn (items).when (itemService).getAllItems (CommonConstants.CURRENCY_GBP);
        Assertions.assertEquals (2, items.size ());
        Assertions.assertEquals (2.0 * eurToGbp, items.get (0).getPrice ());
        Assertions.assertEquals (1.0 * eurToGbp, items.get (1).getPrice ());
    }

    @Test
    void getAllItemsByType() {
        List<Item> vegetables = items.stream ()
                .filter (i -> i.getType ().equals (Type.VEGETABLE.toString ()))
                .collect (Collectors.toList ());
        System.out.println (vegetables.toString ());
        doReturn (vegetables).when (itemService).getAllItemsByType (Type.VEGETABLE.toString (), CommonConstants.CURRENCY_EUR);
        Assertions.assertEquals (1, vegetables.size ());
        Assertions.assertEquals (Type.VEGETABLE.toString (), vegetables.get (0).getType ());
    }

    @Test
    void reduceAvailableAmount() {
        double amount = 1.0;
        double availableAmount = item.getAmountAvailable ();
        if (availableAmount >= amount) {
            item.setAmountAvailable (availableAmount - amount);
        }
        doNothing ().when (itemService).reduceAvailableAmount (amount, 1L);
        Assertions.assertEquals (availableAmount - amount, item.getAmountAvailable ());
    }

    @Test
    void increaseAvailableAmount() {
        double amount = 1.0;
        double availableAmount = item.getAmountAvailable ();
        item.setAmountAvailable (availableAmount + amount);
        doNothing ().when (itemService).increaseAvailableAmount (amount, 1L);
        Assertions.assertEquals (availableAmount + amount, item.getAmountAvailable ());
    }

    @Test
    void itemListWithConvertedPrices() throws IOException {
        List<Double> prices = new ArrayList<> ();
        items.forEach (i -> {
            double price = i.getPrice ();
            prices.add (price);
            try {
                convertEurToGbp.getGbp (price);
            } catch (IOException e) {
                e.printStackTrace ();
            }
            Double convertedPrice = convertEurToGbp.interpret ();
            i.setPrice (convertedPrice);
        });

        when (itemService.itemListWithConvertedPrices (items, CommonConstants.CURRENCY_GBP)).thenReturn (items);
        Assertions.assertEquals (prices.get (0) * eurToGbp, items.get (0).getPrice ());
        Assertions.assertEquals (prices.get (1) * eurToGbp, items.get (1).getPrice ());
    }

    @Test
    void itemWithConvertedPrice() throws IOException {
        double price = item.getPrice ();
        convertEurToGbp.getGbp (price);
        Double convertedPrice = convertEurToGbp.interpret ();
        item.setPrice (convertedPrice);
        when (itemService.itemWithConvertedPrice (item, CommonConstants.CURRENCY_GBP)).thenReturn (item);
        Assertions.assertEquals (price * eurToGbp, item.getPrice ());
    }
}