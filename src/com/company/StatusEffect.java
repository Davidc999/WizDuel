package com.company;

import java.util.Random;

public enum StatusEffect {

    shielded(1), magic_mirror(1), counter_spell(1), amnesia(2),confusion(2),conflicting_status(1), fear(2),paralyzed(2),charmed(2), remove_enchantment(1);

    private int duration;
    private int confusion_hand =-1, confusion_gesture =-1;
    private int paralyzed_handIndex;

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

    public void initParalysis(int paralyzed_handIndex)
    {
        this.paralyzed_handIndex = paralyzed_handIndex;
    }

    public void changeDuration(int duration)
    {
        this.duration = duration;
    }

    public int getConfusion_hand()
    {
        return confusion_hand;
    }

    public int getParalyzed_handIndex()
    {
        return paralyzed_handIndex;
    }

    public int getConfusion_gesture()
    {
        return confusion_gesture;
    }

    public static boolean isConflictingStatusEffect(StatusEffect status)
    {
        return (status == StatusEffect.fear) || (status == StatusEffect.amnesia) ||
                (status == StatusEffect.confusion) || (status == StatusEffect.conflicting_status) ||
                (status == StatusEffect.paralyzed) || (status == StatusEffect.charmed);
    }

}
