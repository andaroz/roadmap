package com.roadmap.controllers;

import com.roadmap.models.Item;
import com.roadmap.models.Type;
import com.roadmap.services.ItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ShopControllerTest {

    private static final String CURRENCY_EUR = "EUR";

    @Mock
    private ItemServiceImpl itemService;

    @InjectMocks
    private ShopController shopController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks (this);
        mockMvc = MockMvcBuilders.standaloneSetup (shopController).build ();

    }

    @Test
    void getItemsByType() throws Exception {
        List<Item> items = new ArrayList<> ();
        items.add (new Item ());
        items.add (new Item ());

        when (itemService.getAllItemsByType (Type.FRUIT.toString (), CURRENCY_EUR)).thenReturn (items);
        mockMvc.perform (get("/eShop/items?type=FRUIT&currency=EUR"))
                .andExpect (status ().isOk ());
//        for (Item item : items) {
//            Assert.that ();
//        }

    }

    @Test
    void getAllItems() throws Exception {
        List<Item> items = new ArrayList<> ();
        items.add (new Item ());
        items.add (new Item ());

        when (itemService.getAllItems (CURRENCY_EUR)).thenReturn (items);

        mockMvc.perform (get ("/eShop/items?currency=EUR"))
                .andExpect (status ().isOk ());
    }

    @Test
    void getItemById() throws Exception {
        Long id = 1L;
        when(itemService.getItemById (id, CURRENCY_EUR)).thenReturn (new Item ());

        mockMvc.perform (get("/eShop/item?id=1&currency=EUR"))
                .andExpect (status().isOk());
    }

    @Test
    void getItemsByType1() {
    }

    @Test
    void getAllItems1() {
    }

    @Test
    void getItemById1() {
    }
}