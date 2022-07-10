package com.roadmap.controllers;

import com.roadmap.models.Item;
import com.roadmap.services.ItemServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/eShop")
public class ShopController {

    private final ItemServiceImpl itemService;

    public ShopController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @GetMapping(path = "/items/byType{type}{currency}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Item> getItemsByType(@PathParam("type") String type, @PathParam("currency") String currency) {
        return itemService.getAllItemsByType (type, currency);
    }

    @GetMapping(path = "/items{currency}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Item> getAllItems(@PathParam("currency") String currency) {
        return itemService.getAllItems (currency);
    }

    @GetMapping(path = "/item{id}{currency}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getItemById(@PathParam("id") String id, @PathParam("currency") String currency) throws IOException {
        return new ResponseEntity<Item> (itemService.getItemById (Long.parseLong (id), currency), HttpStatus.OK);
    }
}
