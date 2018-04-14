package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Player extends Entity{

    private static Scanner reader = new Scanner(System.in);

    private List<Gestures> lastGesturesLeft; // This should be up to length 8
    private List<Gestures> lastGesturesRight; // This should be up to length 8
    private SpellTree leftTree = new SpellTree();
    private SpellTree rightTree = new SpellTree();

    public Player(int id)
    {
        super(15,id,"Player "+id);
        lastGesturesLeft  = new ArrayList<Gestures>(8);
        lastGesturesRight = new ArrayList<Gestures>(8);
    }


    public void getPlayerInput(PlayerMove[] playerMoves, List<Entity> targetList)
    {
        String leftInput, rightInput;
        Gestures leftGest, rightGest;

        //Get player input
        System.out.println("Enter your moves <L R>: ");
        leftInput = reader.next(".").toUpperCase();
        rightInput = reader.next(".").toUpperCase();
        leftGest = Gestures.getGestureByChar(leftInput.charAt(0));
        rightGest = Gestures.getGestureByChar(rightInput.charAt(0));

        //Add to last gestures list
        addLastGestures(leftGest,rightGest);

        // Do not allow two stabs
        if((rightGest == Gestures.stab) && (leftGest == Gestures.stab))
        {
            System.err.println("A wizard only has 1 dagger");
            rightGest = Gestures.nothing;
        }

        leftTree.walkTree(leftGest,rightGest);
        rightTree.walkTree(rightGest,leftGest);


        //In case of multiple completed spell, select spell and handle conflicts
        Hand conflict = checkTwoHandedConflict(leftTree.currLocation.spellsCast,rightTree.currLocation.spellsCast);
        if(conflict == null) {
            // no conflicts! Each casts 1-handed spell
            selectSpell(leftTree,playerMoves[Hand.left.handIndex],targetList);
            selectSpell(rightTree,playerMoves[Hand.right.handIndex],targetList);
            playerMoves[Hand.both.handIndex].spellIndex = -1;
        }
        else if(conflict == Hand.both)
        {
            // Both hands may cast a double-handed spell
            //According to the spell list, this can only mean both hands are casting the same spell together.
            playerMoves[Hand.left.handIndex].spellIndex = -1;
            playerMoves[Hand.right.handIndex].spellIndex = -1;
            selectSpell(rightTree,playerMoves[Hand.both.handIndex],targetList);
        }
        else
        {
            // One hand can cast a double spell. We must chose between hands.
            System.out.println("Both hands have completed a spell, but "+conflict+ " must use both hands. You must choose:" );
            System.out.println("Case the spell on left hand? y/n");
            leftInput = reader.next(".").toUpperCase();
            if (leftInput.equals("Y")) // cast the spell on left
            {
                selectSpell(leftTree,playerMoves[Hand.left.handIndex],targetList);
                playerMoves[Hand.right.handIndex].spellIndex = -1;
            }
            else
            {
                playerMoves[Hand.left.handIndex].spellIndex = -1;
                selectSpell(rightTree,playerMoves[Hand.right.handIndex],targetList);
            }
            playerMoves[Hand.both.handIndex].spellIndex = -1;
        }

        //NOTE: Only one spell can be cast per gesture. Not the following example:
        // P P W S (invisibility - PPws)
        // W W W S (counter-spell wws).
        // Both cannot be cast!

    }

    private void selectSpell(SpellTree tree, PlayerMove playerMove, List<Entity> targetList)
    {
        int selectedSpell;

        if(tree.currLocation.spellsCast.size() == 0) //No spells available
        {
            playerMove.spellIndex = -1;
            return;
        }

        if(tree.currLocation.spellsCast.size() == 1)
        {
            playerMove.spellIndex = tree.currLocation.spellsCast.get(0);
            playerMove.spellTarget = selectTarget(playerMove.spellIndex,targetList);
        }
        else
        {
            //Note: No need to worry about double-handed spells here.
            //The existing spell tree cannot create a situation where a double and a single spell will be cast at once.
            System.out.println("Select which spell to cast:");
            for(int spellInd = 0; spellInd < tree.currLocation.spellsCast.size(); spellInd++)
            {
                System.out.println(spellInd + " " + SpellLibrary.spellNames[tree.currLocation.spellsCast.get(spellInd)]);
            }
            selectedSpell = reader.nextInt();
            playerMove.spellIndex = tree.currLocation.spellsCast.get(selectedSpell);
            playerMove.spellTarget = selectTarget(playerMove.spellIndex,targetList);
        }
    }

    private int selectTarget(int spellIndex,List<Entity> targetList)
    {
        if(SpellLibrary.requiresTarget[spellIndex])
        {
            System.out.println("Who do you wish to target with "+SpellLibrary.spellNames[spellIndex]+"?");
            for(int i=0; i < targetList.size(); i++)
            {
                System.out.println(i+". "+targetList.get(i).name);
            }
            //TODO: Maybe best to set the target for new monsters as -1 and -2, so that we don't have problems later when trying to resolve moves which summon monsters
            System.out.println(targetList.size()+". Player 1 new monster");
            System.out.println(targetList.size()+1+". Player 2 new monster");
            return reader.nextInt();
        }
        return -1;
    }

    private Hand checkTwoHandedConflict(List<Integer> leftSpells, List<Integer> rightSpells)
    {
        // Checks which hand has cast a double hand spell (think of the binary number)
        Hand output = null;

        if(rightSpells.size() > 0 && leftSpells.size() > 0)
        {
            for(int i = 0; i < leftSpells.size(); i++)
            {
                if(SpellLibrary.isDoubleHandedSpell[leftSpells.get(i)])
                {
                    output = Hand.left;
                    break;
                }
            }
            for(int i = 0; i < rightSpells.size(); i++)
            {
                if(SpellLibrary.isDoubleHandedSpell[rightSpells.get(i)])
                {
                    if(output == Hand.left)
                    {
                        output = Hand.both;
                    }
                    else
                    {
                        output = Hand.right;
                    }
                    break;
                }
            }
        }
        return output;
    }

    public void addLastGestures(Gestures left, Gestures right)
    {
        if(lastGesturesLeft.size() == 8)
            lastGesturesLeft.remove(0);
        lastGesturesLeft.add(left);

        if(lastGesturesRight.size() == 8)
            lastGesturesRight.remove(0);
        lastGesturesRight.add(right);
    }

    public String getMoveString(int Lineindex)
    {
        if(Lineindex < lastGesturesRight.size())
            return lastGesturesLeft.get(Lineindex).gestureChar + " " + lastGesturesRight.get(Lineindex).gestureChar;
        else
            return " ";
    }

    public String getStatusString(int Lineindex)
    {
        switch (Lineindex)
        {
            case 0:
                return "Player" + id + ":";
            case 1:
                return hp + " hp";
            default:
                return " ";
        }
    }
}
