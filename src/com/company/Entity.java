package com.company;

import java.util.ArrayList;
import java.util.List;

public class Entity {

int hp, id;
final int initialHp;
public String name;
private List<StatusEffect> statusEffects;

public Entity()
{
    initialHp = 0;
}

public Entity(int hp, int id, String name)
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

public boolean hasConflictingStatusEffect()
{
    return statusEffects.contains(StatusEffect.fear) || statusEffects.contains(StatusEffect.amnesia) ||
            statusEffects.contains(StatusEffect.confusion) || statusEffects.contains(StatusEffect.conflicting_status) ||
            statusEffects.contains(StatusEffect.paralyzed) || statusEffects.contains(StatusEffect.charmed);
}

public void addEffect(StatusEffect effect)
{
    if(!hasEffect(effect))
        statusEffects.add(effect);
}

public void removeEffect(StatusEffect effect)
{
    statusEffects.remove(effect);
}

public void clearEffects()
{
    statusEffects.clear();
}

public void updateStatusEffects()
{
    for (int i=0; i<statusEffects.size(); i++)
    {
        StatusEffect currEffect = statusEffects.get(i);
        if(currEffect.updateDuration() <= 0)
            removeEffect(currEffect);

    }
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
