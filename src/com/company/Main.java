package com.company;

import java.io.Console;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	// write your code here
        SpellTree leftTree = new SpellTree();
        SpellTree rightTree = new SpellTree();

        Scanner reader = new Scanner(System.in);
        String leftInput, rightInput;
        boolean castDouble = false;

        while(true)
        {
            //printStatus();

            castDouble = false;
            System.out.println("Enter your moves <L R>: ");
            //hasNext_b = reader.hasNext(".");
            leftInput = reader.next(".").toUpperCase();
            rightInput = reader.next(".").toUpperCase();

            //TODO: This should go in a function - resolveMove or something.

            leftTree.walkTree(Gestures.getGestureByChar(leftInput.charAt(0)),Gestures.getGestureByChar(rightInput.charAt(0)));
            rightTree.walkTree(Gestures.getGestureByChar(rightInput.charAt(0)),Gestures.getGestureByChar(leftInput.charAt(0)));

            int conflict = checkTwoHandedConflict(leftTree.currLocation.spellsCast,rightTree.currLocation.spellsCast);
            if(conflict > 0) //TODO: reversing hands in the example has another conflict. It should not be able to cast!
            {
                if((conflict == 3) && (leftTree.currLocation.spellsCast.size() == 1) && (rightTree.currLocation.spellsCast.size() == 1) && (leftTree.currLocation.spellsCast.get(0) == rightTree.currLocation.spellsCast.get(0)))
                { castDouble = castSpell(leftTree,"both hands",false); }
                else {
                    System.out.println("There is a conflict between both hands!");
                    System.out.println("Do you want to cast a spell on left hand? y/n");
                    leftInput = reader.next(".").toUpperCase();
                    if (leftInput.equals("Y"))
                    {
                        castDouble = castSpell(leftTree,"left hand",false);
                    }
                    castSpell(rightTree, "right hand", castDouble);
                }
            }
            else {
                if (leftTree.currLocation.spellsCast.size() > 0) {
                    //System.out.println("Left hand casts " + SpellTree.spellNames[leftTree.currLocation.spellsCast.get(0)]);
                    castSpell(leftTree, "left hand", false);
                }
                if (rightTree.currLocation.spellsCast.size() > 0) {
                    //System.out.println("Right hand casts " + SpellTree.spellNames[rightTree.currLocation.spellsCast.get(0)]);
                    castSpell(rightTree, "right hand", false);
                }
            }
            //TODO: Only one spell can be cast per gesture. Not the following example:
            // P P W S (invisibility - PPws)
            // W W W S (counter-spell wws).
            // Both cannot be cast!
        }

    }

    private static boolean castSpell(SpellTree tree, String hand, boolean disableDouble)
    {
        Scanner reader = new Scanner(System.in);
        int selectedSpell;
        if(tree.currLocation.spellsCast.size() == 1)
        {
            if(disableDouble == false || SpellTree.isDoubleHandedSpell[tree.currLocation.spellsCast.get(0)])
            {
                System.out.println("Player casts " + SpellTree.spellNames[tree.currLocation.spellsCast.get(0)] + " " + "with " + hand);
                return SpellTree.isDoubleHandedSpell[tree.currLocation.spellsCast.get(0)];
            }
            else
                return false;
        }
        else // TODO: make sure that surrender is always cast!
        {
            System.out.println("Select which spell to cast with " + hand +":");
            for(int spellInd = 0; spellInd < tree.currLocation.spellsCast.size(); spellInd++)
            {
                if(disableDouble == false || SpellTree.isDoubleHandedSpell[tree.currLocation.spellsCast.get(spellInd)])
                System.out.println(spellInd + " " + SpellTree.spellNames[tree.currLocation.spellsCast.get(spellInd)]);
            }
            // TODO: player can still cast disallowed spells! fix this. Will probably need another list...
            selectedSpell = reader.nextInt();
            System.out.println("Player casts " + SpellTree.spellNames[tree.currLocation.spellsCast.get(selectedSpell)] +" " + "with " + hand);
            return SpellTree.isDoubleHandedSpell[tree.currLocation.spellsCast.get(selectedSpell)];
        }
    }

    private static int checkTwoHandedConflict(List<Integer> leftSpells, List<Integer> rightSpells)
    {
        // Checks which hand has cast a double hand spell (think of the binary number)
        // 0 - neither hand
        // 1 - right hand
        // 2 - left hand
        // 3 - both hands
        int output =0;
        if(rightSpells.size() > 0 && leftSpells.size() > 0)
        {
            for(int i = 0; i < leftSpells.size(); i++)
            {
                if(SpellTree.isDoubleHandedSpell[leftSpells.get(i)])
                { output += 1; }
            }
            for(int i = 0; i < rightSpells.size(); i++)
            {
                if(SpellTree.isDoubleHandedSpell[rightSpells.get(i)])
                { output += 2; }
            }
        }
        return output;
    }
}
