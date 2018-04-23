package com.company;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Entity {

int hp, id;
private final int initialHp;
public String name;
private List<StatusEffect> statusEffects;
Monster newMonster;


Entity(int hp, int id, String name)
{
    this.id = id;
    this.name = name;
    this.hp = hp;
    initialHp = hp;
    statusEffects = new ArrayList<>();
}

public boolean hasEffect(StatusEffect effect)
{
    return statusEffects.contains(effect);
}

public boolean hasConflictingStatusEffect(StatusEffect newStatus)
{
    for (StatusEffect effect: statusEffects) {
        if(StatusEffect.isConflictingStatusEffect(effect) && effect != newStatus)
        {
            return true;
        }
    }
    return false;
}

public void addStatusEffect(StatusEffect effect)
{
    if(!hasEffect(effect))
        statusEffects.add(effect);
}

public void removeStatusEffect(StatusEffect effect)
{
    statusEffects.remove(effect);
}

public void clearStatusEffects()
{
    statusEffects.clear();
}

public void updateStatusEffects()
{
    StatusEffect currEffect;
    for(Iterator<StatusEffect> iter = statusEffects.iterator();iter.hasNext();)
    {
        currEffect = iter.next();
        if(currEffect.updateDuration() <= 0)
            iter.remove();

    }
    // Also, reset newMonster
    newMonster = null;
}

public void initConfusion()
{
    int confusionIndex = statusEffects.indexOf(StatusEffect.confusion);
    statusEffects.get(confusionIndex).initConfusion();
}

public int getConfusionHand()
{
    int confusionIndex = statusEffects.indexOf(StatusEffect.confusion);
    return statusEffects.get(confusionIndex).getConfusion_hand();
}

public int getParalyzedHandIndex()
{
    int paralysisIndex = statusEffects.indexOf(StatusEffect.paralyzed);
    return statusEffects.get(paralysisIndex).getParalyzed_handIndex();
}


public int getConfusionGesture()
{
    int confusionIndex = statusEffects.indexOf(StatusEffect.confusion);
    return statusEffects.get(confusionIndex).getConfusion_gesture();
}


public void dealDamage(int damage)
{
    hp -= damage;
}
public void heal(int healVal)
{
    hp = Math.min(initialHp,hp + healVal);
}

}
