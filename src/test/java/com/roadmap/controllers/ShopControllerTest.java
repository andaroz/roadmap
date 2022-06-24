package com.roadmap.controllers;

import com.roadmap.config.PropertiesLoader;
import com.roadmap.models.Item;
import com.roadmap.models.Type;
import com.roadmap.services.ItemServiceImpl;
import com.roadmap.utility.CommonConstants;
import com.roadmap.utility.currencyConverter.ConvertEurToGbp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ShopControllerTest {

    @Mock
    private ItemServiceImpl itemService;

    @InjectMocks
    private ShopController shopController;

    private MockMvc mockMvc;
    private final ConvertEurToGbp convertEurToGbp = new ConvertEurToGbp ();
    Properties properties = PropertiesLoader.loadProperties (CommonConstants.PROPERTIES_FILE);
    private double eurToGbp = Double.valueOf (properties.getProperty (CommonConstants.PROPERTY_KEY_EUR_TO_GBP));

    private Item item;
    private List<Item> items = new ArrayList<> ();
    private Long id = 1L;

    ShopControllerTest() throws IOException {
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks (this);
        mockMvc = MockMvcBuilders.standaloneSetup (shopController).build ();
        item = new Item ();
        item.setId (id);
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
    void getAllItems() throws Exception {
        when (itemService.getAllItems (CommonConstants.CURRENCY_EUR)).thenReturn (items);

        mockMvc.perform (get (CommonConstants.BASE_PATH + CommonConstants.ESHOP_PATH + "/items?currency=EUR"))
                .andExpect (status ().isOk ());

        Assertions.assertEquals (2, items.size ());
    }

    @Test
    void getItemsByType() throws Exception {
        List<Item> fruits = items.stream ()
                .filter (i -> i.getType ().equals (Type.FRUIT.toString ()))
                .collect (Collectors.toList ());
        doReturn (fruits).when (itemService).getAllItemsByType (Type.FRUIT.toString (), CommonConstants.CURRENCY_EUR);
        mockMvc.perform (get(CommonConstants.BASE_PATH + CommonConstants.ESHOP_PATH + "/items?type=FRUIT&currency=EUR"))
                .andExpect (status ().isOk ());

        Assertions.assertEquals (1, fruits.size ());
        Assertions.assertEquals (Type.FRUIT.toString (), fruits.get (0).getType ());
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById (id, CommonConstants.CURRENCY_EUR)).thenReturn (item);

        mockMvc.perform (get(CommonConstants.BASE_PATH + CommonConstants.ESHOP_PATH + "/item?id=1&currency=EUR"))
                .andExpect (status().isOk());
    }

    @Test
    void getItemsByTypeWithConvertedPrice() throws Exception{
        double price = item.getPrice ();
        convertEurToGbp.getGbp (price);
        Double convertedPrice = convertEurToGbp.interpret ();
        item.setPrice (convertedPrice);
        when (itemService.itemWithConvertedPrice (item, CommonConstants.CURRENCY_GBP)).thenReturn (item);
        Assertions.assertEquals (price * eurToGbp, item.getPrice ());
    }
}