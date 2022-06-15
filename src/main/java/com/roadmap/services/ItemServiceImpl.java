package com.roadmap.services;

import com.roadmap.models.Item;
import com.roadmap.repositories.ItemRepository;
import com.roadmap.utility.CommonConstants;
import com.roadmap.utility.currencyConverter.ConvertEurToGbp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService{


    private final ItemRepository itemRepository;
    private final ConvertEurToGbp convertEurToGbp = new ConvertEurToGbp ();

    public Item getItemById(Long id, String currency) throws IOException {
        Item item = new Item ();
        Optional<Item> optional = itemRepository.findById (id);

        if (optional.isPresent ()){
            item = optional.get ();
        }
        if (currency != null) {
            if (currency.equalsIgnoreCase (CommonConstants.CURRENCY_GBP)){
                return itemWithConvertedPrices(item, currency);
            }
        } else {
            return item;
        }
        return item;
    }

    public Item getItemById(Long id) {
        Item item = itemRepository.getById (id);
        return item;
    }


    public List<Item> getAllItems(String currency) {
        List<Item>itemList =  itemRepository.findAll ();
        if (currency == null || currency.equalsIgnoreCase (CommonConstants.CURRENCY_EUR)){
            System.out.println ("Not converting");
            log.debug ("Get all items: " + itemList.toString ());
            return itemList;
        } else if (currency.equalsIgnoreCase (CommonConstants.CURRENCY_GBP)){
            return itemListWithConvertedPrices(itemList, currency);
        } else {
            return itemList;
        }
    }

    public List<Item> getAllItemsByType(String type, String currency) {

        List<Item>itemList = itemRepository.getAllItemsByType (type);
        log.debug ("Getting items......");
        if (currency == null || currency.equalsIgnoreCase (CommonConstants.CURRENCY_EUR)){
            System.out.println (itemList.toString ());
            log.info ("Get all items by type: "+itemList.toString ());
            return itemList;
        } else if (currency.equalsIgnoreCase (CommonConstants.CURRENCY_GBP)){
            return itemListWithConvertedPrices(itemList, currency);
        } else {
            return itemList;
        }
    }


    public Double getTotalPrice(Item item) {

        Double  totalPrice= 0.00;
        return totalPrice;
    }

    public List<Item> itemListWithConvertedPrices(List<Item> itemList, String currency) {
        switch (currency){
            case(CommonConstants.CURRENCY_GBP):
            itemList.forEach (item -> {
                try {
                    convertEurToGbp.getGbp (item.getPrice ());
                } catch (IOException e) {
                    e.printStackTrace ();
                }
                Double convertedPrice = convertEurToGbp.interpret ();
                item.setPrice (convertedPrice);
            });
            break;
        }
        return itemList;
    }

    public Item itemWithConvertedPrices(Item item, String currency) throws IOException {
        switch (currency){
        case(CommonConstants.CURRENCY_GBP):
                convertEurToGbp.getGbp (item.getPrice ());
                Double convertedPrice = convertEurToGbp.interpret ();
                item.setPrice (convertedPrice);
            break;
        }
        return item;
    }

}
