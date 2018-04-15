package com.company;

public class PlayerMove implements Comparable<PlayerMove> {
    public int spellIndex;
    public int spellTarget;
    public Player moveMaker;
    public int newMonsterTarget;
    public Hand hand;

    @Override
    public int compareTo(PlayerMove o) {
        return Integer.compare(SpellLibrary.spellPriority[spellIndex], SpellLibrary.spellPriority[o.spellIndex]);

    }
}
