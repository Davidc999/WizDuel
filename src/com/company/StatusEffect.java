package com.company;

public enum StatusEffect {

    shielded(1);

    private int duration;

    StatusEffect(int duration)
    {
        this.duration = duration;
    }

    public int updateDuration()
    {
        duration--;
        return duration;
    }

}
