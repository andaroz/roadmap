package com.roadmap.models;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Item {

    @Id
    @Column(name="Id")
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String uom;
    private String image;
    private String type;
    private Double price;
    private Double amountAvailable = 0.0;
//    @OneToOne
//    private ShoppingCart shoppingCart;

    @Builder
    public Item(Long id, String name, String description, String uom, String image,
                String type, Double price, Double amountAvailable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.uom = uom;
        this.image = image;
        this.type = type;
        this.price = price;
        this.amountAvailable = amountAvailable;
//        this.shoppingCart = shoppingCart;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", uom='" + uom + '\'' +
                ", image='" + image + '\'' +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", amountAvailable=" + amountAvailable +
                '}';
    }
}
