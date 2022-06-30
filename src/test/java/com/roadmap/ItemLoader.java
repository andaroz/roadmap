package com.roadmap;

import com.roadmap.models.Item;
import com.roadmap.models.Type;
import com.roadmap.models.UOM;
import com.roadmap.repositories.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

@Slf4j
@RequiredArgsConstructor
@ContextConfiguration
@Component
public class ItemLoader implements CommandLineRunner {

    private final ItemRepository itemRepository;

    @Override
    public void run(String... args) {
        loadItems();
    }

    private void loadItems() {

        Item apple = itemRepository.save (Item.builder()
                .name ("apple")
                .description ("Red apple")
                .uom (UOM.KG.toString ())
                .image ("default apple image")
                .type (Type.FRUIT.toString ())
                .price (1.5)
                .amountAvailable (3.0)
//                .shoppingCart (null)
                .build ());

        Item pear = itemRepository.save (Item.builder()
                .name ("pear")
                .description ("Pear Conference")
                .uom (UOM.KG.toString ())
                .image ("default pear image")
                .type (Type.FRUIT.toString ())
                .price (1.75)
                .amountAvailable (10.0)
//                .shoppingCart (null)
                .build ());

        Item milk = itemRepository.save (Item.builder()
                .name ("Coconut milk")
                .description ("Coconut milk")
                .uom (UOM.L.toString ())
                .image ("default pear image")
                .type (Type.DRINK.toString ())
                .price (2.00)
                .amountAvailable (7.0)
//                .shoppingCart (null)
                .build ());

        Item cucumber = itemRepository.save (Item.builder()
                .name ("Cucumber")
                .description ("Farmers market cucumber")
                .uom (UOM.KG.toString ())
                .image ("default cucumber image")
                .type (Type.VEGETABLE.toString ())
                .price (2.10)
                .amountAvailable (2.0)
//                .shoppingCart (null)
                .build ());

        log.debug ("Items Loaded: " + itemRepository.findAll ().size ());
    }
}
