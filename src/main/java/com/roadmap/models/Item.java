package com.roadmap.models;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Item {

    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String uom;
    private String image;
    private String type;
    private Double price;
    private Double amountAvailable = 0.0;

    @Builder
    public Item(String name, String description, String uom, String image,
                String type, Double price, Double amountAvailable) {
        this.name = name;
        this.description = description;
        this.uom = uom;
        this.image = image;
        this.type = type;
        this.price = price;
        this.amountAvailable = amountAvailable;
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
