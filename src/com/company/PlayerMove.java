package com.company;

public class PlayerMove implements Comparable<PlayerMove> {
    public int spellIndex;
    public int spellTarget;
    public int playerID;
    public Hand hand;

    @Override
    public int compareTo(PlayerMove o) {
        if(SpellLibrary.spellPriority[spellIndex] == SpellLibrary.spellPriority[o.spellIndex])
            return 0;
        if(SpellLibrary.spellPriority[spellIndex] > SpellLibrary.spellPriority[o.spellIndex])
            return 1;
        if(SpellLibrary.spellPriority[spellIndex] < SpellLibrary.spellPriority[o.spellIndex])
            return -1;

        return 0;
    }
}
