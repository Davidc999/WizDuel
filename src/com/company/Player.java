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
    public boolean castShortLightning = false;

    //Data for confusion
    int prevLeftTarget=-1, prevRightTarget =-1;

    public Player(int id)
    {
        super(15,id,"Player "+(id+1));
        lastGesturesLeft  = new ArrayList<Gestures>(8);
        lastGesturesRight = new ArrayList<Gestures>(8);
    }


    public List<PlayerMove> getPlayerInput(List<Entity> targetList)
    {
        String leftInput, rightInput;
        Gestures leftGest, rightGest;

        List<PlayerMove> playerMoves = new ArrayList<>(3);

        //Get player input
        if(this.hasEffect(StatusEffect.amnesia)) { // Handle amnesia
            System.out.println(name + ", you are suffering from "+Main.ANSI_PURPLE+"amnesia"+Main.ANSI_RESET+" and so must repeat the previous gestures!");
            leftGest = lastGesturesLeft.get(lastGesturesLeft.size()-1);
            rightGest = lastGesturesRight.get(lastGesturesRight.size()-1);
        }
        else //Regular input
        {
            if(this.hasEffect(StatusEffect.fear))
            {System.out.println(name + ", enter your moves <L R>. Since you are "+Main.ANSI_PURPLE+"afraid "+Main.ANSI_RESET+"C, D, F or S are not allowed:");}
            else
            {System.out.println(name + ", enter your moves <L R>: ");}

            leftInput = reader.next(".").toUpperCase();
            rightInput = reader.next(".").toUpperCase();
            leftGest = Gestures.getGestureByChar(leftInput.charAt(0));
            rightGest = Gestures.getGestureByChar(rightInput.charAt(0));
        }

        //Handle fear
        if(this.hasEffect(StatusEffect.fear))
        {
            if (Gestures.isBraveGesture(leftGest))
            {
                System.out.println("Your left hand freezes in"+Main.ANSI_PURPLE+" fear "+Main.ANSI_RESET+"and ends up doing nothing!");
                leftGest = Gestures.nothing;
            }
            if(Gestures.isBraveGesture(rightGest))
            {
                System.out.println("Your right hand freezes in"+Main.ANSI_PURPLE+" fear "+Main.ANSI_RESET+"and ends up doing nothing!");
                rightGest = Gestures.nothing;
            }
        }


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


        //In case of multiple completed spell, select spell and handle
        PlayerMove newMove;
        Hand conflict = checkTwoHandedConflict(leftTree.currLocation.spellsCast,rightTree.currLocation.spellsCast);
        if(conflict == null) {
            // no conflicts! Each casts 1-handed spell
            newMove = selectMove(leftTree,targetList,Hand.left,-3);
            if(newMove != null)
            {
                playerMoves.add(newMove);
                prevLeftTarget = newMove.spellTarget;
            }
            newMove = selectMove(rightTree,targetList,Hand.right,-3);
            if(newMove != null) {
                playerMoves.add(newMove);
                prevRightTarget = newMove.spellTarget;
            }

        }
        else if(conflict == Hand.both)
        {
            // Both hands may cast a double-handed spell
            //According to the spell list, this can only mean both hands are casting the same spell together.
            newMove = selectMove(rightTree,targetList,Hand.both,-3);
            if(newMove != null) {
                playerMoves.add(newMove);
                prevRightTarget = newMove.spellTarget;
            }
        }
        else
        {
            // One hand can cast a double spell. We must chose between hands.
            System.out.println("Both hands have completed a spell, but "+conflict+ " must use both hands. You must choose:" );
            System.out.println("Case the spell on left hand? y/n");
            leftInput = reader.next(".").toUpperCase();
            if (leftInput.equals("Y")) // cast the spell on left
            {
                newMove = selectMove(leftTree,targetList,Hand.left,-3);
                if(newMove != null) {
                    playerMoves.add(newMove);
                    prevLeftTarget = newMove.spellTarget;
                }
            }
            else
            {
                newMove = selectMove(rightTree,targetList,Hand.right,-3);
                if(newMove != null) {
                    playerMoves.add(newMove);
                    prevRightTarget = newMove.spellTarget;
                }
            }
        }
        return playerMoves;
        //NOTE: Only one spell can be cast per gesture. Not the following example:
        // P P W S (invisibility - PPws)
        // W W W S (counter-spell wws).
        // Both cannot be cast!
    }

    public List<PlayerMove> handleConfusion(List<Entity> targetList)
    {
        String leftInput, rightInput;
        Gestures prevLeftGest, prevRightGest, newGest, leftGest,rightGest;

        List<PlayerMove> playerMoves = new ArrayList<>(3);

        //Check if confusion is new
        if(getConfusionHand() == -1)
            initConfusion();

        //Undo last move
        prevLeftGest = lastGesturesLeft.remove(lastGesturesLeft.size()-1);
        prevRightGest = lastGesturesRight.remove(lastGesturesRight.size()-1);
        leftTree.walkBack();
        rightTree.walkBack();

        //Confuse one of the gestures
        newGest = Gestures.GESTURES_INDEXED[getConfusionGesture()];
        switch (getConfusionHand()){
            case 0: // left
                leftGest = newGest;
                rightGest = prevRightGest;
                System.out.println("In "+name+"'s "+Main.ANSI_PURPLE+"confusion"+Main.ANSI_RESET+" his left hand switches from "+prevLeftGest+" to "+newGest);
                break;
            case 1: // right
                leftGest = prevLeftGest;
                rightGest = prevRightGest;
                System.out.println("In " + name + "'s "+Main.ANSI_PURPLE+"confusion"+Main.ANSI_RESET+" his right hand switches from "+prevRightGest+" to "+newGest);
                break;
            default: // Cannot happen...
                leftGest = prevLeftGest;
                rightGest = prevRightGest;
                break;
        }

        addLastGestures(leftGest,rightGest);

        leftTree.walkTree(leftGest,rightGest);
        rightTree.walkTree(rightGest,leftGest);

        //In case of multiple completed spell, select spell and handle
        PlayerMove newMove;
        Hand conflict = checkTwoHandedConflict(leftTree.currLocation.spellsCast,rightTree.currLocation.spellsCast);
        if(conflict == null) {
            // no conflicts! Each casts 1-handed spell
            newMove = selectMove(leftTree,targetList,Hand.left,prevLeftTarget);
            if(newMove != null)
            {
                playerMoves.add(newMove);
            }
            newMove = selectMove(rightTree,targetList,Hand.right,prevRightTarget);
            if(newMove != null)
                playerMoves.add(newMove);

        }
        else if(conflict == Hand.both)
        {
            // Both hands may cast a double-handed spell
            //According to the spell list, this can only mean both hands are casting the same spell together.
            newMove = selectMove(rightTree,targetList,Hand.both,prevLeftTarget);
            if(newMove != null)
                playerMoves.add(newMove);
        }
        else
        {
            // One hand can cast a double spell. We must chose between hands.
            System.out.println("Both hands have completed a spell, but "+conflict+ " must use both hands. You must choose:" );
            System.out.println("Case the spell on left hand? y/n");
            leftInput = reader.next(".").toUpperCase();
            if (leftInput.equals("Y")) // cast the spell on left
            {
                newMove = selectMove(leftTree,targetList,Hand.left,prevLeftTarget);
                if(newMove != null)
                    playerMoves.add(newMove);
            }
            else
            {
                newMove = selectMove(rightTree,targetList,Hand.right,prevRightTarget);
                if(newMove != null)
                    playerMoves.add(newMove);
            }
        }
        return playerMoves;
        //NOTE: Only one spell can be cast per gesture. Not the following example:
        // P P W S (invisibility - PPws)
        // W W W S (counter-spell wws).
        // Both cannot be cast!
    }

    public void commandMonsters(List<Entity> targetList)
    {
        Monster currMonster;
        for(int i=2; i<targetList.size();i++)
        {
            currMonster = (Monster)targetList.get(i);
            if(currMonster.owner == id)
            {
                currMonster.target = selectTarget(0,targetList,"Who should "+currMonster.name+" attack?");
            }
        }
    }

    private PlayerMove selectMove(SpellTree tree, List<Entity> targetList, Hand hand, int forcedTarget)
    {
        int selectedSpell;

        if(tree.currLocation.spellsCast.size() == 0) //No spells available
        {
            return null;
        }
        PlayerMove playerMove = new PlayerMove();
        playerMove.hand = hand;
        playerMove.moveMaker= this;
        if(tree.currLocation.spellsCast.size() == 1)
        {
            playerMove.spellIndex = tree.currLocation.spellsCast.get(0);
            if(forcedTarget == -3) //If we don't have a forced target (due to confusion)
                playerMove.spellTarget = selectTarget(playerMove.spellIndex,targetList,"Who do you wish to target with "+SpellLibrary.spellNames[playerMove.spellIndex ]+"?");
            else
                playerMove.spellTarget = forcedTarget;

            if(SpellLibrary.isTargetableMonsterSpell(playerMove.spellIndex))
            {
                playerMove.newMonsterTarget = selectTarget(playerMove.spellIndex,targetList,"You are casting "+SpellLibrary.spellNames[playerMove.spellIndex ]+". Whom would you like him to attack?");
            }

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
            if(forcedTarget == -3) //If we don't have a forced target (due to confusion)
                playerMove.spellTarget = selectTarget(playerMove.spellIndex,targetList,"Who do you wish to target with "+SpellLibrary.spellNames[playerMove.spellIndex ]+"?");
            else
                playerMove.spellTarget = forcedTarget;

            if(SpellLibrary.isTargetableMonsterSpell(playerMove.spellIndex))
            {
                playerMove.newMonsterTarget = selectTarget(playerMove.spellIndex,targetList,"You are casting "+SpellLibrary.spellNames[playerMove.spellIndex ]+". Whom would you like him to attack?");
            }
        }
        return playerMove;
    }

    private int selectTarget(int spellIndex,List<Entity> targetList,String prompt)
    {
        if(SpellLibrary.requiresTarget[spellIndex])
        {
            System.out.println(prompt);
            for(int i=0; i < targetList.size(); i++)
            {
                System.out.println(i+1+". "+targetList.get(i).name);
            }
            //TODO: Maybe best to set the target for new monsters as -1 and -2, so that we don't have problems later when trying to resolve moves which summon monsters
            System.out.println(targetList.size()+1+". Player 1 new monster");
            System.out.println(targetList.size()+2+". Player 2 new monster");
            return reader.nextInt()-1;
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

    public void resetTrees()
    {
        leftTree.resetTree();
        rightTree.resetTree();
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
                return name + ":";
            case 1:
                return hp + " hp";
            default:
                return " ";
        }
    }
}
