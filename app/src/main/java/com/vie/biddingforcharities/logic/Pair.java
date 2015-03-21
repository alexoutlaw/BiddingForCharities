package com.vie.biddingforcharities.logic;

public class Pair {
    public String Label;
    public Integer ID;

    public Pair(String label, Integer id) {
        Label = label;
        ID = id;
    }

    @Override
    public String toString() {
        return Label;
    }
}
