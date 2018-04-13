package com.company;

public enum Hand {
    left(0), right(1), both(2);

    int handIndex;

    Hand(int handIndex)
    {
        this.handIndex = handIndex;
    }
}
