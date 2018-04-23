package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Player extends Entity{

    private static Scanner reader = new Scanner(System.in);

    private List<Gestures>[] lastGestures = new List[2]; // This should be up to length 8
    private SpellTree[] spellTrees = new SpellTree[2];
    public boolean castShortLightning = false;

    //Data for confusion
    private int[] prevTargets = {-1,-1}; //prevTargets[Hand.left.handIndex]=-1, prevTargets[Hand.right.handIndex] =-1;

    Player(int id)
    {
        super(15,id,"Player "+(id+1));
        spellTrees[Hand.left.handIndex] = new SpellTree();
        spellTrees[Hand.right.handIndex] = new SpellTree();
        lastGestures[Hand.left.handIndex]  = new ArrayList<Gestures>(8);
        lastGestures[Hand.right.handIndex] = new ArrayList<Gestures>(8);
    }


    public List<PlayerMove> getPlayerMoves(List<Entity> targetList)
    {
        String leftInput, rightInput;
        Gestures leftGest, rightGest;
        int paralyzedHandIndex;

        List<PlayerMove> playerMoves = new ArrayList<>(3);

        //Get player input
        if(this.hasEffect(StatusEffect.amnesia)) { // Handle amnesia
            System.out.println(name + ", you are suffering from "+Main.ANSI_PURPLE+"amnesia"+Main.ANSI_RESET+" and so must repeat the previous gestures!");
            leftGest = lastGestures[Hand.left.handIndex].get(lastGestures[Hand.left.handIndex].size()-1);
            rightGest = lastGestures[Hand.right.handIndex].get(lastGestures[Hand.right.handIndex].size()-1);
        }
        else if(this.hasEffect(StatusEffect.paralyzed)) { // Handle paralysis
            paralyzedHandIndex = getParalyzedHandIndex();
            leftGest = lastGestures[Hand.left.handIndex].get(lastGestures[Hand.left.handIndex].size()-1);
            rightGest = lastGestures[Hand.right.handIndex].get(lastGestures[Hand.right.handIndex].size()-1);
            System.out.println(name + ", your " + Hand.HANDS_INDEXED[paralyzedHandIndex] + " hand is "+Main.ANSI_PURPLE+"Paralyzed."+Main.ANSI_RESET+" Enter a single gesture for the other hand:");
            if(paralyzedHandIndex == Hand.left.handIndex) // paralyze left hand, get right
            {
                rightInput = reader.next(".").toUpperCase();
                rightGest = Gestures.getGestureByChar(rightInput.charAt(0));
                leftGest = Gestures.paralyzeGesture(leftGest);
                System.out.println(name + ", your " + Hand.HANDS_INDEXED[paralyzedHandIndex] + " hand is "+Main.ANSI_PURPLE+"frozen"+Main.ANSI_RESET+" in a " + leftGest + " gesture.");
            }
            else // paralyze right hand, get left
            {
                leftInput = reader.next(".").toUpperCase();
                leftGest = Gestures.getGestureByChar(leftInput.charAt(0));
                rightGest = Gestures.paralyzeGesture(rightGest);
                System.out.println(name + ", your " + Hand.HANDS_INDEXED[paralyzedHandIndex] + " hand is "+Main.ANSI_PURPLE+"frozen"+Main.ANSI_RESET+" in a " + rightGest + " gesture.");
            }
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

        playerMoves = handleGestures(targetList,leftGest,rightGest,false);
        return playerMoves;
    }

    public int getParalyzedHandIndexInput(String targetName)
    {
        System.out.println(name + " you have succesfully cast paralyze at " + targetName + ". Select which hand to paralyze: ");
        System.out.println("1. Left");
        System.out.println("2. Right");
        return reader.nextInt() - 1;
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
        prevLeftGest = lastGestures[Hand.left.handIndex].remove(lastGestures[Hand.left.handIndex].size()-1);
        prevRightGest = lastGestures[Hand.right.handIndex].remove(lastGestures[Hand.right.handIndex].size()-1);
        spellTrees[Hand.left.handIndex].walkBack();
        spellTrees[Hand.right.handIndex].walkBack();

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

        playerMoves = handleGestures(targetList,leftGest,rightGest,true);
        return playerMoves;
    }

    private List<PlayerMove> handleGestures(List<Entity> targetList,Gestures leftGest, Gestures rightGest, boolean usePreviousTarget) {

        List<PlayerMove> playerMoves = new ArrayList<>(3);
        String leftInput;

        //Add to last gestures list
        addLastGestures(leftGest, rightGest);

        // Do not allow two stabs
        if ((rightGest == Gestures.stab) && (leftGest == Gestures.stab)) {
            System.err.println("A wizard only has 1 dagger");
            rightGest = Gestures.nothing;
        }

        spellTrees[Hand.left.handIndex].walkTree(leftGest, rightGest);
        spellTrees[Hand.right.handIndex].walkTree(rightGest, leftGest);


        //In case of multiple completed spell, select spell and handle
        PlayerMove newMove;
        Hand conflict = checkTwoHandedConflict(spellTrees[Hand.left.handIndex].currLocation.spellsCast, spellTrees[Hand.right.handIndex].currLocation.spellsCast);
        if (conflict == null) {
            // no conflicts! Each casts 1-handed spell
            newMove = selectMove(targetList, Hand.left, usePreviousTarget);
            if (newMove != null) {
                playerMoves.add(newMove);
                prevTargets[Hand.left.handIndex] = newMove.spellTarget;
            }
            newMove = selectMove(targetList, Hand.right, usePreviousTarget);
            if (newMove != null) {
                playerMoves.add(newMove);
                prevTargets[Hand.right.handIndex] = newMove.spellTarget;
            }

        } else if (conflict == Hand.both) {
            // Both hands may cast a double-handed spell
            //According to the spell list, this can only mean both hands are casting the same spell together.
            newMove = selectMove(targetList, Hand.both, usePreviousTarget);
            if (newMove != null) {
                playerMoves.add(newMove);
                prevTargets[Hand.right.handIndex] = newMove.spellTarget;
            }
        } else {
            // One hand can cast a double spell. We must chose between hands.
            System.out.println("Both hands have completed a spell, but " + conflict + " must use both hands. You must choose:");
            System.out.println("Case the spell on left hand? y/n");
            leftInput = reader.next(".").toUpperCase();
            if (leftInput.equals("Y")) // cast the spell on left
            {
                newMove = selectMove(targetList, Hand.left, usePreviousTarget);
                if (newMove != null) {
                    playerMoves.add(newMove);
                    prevTargets[Hand.left.handIndex] = newMove.spellTarget;
                }
            } else {
                newMove = selectMove(targetList, Hand.right, usePreviousTarget);
                if (newMove != null) {
                    playerMoves.add(newMove);
                    prevTargets[Hand.right.handIndex] = newMove.spellTarget;
                }
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

    private PlayerMove selectMove(List<Entity> targetList, Hand hand, boolean forceTarget)
    {
        int selectedSpell;

        if(spellTrees[hand.handIndex].currLocation.spellsCast.size() == 0) //No spells available
        {
            return null;
        }
        PlayerMove playerMove = new PlayerMove();
        playerMove.hand = hand;
        playerMove.moveMaker= this;
        if(spellTrees[hand.handIndex].currLocation.spellsCast.size() == 1)
        {
            playerMove.spellIndex = spellTrees[hand.handIndex].currLocation.spellsCast.get(0);
            if(forceTarget) // If we have a forced target (due to confusion)
                playerMove.spellTarget = prevTargets[hand.handIndex];
            else
                playerMove.spellTarget = selectTarget(playerMove.spellIndex,targetList,"Who do you wish to target with "+SpellLibrary.spellNames[playerMove.spellIndex ]+"?");

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
            for(int spellInd = 0; spellInd < spellTrees[hand.handIndex].currLocation.spellsCast.size(); spellInd++)
            {
                System.out.println(spellInd + " " + SpellLibrary.spellNames[spellTrees[hand.handIndex].currLocation.spellsCast.get(spellInd)]);
            }
            selectedSpell = reader.nextInt();
            playerMove.spellIndex = spellTrees[hand.handIndex].currLocation.spellsCast.get(selectedSpell);
            if(forceTarget) //If we have a forced target (due to confusion)
                playerMove.spellTarget = prevTargets[hand.handIndex];
            else
                playerMove.spellTarget = selectTarget(playerMove.spellIndex,targetList,"Who do you wish to target with "+SpellLibrary.spellNames[playerMove.spellIndex ]+"?");


            if(SpellLibrary.isTargetableMonsterSpell(playerMove.spellIndex))
            {
                playerMove.newMonsterTarget = selectTarget(playerMove.spellIndex,targetList,"You are casting "+SpellLibrary.spellNames[playerMove.spellIndex ]+". Whom would you like him to attack?");
            }
        }
        return playerMove;
    }

    private int selectTarget(int spellIndex,List<Entity> targetList,String prompt)
    {
        int selection;
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
            selection =  reader.nextInt();
            if(selection > targetList.size())
                return targetList.size() -selection; //return -1 for player 1 new monster, and -2 for player 2 new monster.
            else
                return selection -1;
        }
        return -3;
    }

    private Hand checkTwoHandedConflict(List<Integer> leftSpells, List<Integer> rightSpells)
    {
        // Checks which hand has cast a double hand spell (think of the binary number)
        Hand output = null;

        if(rightSpells.size() > 0 && leftSpells.size() > 0)
        {
            for (Integer leftSpell : leftSpells) {
                if (SpellLibrary.isDoubleHandedSpell[leftSpell]) {
                    output = Hand.left;
                    break;
                }
            }
            for (Integer rightSpell : rightSpells) {
                if (SpellLibrary.isDoubleHandedSpell[rightSpell]) {
                    if (output == Hand.left) {
                        output = Hand.both;
                    } else {
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
        if(lastGestures[Hand.left.handIndex].size() == 8)
            lastGestures[Hand.left.handIndex].remove(0);
        lastGestures[Hand.left.handIndex].add(left);

        if(lastGestures[Hand.right.handIndex].size() == 8)
            lastGestures[Hand.right.handIndex].remove(0);
        lastGestures[Hand.right.handIndex].add(right);
    }

    public void resetTrees()
    {
        spellTrees[Hand.left.handIndex].resetTree();
        spellTrees[Hand.right.handIndex].resetTree();
    }

    public String getMoveString(int Lineindex)
    {
        if(Lineindex < lastGestures[Hand.right.handIndex].size())
            return lastGestures[Hand.left.handIndex].get(Lineindex).gestureChar + " " + lastGestures[Hand.right.handIndex].get(Lineindex).gestureChar;
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
