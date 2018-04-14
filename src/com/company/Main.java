package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Main {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void main(String[] args) {

        // Instantiate  player 1
        Player player1 = new Player(1);
        List<PlayerMove> player1Moves;

        // Instantiate  player 2
        Player player2 = new Player(2);
        List<PlayerMove> player2Moves;

        //Instantiate target list
        List<Entity> entities = new ArrayList<>();
        entities.add(player1);
        entities.add(player2);

        while (true) {
            player1.updateStatusEffects();
            player2.updateStatusEffects();
            printStatus(player1, player2);
            player1Moves = player1.getPlayerInput(entities);
            player2Moves = player2.getPlayerInput(entities);

            //Handle confusion
            if(player1.hasEffect(StatusEffect.confusion))
                player1Moves = player1.handleConfusion(entities);
            if(player2.hasEffect(StatusEffect.confusion))
                player2Moves = player2.handleConfusion(entities);
            resolveMoves(player1Moves, player2Moves, entities);
        }

    }

    private static void printStatus(Player player1, Player player2) {
        String leftAlignFormat = "| %-9s | %-3s | %-9s | %-3s |%n";

        System.out.format("+-----------+-----+-----------+-----+%n");
        for (int index = 0; index < 8; index++) {
            System.out.format(leftAlignFormat, player1.getStatusString(index), player1.getMoveString(index), player2.getStatusString(index), player2.getMoveString(index));
        }
        System.out.format("+-----------+-----+-----------+-----+%n");
    }

    private static void resolveMoves(List<PlayerMove> player1Moves, List<PlayerMove> player2Moves, List<Entity> targets) {
        //TODO: Since spells must be resolved in some odd order, might be best to add them into a priority queue at this point and then send them on to some other function for resolution.
        PriorityQueue<PlayerMove> spellsQueue = new PriorityQueue<>();
        final String[] handDescription = {"left hand", "Right hand", "Both hands"};

        //Add player 1 moves to queue
        for (int i = 0; i < player1Moves.size(); i++) {
            spellsQueue.add(player1Moves.get(i));
        }

        //Add player 2 moves to queue
        for (int i = 0; i < player2Moves.size(); i++) {
            spellsQueue.add(player2Moves.get(i));
        }

        //Resolve moves by priority
        for (PlayerMove playerMove : spellsQueue)
            castSpellatTarget(playerMove, targets);
    }

    private static void castSpellatTarget(PlayerMove playerMove, List<Entity> targets) {
        boolean successfullyCast;
        describeSpell(playerMove, targets);
        successfullyCast = checkObjections(playerMove, targets);
        if (successfullyCast) {
            applySpell(playerMove,targets);
        }
    }

    private static void describeSpell(PlayerMove playerMove, List<Entity> targets) {
        switch (playerMove.spellIndex) {
            case -1:
                return;
            case 0: // Dispel Magic
                System.out.println("Player " + playerMove.playerID + " casts " + ANSI_BLUE + "dispel magic" + ANSI_RESET + " with his " + playerMove.hand + " hand...");
                break;
            case 27: //anti-spell
                System.out.println("Player " + playerMove.playerID + " casts" + ANSI_BLUE + " "+SpellLibrary.spellNames[playerMove.spellIndex] + ANSI_RESET + " at " + targets.get(playerMove.spellTarget).name + " with his " + playerMove.hand + " hand.");
                break;
            case 2: // Magic mirror
                System.out.println("Player " + playerMove.playerID + " casts a" + ANSI_YELLOW + " magic mirror" + ANSI_RESET + " with " + playerMove.hand + " hands...");
                break;
            case 3: /*Long lightning bolt*/
                System.out.println("Player " + playerMove.playerID + " fires a" + ANSI_RED + " lightning bolt" + ANSI_RESET + " at " + targets.get(playerMove.spellTarget).name + " with his " + playerMove.hand + " hand...");
                break;
            case 6: /*Amnesia*/ case 7: /*Confusion*/ case 31: /*Fear*/
                System.out.println("Player " + playerMove.playerID + " casts" + ANSI_PURPLE + " "+SpellLibrary.spellNames[playerMove.spellIndex] + ANSI_RESET + " at " + targets.get(playerMove.spellTarget).name + " with his " + playerMove.hand + " hand...");
                break;
            case 16: // shield
                System.out.println("Player " + playerMove.playerID + " casts a" + ANSI_YELLOW + " shield" + ANSI_RESET + " with his " + playerMove.hand + " hand...");
                break;
            case 17: //surrender
                System.out.println("Player " + playerMove.playerID + ANSI_GREEN + " Surrenders!" + ANSI_RESET);
                System.out.println("The game is over! Player " + (3 - playerMove.playerID) + " is the winner!");
                System.exit(0);
                break;
            case 25: /*Missile */ case 15: /* Fireball */
                System.out.println("Player " + playerMove.playerID + " launches a " + ANSI_RED + SpellLibrary.spellNames[playerMove.spellIndex] + ANSI_RESET + " at " + targets.get(playerMove.spellTarget).name + " with his " + playerMove.hand + " hand...");
                break;
            case 33: /* short lightning bolt - cast only once!*/
                System.out.println("Player " + playerMove.playerID + " fires a" + ANSI_RED + " lightning bolt" + ANSI_RESET + " at " + targets.get(playerMove.spellTarget).name + " with a clap...");
                break;
            case 34: /*Cause light wounds*/ case 36: /*Cause heavy wounds*/
                System.out.println("Player " + playerMove.playerID + " casts " + ANSI_RED + SpellLibrary.spellNames[playerMove.spellIndex] + ANSI_RESET + " at " + targets.get(playerMove.spellTarget).name + " with his " + playerMove.hand + " hand...");
                break;
            case 4: /*cure heavy wounds*/  case 5: /*cure light wounds*/
                System.out.println("Player " + playerMove.playerID + " casts " + ANSI_GREEN + SpellLibrary.spellNames[playerMove.spellIndex] + ANSI_RESET + " at " + targets.get(playerMove.spellTarget).name + " with his " + playerMove.hand + " hand...");
                break;
            case 42: // stab
                System.out.println("Player " + playerMove.playerID + " attempts to" + ANSI_RED + " stab" + ANSI_RESET + " at " + targets.get(playerMove.spellTarget).name + " with his " + playerMove.hand + " hand...");
                break;
            default:
                System.out.println(ANSI_RED + "Spell not yet implemented" + ANSI_RESET);
        }
    }

    private static boolean checkObjections(PlayerMove playerMove, List<Entity> targets) {
        Entity spellTarget = targets.get(playerMove.spellTarget);
        // Short lightning bolt - cast only once!
        if(playerMove.spellIndex == 33) {
            Player castingPlayer = (Player) targets.get(playerMove.playerID - 1);
            if (!castingPlayer.castShortLightning) // If this version has not yet been cast
            {
                castingPlayer.castShortLightning = true;
                return true;
            }
            else {
                System.out.println("Player " + castingPlayer.id + " has already cast this spell. The spell fizzles uselessly.");
                return false;
            }
        }

        // check for dispel magic
        if(targets.get(0).hasEffect(StatusEffect.dispel))
        {
            System.out.println(SpellLibrary.spellNames[playerMove.spellIndex] +" is dispelled!");
            return false;
        }

        //Check for shield
        boolean shieldableSpell = (playerMove.spellIndex == 25) || (playerMove.spellIndex == 42);
        if(spellTarget.hasEffect(StatusEffect.shielded) &&(shieldableSpell) )
        {
            System.out.println(spellTarget.name + " is " + ANSI_YELLOW + "shielded" + ANSI_RESET + " and takes no damage.");
            return false;
        }

        //Check for magic mirror
        if(spellTarget.hasEffect(StatusEffect.magic_mirror) &&(SpellLibrary.reflectable[playerMove.spellIndex]))
        {
            System.out.println(SpellLibrary.spellNames[playerMove.spellIndex] + " is " + ANSI_YELLOW + "reflected" + ANSI_RESET + " by the magic mirror!");
            playerMove.spellTarget = playerMove.playerID-1;
            applySpell(playerMove,targets);
            return false;
        }

        // Check for conflicting status: confusion, anmesia, charm person, charm monster, paralysis or fear
        if(SpellLibrary.isStatusEffectSpell(playerMove.spellIndex) && ((spellTarget.hasConflictingStatusEffect()) ))
        {
            System.out.println(SpellLibrary.spellNames[playerMove.spellIndex] + " is " + ANSI_YELLOW + "cancelled out" + ANSI_RESET + " by other " +ANSI_PURPLE+"enchantments"+ANSI_RESET);
            spellTarget.addEffect(StatusEffect.conflicting_status);
            return false;
        }

        return true;
    }

    private static void applySpell(PlayerMove playerMove, List<Entity> targets){
        switch (playerMove.spellIndex) {
            case -1:
                return;
            case 0: // Dispel Magic
                System.out.println(targets.get(playerMove.spellTarget).name + " is" + ANSI_YELLOW + " shielded." + ANSI_RESET +" All enchantments are removed, all magic spells fail!");
                //TODO: dispel monsters once they are implemented!
                for(int i=0; i<targets.size(); i++)
                {
                    targets.get(i).clearEffects();
                    targets.get(i).addEffect(StatusEffect.dispel);
                }
                targets.get(playerMove.spellTarget).addEffect(StatusEffect.shielded);
                break;
            case 2: // Magic mirror
                System.out.println(targets.get(playerMove.spellTarget).name + " is" + ANSI_YELLOW + " reflecting magic." + ANSI_RESET);
                targets.get(playerMove.spellTarget).addEffect(StatusEffect.magic_mirror);
                break;
            case 3: // Long lightning bolt
                System.out.println(targets.get(playerMove.spellTarget).name + " is zapped for " + ANSI_RED + "5 damage." + ANSI_RESET);
                targets.get(playerMove.spellTarget).dealDamage(5);
                break;
            case 4: //cure heavy wounds
                System.out.println(targets.get(playerMove.spellTarget).name + " is healed for " + ANSI_GREEN + "2 damage." + ANSI_RESET);
                targets.get(playerMove.spellTarget).heal(2);
                break;
            case 5: //cure light wounds
                System.out.println(targets.get(playerMove.spellTarget).name + " is healed for " + ANSI_GREEN + "1 damage." + ANSI_RESET);
                targets.get(playerMove.spellTarget).heal(1);
                break;
            case 6: // Amnesia
                System.out.println(targets.get(playerMove.spellTarget).name+ " has" + ANSI_PURPLE + " forgotten" + ANSI_RESET + " the magic gestures!");
                targets.get(playerMove.spellTarget).addEffect(StatusEffect.amnesia);
                break;
            case 7: // Confusion
                System.out.println(targets.get(playerMove.spellTarget).name+ " looks a bit" + ANSI_PURPLE + " confused!" + ANSI_RESET);
                targets.get(playerMove.spellTarget).addEffect(StatusEffect.confusion);
                break;
            case 15: //fireball
                System.out.println(targets.get(playerMove.spellTarget).name + " is burned for " + ANSI_RED + "5 damage." + ANSI_RESET);
                targets.get(playerMove.spellTarget).dealDamage(5);
                break;
            case 16: // shield
                System.out.println(targets.get(playerMove.spellTarget).name + " is" + ANSI_YELLOW + " shielded." + ANSI_RESET);
                targets.get(playerMove.spellTarget).addEffect(StatusEffect.shielded);
                break;
            case 17: //surrender
                break;
            case 25: //Missile
                    System.out.println(targets.get(playerMove.spellTarget).name + " takes " + ANSI_RED + "1 damage " + ANSI_RESET + "from missile.");
                    targets.get(playerMove.spellTarget).dealDamage(1);
                break;
            case 27: //anti-spell
                Player target = (Player)targets.get(2-playerMove.playerID);
                System.out.println("All of Player "+target.name + "'s previous gestures are " + ANSI_BLUE + "nullified."+ANSI_RESET);
                target.resetTrees();
                break;
            case 31: //Fear
                System.out.println(targets.get(playerMove.spellTarget).name+ " looks" + ANSI_PURPLE + " terrified!" + ANSI_RESET);
                targets.get(playerMove.spellTarget).addEffect(StatusEffect.fear);
                break;
            case 33: // Short lightning bolt - cast only once!
                Player castingPlayer = (Player) targets.get(playerMove.playerID - 1);
                System.out.println(targets.get(playerMove.spellTarget).name + " is zapped for " + ANSI_RED + "5 damage." + ANSI_RESET);
                targets.get(playerMove.spellTarget).dealDamage(5);
                System.out.println("This spell cannot be cast again by Player " + castingPlayer.id);
                castingPlayer.castShortLightning = true;
                break;
            case 34: //Cause light wounds
                System.out.println(targets.get(playerMove.spellTarget).name + " takes " + ANSI_RED + "2 damage " + ANSI_RESET + "from the spell.");
                targets.get(playerMove.spellTarget).dealDamage(2);
                break;
            case 36: //Cause heavy wounds
                System.out.println(targets.get(playerMove.spellTarget).name + " takes " + ANSI_RED + "3 damage " + ANSI_RESET + "from the spell.");
                targets.get(playerMove.spellTarget).dealDamage(3);
                break;
            case 42: // stab
                System.out.println(targets.get(playerMove.spellTarget).name + " is " + ANSI_RED + "stabbed for 1 damage." + ANSI_RESET);
                targets.get(playerMove.spellTarget).dealDamage(1);
                break;
            default:
                break;
        }
    }

}