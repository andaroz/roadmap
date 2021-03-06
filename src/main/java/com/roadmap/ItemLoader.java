package com.roadmap;

import com.roadmap.models.Item;
import com.roadmap.models.Type;
import com.roadmap.models.UOM;
import com.roadmap.repositories.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ItemLoader implements CommandLineRunner {

    private final ItemRepository itemRepository;

    @Override
    public void run(String... args) {
        loadItems ();
    }

    private void loadItems() {

        itemRepository.save (Item.builder ()
                .name ("apple")
                .description ("Red apple")
                .uom (UOM.KG.toString ())
                .image ("default apple image")
                .type (Type.FRUIT.toString ())
                .price (1.5)
                .amountAvailable (3.0)
                .build ());

        itemRepository.save (Item.builder ()
                .name ("pear")
                .description ("Pear Conference")
                .uom (UOM.KG.toString ())
                .image ("default pear image")
                .type (Type.FRUIT.toString ())
                .price (1.75)
                .amountAvailable (10.0)
                .build ());

        itemRepository.save (Item.builder ()
                .name ("Coconut milk")
                .description ("Coconut milk")
                .uom (UOM.L.toString ())
                .image ("default pear image")
                .type (Type.DRINK.toString ())
                .price (2.00)
                .amountAvailable (7.0)
                .build ());

        itemRepository.save (Item.builder ()
                .name ("Cucumber")
                .description ("Farmers market cucumber")
                .uom (UOM.KG.toString ())
                .image ("default cucumber image")
                .type (Type.VEGETABLE.toString ())
                .price (2.10)
                .amountAvailable (2.0)
                .build ());

        log.info ("Items Loaded: " + itemRepository.findAll ().size ());
    }
}
