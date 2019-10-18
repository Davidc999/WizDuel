package com.company;

public enum Hand {
    left(0), right(1), both(2);

    public int handIndex;
    public static Hand[] HANDS_INDEXED = new Hand[] { left, right, both };

    Hand(int handIndex)
    {
        this.handIndex = handIndex;
    }
}
