package com.roadmap.models;

import com.roadmap.utility.taxCalculation.Price;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
//@Entity
@AllArgsConstructor
public class Order {
//    @Id
//    @Column(name="Id")
//    @GeneratedValue(strategy= GenerationType.AUTO)
//    private Long id;
    private static Order instance = null;
    private HashMap<Long, ItemWithPrice> orderItems;
    private Price totalPrice;


    public Order(){
        this.orderItems = new HashMap<> ();
        this.totalPrice = new Price ();
    }

    public static Order getInstance() {
        if (instance == null) {
            instance = new Order ();
        }
        return instance;
    }
}
