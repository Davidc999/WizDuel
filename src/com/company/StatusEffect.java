package com.company;

import java.util.Random;

public enum StatusEffect {

    shielded(1), magic_mirror(1),dispel(1), amnesia(2),confusion(2),conflicting_status(1), fear(2),paralyzed(2),charmed(2);

    private int duration;
    private int confusion_hand =-1, confusion_gesture =-1;

    StatusEffect(int duration)
    {
        this.duration = duration;
    }

    public int updateDuration()
    {
        duration--;
        return duration;
    }

    public void initConfusion()
    {
        Random rand = new Random();
        confusion_hand = rand.nextInt(2);
        confusion_gesture = rand.nextInt(6);
    }

    public int getConfusion_hand()
    {
        return confusion_hand;
    }

    public int getConfusion_gesture()
    {
        return confusion_gesture;
    }

}
