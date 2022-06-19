package com.roadmap.utility.checkout;

import com.roadmap.models.Checkout;

public class Memento {

    private Checkout checkout;

    public Memento (Checkout newCheckout) {this.checkout = newCheckout;}

    public Checkout getCheckout(){return checkout;}
}
