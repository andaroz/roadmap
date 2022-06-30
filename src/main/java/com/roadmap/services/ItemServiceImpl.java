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
public class ItemServiceImpl{


    private final ItemRepository itemRepository;
    private final ConvertEurToGbp convertEurToGbp = new ConvertEurToGbp ();

    public Item getItemById(Long id) {
        Item item = new Item ();
        Optional<Item> optional = itemRepository.findById (id);

        if (optional.isPresent ()){
            item = optional.get ();
        }
        return item;
    }


    public Item getItemById(Long id, String currency) throws IOException {
        Item item = getItemById(id);
        if (currency != null) {
            if (currency.equalsIgnoreCase (CommonConstants.CURRENCY_GBP)){
                return itemWithConvertedPrice(item, currency);
            }
        } else {
            return item;
        }
        return item;
    }

    public List<Item> getAllItems(String currency) {
        List<Item>itemList =  itemRepository.findAll ();
        if (currency == null || currency.equalsIgnoreCase (CommonConstants.CURRENCY_EUR)){
            return itemList;
        } else if (currency.equalsIgnoreCase (CommonConstants.CURRENCY_GBP)){
            return itemListWithConvertedPrices(itemList, currency);
        } else {
            return itemList;
        }
    }

    public List<Item> getAllItemsByType(String type, String currency) {
        List<Item>itemList = itemRepository.getAllItemsByType (type);
        if (currency == null || currency.equalsIgnoreCase (CommonConstants.CURRENCY_EUR)){
            System.out.println (itemList.toString ());
            return itemList;
        } else if (currency.equalsIgnoreCase (CommonConstants.CURRENCY_GBP)){
            return itemListWithConvertedPrices(itemList, currency);
        } else {
            return itemList;
        }
    }

    public void reduceAvailableAmount(double amount, Long id) {
        Item item = getItemById(id);
        double availableAmount = item.getAmountAvailable ();
        if (availableAmount >= amount) {
            double remainingAmount = availableAmount - amount;
            itemRepository.updateAvailableAmount (remainingAmount, id);
        }
    }

    public void increaseAvailableAmount(double amount, Long id){
        Item item = getItemById (id);
        double remainingAmount = item.getAmountAvailable () + amount;
        itemRepository.updateAvailableAmount (remainingAmount, id);
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

    public Item itemWithConvertedPrice(Item item, String currency) throws IOException {
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
