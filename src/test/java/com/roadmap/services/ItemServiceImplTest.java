package com.roadmap.services;

import com.roadmap.repositories.ItemRepository;
import com.roadmap.utility.currencyConverter.ConvertEurToGbp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

class ItemServiceImplTest {

    private final ConvertEurToGbp convertEurToGbp = new ConvertEurToGbp ();
    private final ItemRepository itemRepository;
    private MockMvc mockMvc;

    ItemServiceImplTest(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }


    @BeforeEach
    void setUp() {
    }

    @Test
    void getItemById() {
    }

    @Test
    void getAllItems() {

    }

    @Test
    void getAllItemsByType() {

    }

    @Test
    void getTotalPrice() {
    }
}