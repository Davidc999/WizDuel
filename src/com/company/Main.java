package com.company;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	// write your code here
        SpellTree leftTree = new SpellTree();
        SpellTree rightTree = new SpellTree();

        Scanner reader = new Scanner(System.in);
        String leftInput, rightInput;
        boolean hasNext_b;

        while(true)
        {
            //printStatus();
            System.out.println("Enter your moves <L R>: ");
            //hasNext_b = reader.hasNext(".");
            leftInput = reader.next(".").toUpperCase();
            rightInput = reader.next(".").toUpperCase();

            //TODO: This should go in a function - resolveMove or something.

            leftTree.walkTree(Gestures.getGestureByChar(leftInput.charAt(0)),Gestures.getGestureByChar(rightInput.charAt(0)));
            rightTree.walkTree(Gestures.getGestureByChar(rightInput.charAt(0)),Gestures.getGestureByChar(leftInput.charAt(0)));

            if(checkTwoHandedConflict(leftTree.currLocation.spellsCast,rightTree.currLocation.spellsCast))
            { System.out.println("There is a conflict between both hands"); }
            if(leftTree.currLocation.spellsCast.size() >0 )
            {
                System.out.println("Left hand casts " + SpellTree.spellNames[leftTree.currLocation.spellsCast.get(0)]);
            }
            if(rightTree.currLocation.spellsCast.size() >0 )
            {
                System.out.println("Right hand casts " + SpellTree.spellNames[rightTree.currLocation.spellsCast.get(0)]);
            }
            //TODO: Only one spell can be cast per gesture. Not the following example:
            // P P W S (invisibility - PPws)
            // W W W S (counter-spell wws).
            // Both cannot be cast!
        }

    }

    private static boolean checkTwoHandedConflict(List<Integer> leftSpells, List<Integer> rightSpells)
    {
        if(rightSpells.size() > 0 && leftSpells.size() > 0)
        {
            for(int i = 0; i < leftSpells.size(); i++)
            {
                if(SpellTree.isDoubleHandedSpell[leftSpells.get(i)])
                { return true; }
            }
            for(int i = 0; i < rightSpells.size(); i++)
            {
                if(SpellTree.isDoubleHandedSpell[rightSpells.get(i)])
                { return true; }
            }
        }
        return false;
    }
}
