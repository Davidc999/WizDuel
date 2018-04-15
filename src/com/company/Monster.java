package com.company;

public class Monster extends Entity {

    private static int totalMonstersSummoned =0;
    int target, owner, attackDmg;

    Monster(int hp, int owner, int target, String name)
    {
        super(hp,totalMonstersSummoned+1,name+" "+(totalMonstersSummoned+1));
        this.attackDmg = hp;
        this.owner = owner;
        this.target = target;
        totalMonstersSummoned++;
    }

    public void setTarget(int target)
    {
        this.target = target;
    }

}
