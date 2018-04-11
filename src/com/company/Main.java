package com.company;

import java.io.Console;
import java.util.ArrayList;
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

        Player player1 = new Player(1);
        Player player2 = new Player(2);


        while(true)
        {
            //printStatus();
            String leftAlignFormat = "| %-9s | %-3s | %-9s | %-3s |%n";
            System.out.format("+-----------+-----+-----------+-----+%n");
            //System.out.format(leftAlignFormat,"Player 1:",player1.getMoveString(0),"Player 2:",Gestures.stab.gestureChar + " " +Gestures.S.gestureChar);
            //System.out.format(leftAlignFormat,"30 hp",player1.getMoveString(1),"15 hp",Gestures.P.gestureChar + " " +Gestures.D.gestureChar);
            for(int index=0; index < 8; index++)
            {
                System.out.format(leftAlignFormat,player1.getStatusString(index),player1.getMoveString(index),player2.getStatusString(index),player2.getMoveString(index));
            }
            System.out.format("+-----------+-----+-----------+-----+%n");
            castDouble = false;
            System.out.println("Enter your moves <L R>: ");

            leftInput = reader.next(".").toUpperCase();
            rightInput = reader.next(".").toUpperCase();

            player1.addGestures(Gestures.getGestureByChar(leftInput.charAt(0)),Gestures.getGestureByChar(rightInput.charAt(0)));

            //TODO: This should go in a function - resolveMove or something.

            leftTree.walkTree(Gestures.getGestureByChar(leftInput.charAt(0)),Gestures.getGestureByChar(rightInput.charAt(0)));
            rightTree.walkTree(Gestures.getGestureByChar(rightInput.charAt(0)),Gestures.getGestureByChar(leftInput.charAt(0)));

            int conflict = checkTwoHandedConflict(leftTree.currLocation.spellsCast,rightTree.currLocation.spellsCast);
            if(conflict > 0)
            {
                if((conflict == 3) && (leftTree.currLocation.spellsCast.size() == 1) && (rightTree.currLocation.spellsCast.size() == 1) && (leftTree.currLocation.spellsCast.get(0).equals(rightTree.currLocation.spellsCast.get(0))))
                { //In case both hands can only cast one spell, and it is the same spell
                    castDouble = castSpell(leftTree,"both hands",false);
                }
                else {
                    System.out.println("There is a conflict between both hands!");
                    System.out.println("Do you want to cast a spell on left hand? y/n");
                    leftInput = reader.next(".").toUpperCase();
                    if (leftInput.equals("Y"))
                    {
                        castDouble = castSpell(leftTree,"left hand",false);
                        if(!castDouble) { //If left hand used a double gesture, right hand cannot cast anything.
                                          //If left hand used a single gesture, right hand can also cast single gesture
                            castSpell(rightTree, "right hand", true);
                        }
                    }
                    else {
                        castSpell(rightTree, "right hand", false);
                    }
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
            if(disableDouble == false || !SpellTree.isDoubleHandedSpell[tree.currLocation.spellsCast.get(0)])
            {
                System.out.println("Player casts " + SpellTree.spellNames[tree.currLocation.spellsCast.get(0)] + " " + "with " + hand);
                return SpellTree.isDoubleHandedSpell[tree.currLocation.spellsCast.get(0)];
            }
            else
                return false;
        }
        else
        {
            //Note: No need to worry about double-handed spells here.
            //The existing spell tree cannot create a situation where a double and a single spell will be cast at once.
            System.out.println("Select which spell to cast with " + hand +":");
            for(int spellInd = 0; spellInd < tree.currLocation.spellsCast.size(); spellInd++)
            {
                System.out.println(spellInd + " " + SpellTree.spellNames[tree.currLocation.spellsCast.get(spellInd)]);
            }
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
