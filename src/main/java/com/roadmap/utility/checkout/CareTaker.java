package com.roadmap.utility.checkout;

import java.util.ArrayList;
import java.util.List;

public class CareTaker {

    private List<Memento> savedStates;
    private int currentState = 0;

    public CareTaker() {
        this.savedStates = new ArrayList<> ();
    }

    public int getCurrentState() {
        return currentState;
    }

    public List<Memento> getSavedStates() {
        return savedStates;
    }

    public void addMemento(Memento memento) {
        savedStates.add (memento);
        currentState = savedStates.size () - 1;
    }

    public Memento getMemento(int index) {
        return savedStates.get (index);
    }

    public void undo() {
        if (currentState <= 0) {
            currentState = 0;
        } else {
            currentState--;
        }
    }
}
